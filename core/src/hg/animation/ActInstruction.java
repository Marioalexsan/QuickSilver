package hg.animation;

public class ActInstruction {
    public final ActCriteria criteria;
    public final ActEffect effect;

    public boolean triggered = false;

    public ActInstruction(ActCriteria criteria, ActEffect effect) {
        this.criteria = criteria;
        this.effect = effect;
    }
}
