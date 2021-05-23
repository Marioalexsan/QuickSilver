package hg.animation;

/*
 * Type Documentation:
 *
 * TriggerAtStart
 *      Consumes nothing
 * TriggerAtFrameX
 *      Consumes one afArgs - frame to trigger at
 * TriggerAtEnd
 *      Consumes nothing
 */

/** ActCriteria defines criteria for triggering actions during animations */
public class ActCriteria {
    public enum Type {
        TriggerAtStart,
        TriggerAtFrameX,
        TriggerAtEnd
    }

    public final Type type;
    public final String sArgs;
    public final float[] afArgs;

    public ActCriteria(Type type) {
        this.type = type;
        this.sArgs = "";
        this.afArgs = new float[0];
    }

    public ActCriteria(Type type, String sArgs) {
        this.type = type;
        this.sArgs = sArgs;
        this.afArgs = new float[0];
    }

    public ActCriteria(Type type, float... afArgs) {
        this.type = type;
        this.sArgs = "";
        this.afArgs = afArgs.clone();
    }

    public ActCriteria(Type type, String sArgs, float... afArgs) {
        this.type = type;
        this.sArgs = sArgs;
        this.afArgs = afArgs.clone();
    }

}
