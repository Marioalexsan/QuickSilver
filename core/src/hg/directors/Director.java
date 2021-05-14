package hg.directors;

import hg.interfaces.IDestroyable;
import hg.interfaces.IUpdateable;

/** Directors contain game logic that can be started and stopped individually.
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
