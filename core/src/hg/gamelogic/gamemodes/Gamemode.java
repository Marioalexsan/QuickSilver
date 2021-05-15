package hg.gamelogic.gamemodes;

import hg.entities.Entity;
import hg.gamelogic.states.State;
import hg.interfaces.INetInterface;
import hg.interfaces.IUpdateable;

/** Gamemodes manage things like round start, progression, end, player managing, etc.
 * In a way, it controls what exactly happens in a GameSession */
public abstract class Gamemode implements IUpdateable, INetInterface {
    abstract public void onMatchStart();

    abstract public void onMatchEnd();

    abstract public void restart();

    public State tryGenerateState() {
        return null;
    }

    public void tryApplyState(State state) { }

    // Some generic callbacks

    public void onKillCallback(Entity killer, Entity victim) {}
}
