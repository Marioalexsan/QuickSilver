package hg.gamelogic.gamemodes;

import hg.entities.Entity;
import hg.gamelogic.ObjectState;
import hg.interfaces.IDestroyable;
import hg.interfaces.INetInterface;
import hg.interfaces.IUpdateable;
import hg.networking.PlayerView;

/** Gamemodes manage things like round start, progression, end, player managing, etc.
 * In a way, it controls what exactly happens in a GameSession */
public abstract class Gamemode implements IUpdateable, INetInterface, IDestroyable {
    protected boolean toBeDestroyed = false;

    @Override
    public void signalDestroy() {
        toBeDestroyed = true;
    }

    @Override
    public boolean isDestroySignalled() {
        return toBeDestroyed;
    }

    abstract public void onMatchStart();

    abstract public void onMatchEnd();

    abstract public void restart();

    public ObjectState tryGenerateState() {
        return null;
    }

    public void tryApplyState(ObjectState state) { }

    // Some generic callbacks

    public void onKillCallback(Entity killer, Entity victim) {}

    public void onPlayerViewAdded(PlayerView view) {}

    public void onPlayerViewRemoved(PlayerView view) {}

    public void onEntityAdded(Entity entity) {}

    public void onEntityRemoved(Entity entity) {}


}
