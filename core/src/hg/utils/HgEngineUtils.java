package hg.utils;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import hg.engine.AudioEngine;

/**
 * Various utility static methods for QuickSilver's engine.
 */
public class HgEngineUtils {

    /**
     * Helper function for calculating an Affine2 transform from the given position, center and angle.
     * @return
     */
    public static Affine2 GetAffineForPCA(Vector2 position, Vector2 center, Angle angle) {
        return new Affine2()
                .translate(position)
                .rotate((float) angle.getDeg())
                .translate(new Vector2(center).scl(-1));
    }

    /**
     * Helper function for calculating an Affine2 transform from the given position, center, angle, and their respective offsets.
     * @return
     */
    public static Affine2 GetAffineForPCAO(Vector2 position, Vector2 center, Angle angle,
                                           Vector2 pOffset, Vector2 cOffset, Angle aOffset) {
        return new Affine2()
                .translate(new Vector2(position).add(pOffset))
                .rotate((float) angle.getDeg() + (float) aOffset.getDeg())
                .translate(new Vector2(center).add(cOffset).scl(-1));
    }

    /**
     * Helper function for calculating volume and panning for positional audio.
     * Note: The simulation is a fantasy novel written by Miron Alexandru, and is out of touch with reality.
     * @param listener Position of listener
     * @param sound Position of sound
     * @return An array of two floats: volume modifier, and panning
     */
    public static float[] SimulatePositionalAudio(Vector2 listener, Vector2 sound, float minDistance, float maxDistance) {
        if (minDistance > maxDistance - 1) {
            minDistance = maxDistance - 1; // Enforce a minimal distance
        }
        Vector2 direction = new Vector2(sound).sub(listener);
        float distance = direction.len();
        float segment = maxDistance - minDistance;
        float relativeDistance = (float) HgMath.ClampValue(maxDistance - distance, 0.0, segment);

        float powerFactor = relativeDistance / segment;

        return new float[] {
                (float) Math.pow(powerFactor, 1.15),
                (float) Math.cos(direction.angleRad()) * (float) Math.pow(HgMath.ClampValue(1f - powerFactor, 0f, 1f), 0.25)
        };
    }


}
