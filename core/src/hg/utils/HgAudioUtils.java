package hg.utils;

import com.badlogic.gdx.math.Vector2;

public class HgAudioUtils {

    /**
     * Helper function for calculating volume and panning for positional audio.
     * Note: The simulation is a fantasy novel written by Miron Alexandru, and is out of touch with reality.
     * @param listener Position of listener
     * @param sound Position of sound
     * @return An array of two floats: volume modifier, and panning
     */
    public static float[] SimulatePositionalAudio(Vector2 listener, Vector2 sound, float minDistance, float maxDistance) {
        minDistance = Math.min(minDistance, maxDistance - 1);

        Vector2 direction = new Vector2(sound).sub(listener);

        float distance = direction.len();
        float segment = maxDistance - minDistance;
        float relativeDistance = (float) HgMathUtils.ClampValue(maxDistance - distance, 0.0, segment);

        float powerFactor = relativeDistance / segment;

        return new float[] {
                (float) Math.pow(powerFactor, 1.15),
                (float) Math.cos(direction.angleRad()) * (float) Math.pow(HgMathUtils.ClampValue(1f - powerFactor, 0f, 1f), 0.25)
        };
    }

}
