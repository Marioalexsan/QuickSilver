package hg.animation;

/** ActCriteria defines an effect and its criteria for triggering */
public class ActInstruction {
    public final ActCriteria criteria;
    public final ActEffect effect;

    public boolean triggered = false;

    public ActInstruction(ActCriteria criteria, ActEffect effect) {
        this.criteria = criteria;
        this.effect = effect;
    }
}
