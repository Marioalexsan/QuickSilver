package hg.directors;

import hg.interfaces.IDestroyable;
import hg.interfaces.IUpdateable;

/** Directors are objects that can run various code
 * Somewhat equivalent to a game state, if multiple states could be active at the same time
 * (Yes, I don't know what the heck I'm doing)
 */
public abstract class Director implements IUpdateable, IDestroyable {
    protected boolean toBeDestroyed = false;

    @Override
    public void signalDestroy() {
        toBeDestroyed = true;
    }

    @Override
    public boolean isDestroySignalled() {
        return toBeDestroyed;
    }
}
