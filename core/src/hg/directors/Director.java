package hg.directors;

import hg.interfaces.IDestroyable;
import hg.interfaces.IUpdateable;

public abstract class Director implements IUpdateable, IDestroyable {

    protected boolean toBeDestroyed = false;
    protected int ID;

    @Override
    public void signalDestruction() {
        toBeDestroyed = true;
    }

    @Override
    public boolean isDestructionSignalled() {
        return toBeDestroyed;
    }

}
