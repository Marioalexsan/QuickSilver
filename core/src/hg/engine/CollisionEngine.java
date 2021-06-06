package hg.engine;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.drawables.ColliderDrawable;
import hg.drawables.DrawLayer;
import hg.game.HgGame;
import hg.interfaces.IPolygon;
import hg.physics.*;
import hg.utils.Angle;
import hg.utils.BadCoderException;

import java.util.*;


// https://www.sitepoint.com/implement-javas-equals-method-correctly/


/** CollisionEngine handles collision between Collider objects. This includes movement and attack collisions. */
public class CollisionEngine {
    private static final Comparator<RaycastHit> RaycastHitComparator = (o1, o2) -> {
        float delta = o1.distance - o2.distance;
        return delta > 0 ? 1 : (delta < 0 ? -1 : 0);
    };

    /** Int Pairs are used as keys for the spatial hashmap */
    private static class IntPair {
        int x;
        int y;

        public IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() { return Objects.hash(x, y); }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null) return false;
            if (getClass() != other.getClass()) return false;
            return x == ((IntPair)other).x && y == ((IntPair)other).y;
        }
    }

    /** Collider Pairs are used for remembering which colliders have interacted already */
    private static class ColliderPair {
        Collider A;
        Collider B;

        public ColliderPair(Collider A, Collider B) {
            this.A = A;
            this.B = B;
        }

        @Override
        public int hashCode() { return A.hashCode() + B.hashCode(); }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            return A == ((ColliderPair)other).A && B == ((ColliderPair)other).B || B == ((ColliderPair)other).A && A == ((ColliderPair)other).B;
        }
    }

    private static class ColliderEngineData {
        Collider collider;
        Vector2 lastCenterPos;

        public ColliderEngineData(Collider collider) {
            this.collider = collider;
        }

        @Override
        public int hashCode() { return collider.hashCode(); }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            return collider == ((ColliderEngineData)other).collider;
        }
    }

    public static final float CELL_SIZE = 200f;

    private boolean debugDrawEnabled = false;

    private final HashSet<ColliderEngineData> colliderLibrary = new HashSet<>();
    private final HashMap<IntPair, HashSet<ColliderEngineData>> spatialHashMap = new HashMap<>();

    private final ColliderDrawable colliderVision = new ColliderDrawable();
    private final BasicText colliderCount = new BasicText();

    public CollisionEngine() {
        colliderCount.setPosition(new Vector2(-880, 480));
        colliderCount.setLayer(DrawLayer.GUIDefault);
        colliderCount.setCameraUse(false);
    }

    public void setDebugDraw(boolean drawColliders) {
        this.debugDrawEnabled = drawColliders;
    }

    public boolean getDebugDraw() {
        return debugDrawEnabled;
    }

    public void resolveCollisions(boolean movement, boolean combat) {
        if (!(movement || combat)) return;

        updateSpatialHashmap();

        HashSet<ColliderPair> checkedPairs = new HashSet<>();

        for (ColliderEngineData AData : colliderLibrary) {
            Collider A = AData.collider;

            if (!A.isActive()) continue;
            if (A.isHeavy() && A.attackStats == null && A.baseStats == null) continue;

            ArrayList<ColliderEngineData> sortedCandidates = getCollisionCandidates(AData);
            for (ColliderEngineData BData : sortedCandidates) {
                Collider B = BData.collider;

                if (!B.isActive()) continue;
                if (A == B) continue;

                ColliderPair thisPair = new ColliderPair(A, B);
                if (checkedPairs.contains(thisPair)) continue;
                checkedPairs.add(thisPair);

                boolean doMovement = checkMovementPrerequisites(A, B);
                boolean ANotified = checkIfFirstIsNotified(A, B);
                boolean BNotified = checkIfFirstIsNotified(B, A);
                boolean AhurtsB = checkAttackPrerequisites(A, B);
                boolean BhurtsA = checkAttackPrerequisites(B, A);
                if (!(doMovement || AhurtsB || BhurtsA || ANotified || BNotified)) {
                    continue;
                }

                // Mass logic can be improved

                int stepsToUse = 1;

                if (AData.lastCenterPos != null && BData.lastCenterPos != null) {
                    Vector2 AVelocity = new Vector2(A.trueCenter()).sub(AData.lastCenterPos); // How much they moved since last frame
                    Vector2 BVelocity = new Vector2(B.trueCenter()).sub(BData.lastCenterPos);
                    float relativeVel = new Vector2(AVelocity).sub(BVelocity).len();

                    if (relativeVel > 20) stepsToUse = 2;
                    if (relativeVel > 30) stepsToUse = 3;
                    if (relativeVel > 40) stepsToUse = 4;
                    if (relativeVel > 50) stepsToUse = 5;
                }

                SATResults results = SATStepCheckCollision(AData, BData, stepsToUse, doMovement);

                if (results.collision) {

                    if (doMovement) {
                        float APushback = getAPushFactor(A, B);
                        float BPushback = 1f - APushback;

                        if (APushback != 0f) A.getPosition().add(new Vector2(results.mtv).add(results.APositionDelta).scl(-APushback));
                        if (BPushback != 0f) B.getPosition().add(new Vector2(results.mtv).add(results.BPositionDelta).scl(BPushback));

                    }

                    if (ANotified && A.owner != null) A.owner.onGenericCollision(B);
                    if (BNotified && B.owner != null) B.owner.onGenericCollision(A);

                    if (AhurtsB || BhurtsA) {
                        if (A.owner != null) A.owner.onCombatCollision(B);
                        if (B.owner != null) B.owner.onCombatCollision(A);
                    }

                    if (AhurtsB) {
                        if (B.baseStats.owner != null) B.baseStats.owner.onHitByAttack(A.attackStats);
                        if (A.attackStats.owner != null) A.attackStats.owner.onAttackHit(B.baseStats);
                    }
                    if (BhurtsA) {
                        if (A.baseStats.owner != null) A.baseStats.owner.onHitByAttack(B.attackStats);
                        if (B.attackStats.owner != null) B.attackStats.owner.onAttackHit(A.baseStats);
                    }
                }
            }
        }
    }

    private float getAPushFactor(Collider A, Collider B) {
        float AMass = Math.max(A.getMass(), 1);
        float BMass = Math.max(B.getMass(), 1);
        float total = AMass + BMass;

        float APushback = A.isHeavy() ? 0f : (B.isHeavy() ? 1f : BMass / total); // The heavier the other object, the more you push this one
        return APushback;
    }

    private SATResults SATStepCheckCollision(ColliderEngineData AData, ColliderEngineData BData, int steps, boolean simulateAll) {
        if (steps < 1) throw new BadCoderException("Can't do collisions with less than 1 step!");
        if (steps == 1 || AData.lastCenterPos == null || BData.lastCenterPos == null)
            return CollisionAlgorithms.CheckCollisionSAT(AData.collider, BData.collider);

        SATResults results = new SATResults();

        Vector2 ASavedTrueCenter = new Vector2(AData.collider.trueCenter()); // These are restored upon exiting
        Vector2 BSavedTrueCenter = new Vector2(BData.collider.trueCenter());

        Vector2 AStep = new Vector2(ASavedTrueCenter).sub(AData.lastCenterPos).scl(1f / steps); // How much they move each step
        Vector2 BStep = new Vector2(BSavedTrueCenter).sub(BData.lastCenterPos).scl(1f / steps);

        Vector2 ASimulatedPos = new Vector2(AData.lastCenterPos);
        Vector2 BSimulatedPos = new Vector2(BData.lastCenterPos);

        for (int step = 0; step < steps; step++) {
            ASimulatedPos.add(AStep);
            BSimulatedPos.add(BStep);

            AData.collider.getPosition().set(ASimulatedPos); // Sets the temporary collider positions
            BData.collider.getPosition().set(BSimulatedPos);

            // Do collision check as if regular SAT

            SATResults stepResults = CollisionAlgorithms.CheckCollisionSAT(AData.collider, BData.collider);
            if (stepResults.collision) {
                // Write the step deltas
                results.collision = true;

                results.APositionDelta.add(stepResults.mtv);
                results.BPositionDelta.add(stepResults.mtv);

                float APushback = getAPushFactor(AData.collider, BData.collider);
                float BPushback = 1f - APushback;

                ASimulatedPos.add(new Vector2(stepResults.mtv).scl(-APushback));
                BSimulatedPos.add(new Vector2(stepResults.mtv).scl(BPushback));

                if (!simulateAll) break;
            }
        }

        AData.collider.getPosition().set(ASavedTrueCenter); // Restore positions
        BData.collider.getPosition().set(BSavedTrueCenter);

        return results;
    }

    /** This function is used for notifying the interested parties of a collision.
     * For example, bullets fired by players may want to get destroyed if they hit "wall" collider group, but pass through "chain link fence"-like groups while not being pushed back
     */
    public boolean checkIfFirstIsNotified(Collider A, Collider B) {
        // Colliders A gets notified of a collision only if it has NotifyOnly or FullCollision property against B

        ColliderProperty AtoBProperty = A.groupProperties.get(B.group);
        return AtoBProperty == ColliderProperty.Notify || AtoBProperty == ColliderProperty.CollideNotify;
    }

    public boolean checkMovementPrerequisites(Collider A, Collider B) {
        // Heavy colliders do not push other Heavy Colliders.
        // Other colliders get pushed only if both have the property FullCollision against eachother

        return !(A.isHeavy() && B.isHeavy()) &&
                A.groupProperties.get(B.group) == ColliderProperty.CollideNotify &&
                B.groupProperties.get(A.group) == ColliderProperty.CollideNotify;
    }

    public boolean checkAttackPrerequisites(Collider attacker, Collider defender) {
        /* --- List of prerequisites / exclusions for combat ---
         * Do combat check if:
         * - attacker has attackstats and defender has defenderstats
         * - attacker is not also the defender (you can't hurt yourself), unless hurtsDefenderIfOwner is true
         * - attackerstats has targetinggroups that target defender's group type / (if applicable) defenderstats owner
         * - attacker's hitlist does not contain defender (aka wasn't hit previously (however owner may choose to clear hit list to prevent this))
         */

        if (attacker.attackStats == null || defender.baseStats == null) return false;
        if (!attacker.attackStats.hurtsSelf && attacker.attackStats.owner == defender.baseStats.owner) return false;
        if (attacker.attackStats.hitList.contains(defender.baseStats.owner)) return false;

        boolean isTargeted = false;
        for (var target : attacker.attackStats.targetedGroups) {
            switch (target) {
                default -> isTargeted |= target.equals(defender.group);
            }
        }
        return isTargeted;
    }

    public void registerCollider(Collider collider) { colliderLibrary.add(new ColliderEngineData(collider)); }

    public void unregisterCollider(Collider collider) { colliderLibrary.removeIf(data -> data.collider == collider); }

    public void update() {
        if (debugDrawEnabled) {
            colliderVision.registerToEngine();
            colliderVision.setLayer(DrawLayer.Overlay);
            colliderVision.collidersToDraw.clear();
            for (var colData: colliderLibrary) {
                colliderVision.collidersToDraw.add(colData.collider);
            }

            colliderCount.setFont(HgGame.Assets().getFont("Text48"));
            colliderCount.setText("Colliders: " + colliderLibrary.size());
            colliderCount.registerToEngine();
        }
        else {
            colliderVision.unregisterFromEngine();
            colliderCount.unregisterFromEngine();
        }

        resolveCollisions(true, true);
        updateLastPositions();
    }

    private void updateLastPositions() {
        for (var colData: colliderLibrary) {
            colData.lastCenterPos = colData.collider.trueCenter();
        }
    }

    /** Gets the collision candidates for the given collider.
     * @param colData Collider data to request candidates for
     * @return An array of all the colliders that may be interacting with argument
     */
    private ArrayList<ColliderEngineData> getCollisionCandidates(ColliderEngineData colData) {
        Collider collider = colData.collider;

        float[] boundingBox;
        if (collider instanceof IPolygon)
            boundingBox = CollisionAlgorithms.PolyAABB(((IPolygon) collider).trueVertices());
        else if (collider instanceof SphereCollider)
            boundingBox = CollisionAlgorithms.SphereAABB(collider.trueCenter(), ((SphereCollider) collider).getRadius());
        else throw new RuntimeException("Zhis collider type is unknown to zhe man of colliders!");

        int startX = (int) (boundingBox[0] / CELL_SIZE);
        int startY = (int) (boundingBox[1] / CELL_SIZE);
        int endX = (int) (boundingBox[2] / CELL_SIZE);
        int endY = (int) (boundingBox[3] / CELL_SIZE);

        ArrayList<ColliderEngineData> candidates = new ArrayList<>();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                var partial = spatialHashMap.get(new IntPair(x, y));
                if (partial != null) candidates.addAll(partial);
            }
        }
        return candidates;
    }

    /** Rebuilds the spatial hashmap for the current game frame */
    private void updateSpatialHashmap() {
        // We go with the primitive route of rebuilding the spatial hash map on every frame
        // Even without proper updating, it's a massive performance improvement

        spatialHashMap.clear();
        for (var colData: colliderLibrary) {
            Collider collider = colData.collider;

            if (!collider.isActive()) continue;

            float[] boundingBox;
            if (collider instanceof IPolygon)
                boundingBox = CollisionAlgorithms.PolyAABB(((IPolygon) collider).trueVertices());
            else if (collider instanceof SphereCollider)
                boundingBox = CollisionAlgorithms.SphereAABB(collider.trueCenter(), ((SphereCollider) collider).getRadius());
            else throw new RuntimeException("Zhis collider type is unknown to zhe man of colliders!");

            int startX = (int) (boundingBox[0] / CELL_SIZE);
            int startY = (int) (boundingBox[1] / CELL_SIZE);
            int endX = (int) (boundingBox[2] / CELL_SIZE);
            int endY = (int) (boundingBox[3] / CELL_SIZE);

            for (int x = startX; x <= endX; x++)
                for (int y = startY; y <= endY; y++)
                    spatialHashMap.computeIfAbsent(new IntPair(x, y), k -> new HashSet<>()).add(colData);
        }
    }

    /** Raycasts against all collider groups */
    public ArrayList<RaycastHit> doRaycast(Vector2 start, Angle angle, float distance) {
        return doRaycast(start, angle, distance, new HashSet<>(Arrays.asList(ColliderGroup.values())));
    }

    /** Raycasts against the collider groups found in hitThese */
    public ArrayList<RaycastHit> doRaycast(Vector2 start, Angle angle, float distance, HashSet<ColliderGroup> hitThese) {
        Vector2 end = new Vector2(start).add(angle.normalVector().scl(distance));
        ArrayList<RaycastHit> hitList = new ArrayList<>();

        // I should add hashmap candidates instead of searching through whole collider Library
        for (ColliderEngineData colData : colliderLibrary) {
            Collider collider = colData.collider;

            if (!collider.isActive()) continue;
            if (!hitThese.contains(collider.group)) continue;

            RaycastResults results = CollisionAlgorithms.Raycast(start, end, collider);

            if (results.hit) hitList.add(new RaycastHit(results.hitStart, collider));
        }

        hitList.sort(RaycastHitComparator);

        if (debugDrawEnabled) {
            colliderVision.raycastsToDrawOnce.add(new ColliderDrawable.VectorLine(start, end));
        }

        return hitList;
    }
}
