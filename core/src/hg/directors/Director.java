package hg.directors;

import hg.interfaces.IDestroyable;
import hg.interfaces.IUpdateable;

/** Directors are objects that can run various code
 * Somewhat equivalent to a game state, if multiple states could be active at the same time
 */
public abstract class Director implements IUpdateable, IDestroyable {
    protected boolean started = false;
    protected boolean toBeDestroyed = false;

    @Override
    public void signalDestruction() {
        toBeDestroyed = true;
    }

    @Override
    public boolean isDestructionSignalled() {
        return toBeDestroyed;
    }
}
