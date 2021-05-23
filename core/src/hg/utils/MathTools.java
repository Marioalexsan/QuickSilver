package hg.utils;

import java.util.Random;

/**
 * Provides various utilities for dealing with QuickSilver's insane design choices.
 */
public class MathTools {

    /**
     * Returns a value that is in the range [leftBorder; rightBorder).
     * Values outside this range are mapped to the range using wrapping.
     * @return An value in range [leftBorder; rightBorder], or the original value if leftBorder > rightBorder.
     */
    public static double Scroll(double value, double leftBorder, double rightBorder) {
        double delta = rightBorder - leftBorder;
        if (delta <= 0.0) return value;

        int leftDeltas = (int) ((leftBorder - value) / delta);
        int rightDeltas = (int) ((rightBorder - value) / delta);
        if (leftDeltas == 0 && rightDeltas == 0) return value; // Value is in range

        value += Math.max(leftDeltas, rightDeltas) * delta;
        if (value == rightBorder) value -= delta;

        return value;
    }

    /**
     * Returns a value that is in the range [leftBorder; rightBorder).
     * Values outside this range are mapped to the range using wrapping.
     * @return An value in range [leftBorder; rightBorder], or the original value if leftBorder > rightBorder.
     */
    public static float Scroll(float value, float leftBorder, float rightBorder) {
        float delta = rightBorder - leftBorder;
        if (delta <= 0f) return value;

        int leftDeltas = (int) ((leftBorder - value) / delta);
        int rightDeltas = (int) ((rightBorder - value) / delta);
        if (leftDeltas == 0 && rightDeltas == 0) return value; // Value is in range

        value += Math.max(leftDeltas, rightDeltas) * delta;
        if (value == rightBorder) value -= delta;

        return value;
    }

    public static double Clamp(double value, double min, double max) { return Math.max(min, Math.min(value, max)); }

    public static float Clamp(float value, float min, float max) { return Math.max(min, Math.min(value, max)); }

    public static int Clamp(int value, int min, int max) { return Math.max(min, Math.min(value, max)); }

    public static boolean InRangeEPS(double value, double min, double max, double eps) {
        return min - eps <= value && value <= max + eps;
    }

    public static boolean InRangeEPS(float value, float min, float max, float eps) {
        return min - eps <= value && value <= max + eps;
    }

    public static boolean EqualEPS(double a, double b, double eps) {
        return b - eps <= a && a <= b + eps;
    }

    public static boolean EqualEPS(float a, float b, float eps) {
        return b - eps <= a && a <= b + eps;
    }

    /** Solves for X in AX = B, where A = [params] as Mat2x2, X = Mat2x1, B = [-1, -1] */
    public static double[] SolveRaycastLS(double A, double B, double C, double D) {
        // Lines as ax + by + 1 = 0
        double determ = A * D - B * C;
        return determ == 0 ? new double[0] : new double[] { (B - D) / determ, (C - A) / determ };
    }

    /** Returns a random number between the two bounds.
     * The returned number may be incl, but never excl */
    public static float Interp(float ratio, float first, float second) {
        return first + (second - first) * ratio;
    }

    /** Returns a random number between the two bounds.
     * The returned number may be incl, but never excl */
    public static float RandomInRange(Random machine, float incl, float excl) {
        return incl + machine.nextFloat() * excl;
    }
}