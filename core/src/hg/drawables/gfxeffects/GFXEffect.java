package hg.drawables.gfxeffects;

import hg.drawables.Drawable;

// Drawables that have their own lifetime
// GameManager unregisters effects when they expire
// (although there is no strong reason why GameManager needs to do that)

public abstract class GFXEffect extends Drawable {
    protected int timeLeft;

    public GFXEffect(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean isExpired() {
        return timeLeft == 0;
    }

    public void endPrematurely() {
        timeLeft = 0;
    }

    public void update() {
        if (timeLeft > 0) timeLeft--;
    }
}
