package hg.animation;

/** Type Documentation:
 *
 * PlaySound
 *      Consumes sArgs - sound to play.
 *      Optionally consumes one afFloat - sound volume.
 *      Optionally consumes another afFloat - it can be of any value. Its existence will cause the sound to be played globally!
 *
 */


/** Animation Effects
 *
 */
public class ActEffect {
    public enum Type {
        PlaySound
    }

    public final Type type;
    public final String sArgs;
    public final float[] afArgs;

    public ActEffect(Type type) {
        this.type = type;
        this.sArgs = "";
        this.afArgs = new float[0];
    }

    public ActEffect(Type type, String sArgs) {
        this.type = type;
        this.sArgs = sArgs;
        this.afArgs = new float[0];
    }

    public ActEffect(Type type, float[] afArgs) {
        this.type = type;
        this.sArgs = "";
        this.afArgs = afArgs.clone();
    }

    public ActEffect(Type type, String sArgs, float[] afArgs) {
        this.type = type;
        this.sArgs = sArgs;
        this.afArgs = afArgs.clone();
    }
}
