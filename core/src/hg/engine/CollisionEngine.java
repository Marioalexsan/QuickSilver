package hg.engine;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.drawables.ColliderDrawable;
import hg.drawables.DrawLayer;
import hg.game.HgGame;
import hg.interfaces.IPolygon;
import hg.physics.*;
import hg.utils.Angle;

import java.util.*;


// https://www.sitepoint.com/implement-javas-equals-method-correctly/


/**
 * Collection of methods used for collision resolution.
 * The generic algorithm used is SAT.
 */
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

    public static final float CELL_SIZE = 200f;

    private boolean debugDrawEnabled = false;

    private final HashSet<Collider> colliderLibrary = new HashSet<>();
    private final HashMap<IntPair, HashSet<Collider>> spatialHashMap = new HashMap<>();

    private final ColliderDrawable colliderVision = new ColliderDrawable();
    private final BasicText colliderCount = new BasicText();

    public CollisionEngine() {
        colliderCount.setFont(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"));
        colliderCount.setPosition(new Vector2(-880, 480));
        colliderCount.setLayer(DrawLayer.GUIDefault);
        colliderCount.setCameraUse(false);
    }

    public void setDebugDraw(boolean drawColliders) {
        this.debugDrawEnabled = drawColliders;
    }

    public void resolveCollisions(boolean movement, boolean combat) {
        if (!(movement || combat)) return;

        updateSpatialHashmap();

        HashSet<ColliderPair> checkedPairs = new HashSet<>();

        for (Collider A : colliderLibrary) {
            if (!A.isActive()) continue;
            if (A.isHeavy() && A.attackStats == null && A.baseStats == null) continue;

            ArrayList<Collider> sortedCandidates = getCollisionCandidates(A);
            for (Collider B : sortedCandidates) {
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

                SATResults results = CollisionAlgorithms.CheckCollisionSAT(A, B);
                if (results.collision) {

                    if (doMovement) {
                        float AMass = Math.max(A.getMass(), 1);
                        float BMass = Math.max(B.getMass(), 1);
                        float total = AMass + BMass;

                        float APushback = A.isHeavy() ? 0f : (B.isHeavy() ? 1f : BMass / total); // The heavier the other object, the more you push this one
                        float BPushback = B.isHeavy() ? 0f : (A.isHeavy() ? 1f : AMass / total);

                        if (APushback != 0f) A.getPosition().add(new Vector2(results.mtv).scl(-APushback));
                        if (BPushback != 0f) B.getPosition().add(new Vector2(results.mtv).scl(BPushback));

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
        /** --- List of prerequisites / exclusions for combat ---
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

    public void registerCollider(Collider collider) { colliderLibrary.add(collider); }

    public void unregisterCollider(Collider collider) { colliderLibrary.remove(collider); }

    public void update() {
        if (debugDrawEnabled) {
            colliderVision.registerToEngine();
            colliderVision.setLayer(DrawLayer.Overlay);
            colliderVision.collidersToDraw.clear();
            colliderVision.collidersToDraw.addAll(colliderLibrary);

            colliderCount.setText("Colliders: " + colliderLibrary.size());
            colliderCount.registerToEngine();
        }
        else {
            colliderVision.unregisterFromEngine();
            colliderCount.unregisterFromEngine();
        }

        resolveCollisions(true, true);
    }

    /** Gets the collision candidates for the given collider.
     * @param collider Collider to request candidates for
     * @return An array of all the colliders that may be interacting with argument
     */
    private ArrayList<Collider> getCollisionCandidates(Collider collider) {
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

        ArrayList<Collider> candidates = new ArrayList<>();
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
        for (var collider: colliderLibrary) {
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
                    spatialHashMap.computeIfAbsent(new IntPair(x, y), k -> new HashSet<>()).add(collider);
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
        for (Collider A : colliderLibrary) {
            if (!A.isActive()) continue;
            if (!hitThese.contains(A.group)) continue;

            RaycastResults results = CollisionAlgorithms.Raycast(start, end, A);

            if (results.hit) hitList.add(new RaycastHit(results.hitStart, A));
        }

        hitList.sort(RaycastHitComparator);

        if (debugDrawEnabled) {
            colliderVision.raycastsToDrawOnce.add(new ColliderDrawable.VectorLine(start, end));
        }

        return hitList;
    }
}
