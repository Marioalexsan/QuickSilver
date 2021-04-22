package hg.physics;

import com.badlogic.gdx.math.Vector2;
import hg.interfaces.IPolygon;
import hg.utils.HgMath;

// SAT Documentation:
// http://www.dyn4j.org/2010/01/sat/#sat-mtv
// https://www.sevenson.com.au/programming/sat/#download

/** Holds methods for collision detection and resolution */
public class CollisionAlgorithms {

    // ---
    // Generic Stuff
    // ---

    public static float[] PolyAABB(Vector2[] vertices) {
        float XMin = vertices[0].x, YMin = vertices[0].y, XMax = XMin, YMax = YMin;

        for (int i = 1; i < vertices.length; i++) {
            XMin = Math.min(XMin, vertices[i].x);
            XMax = Math.max(XMax, vertices[i].x);
            YMin = Math.min(YMin, vertices[i].y);
            YMax = Math.max(YMax, vertices[i].y);
        }

        return new float[] {XMin, YMin, XMax, YMax};
    }

    public static float[] SphereAABB(Vector2 position, float radius) {
        return new float[] { position.x - radius, position.y - radius, position.x + radius, position.y + radius };
    }

    public static float[] ProjectPolygonOnAxis(Vector2[] vertices, Vector2 axis) {
        float min = axis.dot(vertices[0]), max = min, projection;

        for (int j = 1; j < vertices.length; j++) {
            projection = axis.dot(vertices[j]);
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }

        return new float[] {min, max};
    }

    public static float[] ProjectSphereOnAxis(Vector2 center, float radius, Vector2 axis) {
        float projectedCenter = axis.dot(center);
        return new float[] { projectedCenter - radius, projectedCenter + radius };
    }

    // ---
    // Separating Axis Theorem Stuff
    // ---

    /** Checks for collision between two colliders using the Separating Axis Theorem (SAT) algorithm
     * @param A The first collider, which is either a SphereCollider or implements IPolygon
     * @param B The second collider, which is either a SphereCollider or implements IPolygon
     * @return A SATResults structure with the collision data.
     * @throws RuntimeException If the collider provided is unsupported
     */
    public static SATResults CheckCollisionSAT(Collider A, Collider B) {

        if (A instanceof IPolygon) {
            if (B instanceof SphereCollider)
                return PolyCircleSAT(A.trueCenter(), ((IPolygon) A).trueVertices(), B.trueCenter(), ((SphereCollider) B).getRadius());
            if (B instanceof IPolygon)
                return PolyPolySAT(A.trueCenter(), ((IPolygon) A).trueVertices(), B.trueCenter(), ((IPolygon) B).trueVertices());
        }

        if (A instanceof SphereCollider) {
            if (B instanceof SphereCollider)
                return CircleCircleSAT(A.trueCenter(), ((SphereCollider) A).getRadius(), B.trueCenter(), ((SphereCollider) B).getRadius());

            if (B instanceof IPolygon) {
                var results = PolyCircleSAT(B.trueCenter(), ((IPolygon) B).trueVertices(), A.trueCenter(), ((SphereCollider) A).getRadius());
                if (results.collision) results.mtv.scl(-1);
                return results;
            }
        }

        throw new RuntimeException("One of the provided colliders is not supported! Colliders given: " + A.toString() + " " + B.toString());
    }

    public static SATResults PolyPolySAT(Vector2 ACenter, Vector2[] A, Vector2 BCenter, Vector2[] B) {
        Vector2 overlapVector = null;
        float overlapLength = Float.MAX_VALUE;

        // Check across A's edges
        for (int i = 0; i < A.length; i++) {
            Vector2 axis = new Vector2(A[(i + 1) % A.length]).sub(A[i]).rotate90(0).nor();

            float[] projA = ProjectPolygonOnAxis(A, axis);
            float[] projB = ProjectPolygonOnAxis(B, axis);

            if (projB[1] < projA[0] || projA[1] < projB[0]) {
                return new SATResults();
            }
            else {
                float overlap = Math.min(projB[1] - projA[0], projA[1] - projB[0]);
                if (overlap < overlapLength) {
                    overlapLength = overlap;
                    overlapVector = axis;
                }
            }
        }

        // Check across B's edges
        for (int j = 0; j < B.length; j++) {
            Vector2 axis = new Vector2(B[(j + 1) % B.length]).sub(B[j]).rotate90(0).nor();

            float[] projA = ProjectPolygonOnAxis(A, axis);
            float[] projB = ProjectPolygonOnAxis(B, axis);

            if (projB[1] < projA[0] || projA[1] < projB[0]) {
                return new SATResults();
            }
            else {
                float overlap = Math.min(projB[1] - projA[0], projA[1] - projB[0]);
                if (overlap < overlapLength) {
                    overlapLength = overlap;
                    overlapVector = axis;
                }
            }
        }

        Vector2 AtoB = new Vector2(BCenter).sub(ACenter);
        if (overlapVector.dot(AtoB) < 0) overlapVector.scl(-1);

        return new SATResults(new Vector2(overlapVector).scl(overlapLength));
    }

    public static SATResults PolyCircleSAT(Vector2 ACenter, Vector2[] A, Vector2 BCenter, float BRadius) {
        Vector2 overlapVector = null;
        float overlapLength = Float.MAX_VALUE;

        // Check across A's edges
        for (int i = 0; i < A.length; i++) {
            Vector2 axis = new Vector2(A[(i + 1) % A.length]).sub(A[i]).rotate90(0).nor();

            float[] projA = ProjectPolygonOnAxis(A, axis); // [0] = min, [1] = max
            float[] projB = ProjectSphereOnAxis(BCenter, BRadius, axis);

            if (projB[1] < projA[0] || projA[1] < projB[0]) return new SATResults(); // 100% Not overlapping.
            else {
                float overlap = Math.min(projB[1] - projA[0], projA[1] - projB[0]);
                if (overlap < overlapLength) {
                    overlapLength = overlap;
                    overlapVector = axis;
                }
            }
        }

        // Check B's axis
        {
            Vector2 circleAxis = new Vector2();
            float length = Float.MAX_VALUE, nextLength;
            for (Vector2 vector2 : A) {
                nextLength = Vector2.len2(vector2.x - BCenter.x, vector2.y - BCenter.y);
                if (nextLength < length) {
                    length = nextLength;
                    circleAxis = new Vector2(vector2); // Saves vertex, then recomputes axis later
                }
            }
            circleAxis.sub(BCenter).nor();

            float[] projA = ProjectPolygonOnAxis(A, circleAxis); // [0] = min, [1] = max
            float[] projB = ProjectSphereOnAxis(BCenter, BRadius, circleAxis);

            if (projB[1] < projA[0] || projA[1] < projB[0]) return new SATResults(); // 100% Not overlapping.
            else {
                float overlap = Math.min(projB[1] - projA[0], projA[1] - projB[0]);
                if (overlap < overlapLength) {
                    overlapLength = overlap;
                    overlapVector = circleAxis;
                }
            }
        }

        Vector2 AtoB = new Vector2(BCenter).sub(ACenter);
        if (overlapVector.dot(AtoB) < 0) overlapVector.scl(-1);

        return new SATResults(new Vector2(overlapVector).scl(overlapLength));
    }


    public static SATResults CircleCircleSAT(Vector2 ACenter, float ARadius, Vector2 BCenter, float BRadius) {
        Vector2 AtoB = new Vector2(BCenter).sub(ACenter);
        float delta = ARadius + BRadius - AtoB.len();
        return delta > 0 ? new SATResults(new Vector2(AtoB).nor().scl(delta)) : new SATResults();
    }

    // ---
    // Raycasts
    // ---

    public static RaycastResults Raycast(Vector2 start, Vector2 end, Collider A) {
        if (A instanceof IPolygon) return RaycastVsPoly(start, end, ((IPolygon) A).trueVertices());
        if (A instanceof SphereCollider) return RaycastVsSphere(start, end, A.trueCenter(), ((SphereCollider) A).getRadius());
        throw new RuntimeException("Provided collider is not supported! Collider given: " + A.toString());
    }

    // This considers raycasts that end up completely inside the shape as misses.
    // This also cannot detect hit start correctly if raycast started inside shape
    // (should be 0, ends up being the exit wound)
    public static RaycastResults RaycastVsPoly(Vector2 start, Vector2 end, Vector2[] vertices) {

        double[] ray = HgMath.SolveRaycastLinearSystem(start.x, start.y, end.x, end.y);
        if (ray.length == 0) return new RaycastResults();

        float min = Float.MAX_VALUE;

        for (int i = 0; i < vertices.length; i++) {
            Vector2 first = vertices[i];
            Vector2 second = vertices[(i + 1) % vertices.length];

            double[] edge = HgMath.SolveRaycastLinearSystem(first.x, first.y, second.x, second.y);
            if (edge.length == 0) continue; // Bad sign, collider may be crappy

            double[] impact = HgMath.SolveRaycastLinearSystem(ray[0], ray[1], edge[0], edge[1]);
            if (impact.length == 0) continue; // No impact

            float boxLeft = Math.max(Math.min(start.x, end.x), Math.min(first.x, second.x));
            float boxRight = Math.min(Math.max(start.x, end.x), Math.max(first.x, second.x));
            float boxBottom = Math.max(Math.min(start.y, end.y), Math.min(first.y, second.y));
            float boxTop = Math.min(Math.max(start.y, end.y), Math.max(first.y, second.y));

            // Impact is valid if inside a valid AABB intersection of both ray and edge's AABBs
            boolean valid = boxLeft <= boxRight && boxBottom <= boxTop &&
                    HgMath.InRangeEPS(impact[0], boxLeft, boxRight, 0.016) &&
                    HgMath.InRangeEPS(impact[1], boxBottom, boxTop, 0.016);

            if (valid) {
                // Good hit! Calculate and save relative distance
                float distance = Vector2.len((float)(impact[0] - start.x), (float)(impact[1] - start.y));
                min = Math.min(min, distance);
            }
        }

        if (min != Float.MAX_VALUE) {
            float length = Vector2.len(end.x - start.x, end.y - start.y);
            return new RaycastResults(min / length);
        }
        return new RaycastResults();
    }

    // This considers raycasts that end up completely inside the shape as misses.
    // Because of problems with RaycastVsPoly, starting points inside shapes that do collide return
    // the exit wound distance instead of 0
    public static RaycastResults RaycastVsSphere(Vector2 start, Vector2 end, Vector2 center, float radius) {
        // I've taken the easy way out here:
        // https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm

        Vector2 d = new Vector2(end).sub(start);
        Vector2 f = new Vector2(start).sub(center);

        float a = d.x * d.x + d.y * d.y;
        float b = 2f * (f.x * d.x + f.y * d.y);
        float c = f.x * f.x + f.y * f.y - radius * radius;

        float delta = b * b - 4 * a * c;

        if (delta < 0) return new RaycastResults();

        delta = (float) Math.sqrt(delta);

        float t1 = (-b - delta) / (2 * a);
        float t2 = (-b + delta) / (2 * a);

        if (t1 >= 0 && t1 <= 1) return new RaycastResults(t1);
        if (t2 >= 0 && t2 <= 1) return new RaycastResults(t2);
        return new RaycastResults();
    }
}
