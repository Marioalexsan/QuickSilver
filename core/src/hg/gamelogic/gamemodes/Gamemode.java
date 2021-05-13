package hg.gamelogic.gamemodes;

import hg.entities.Entity;
import hg.gamelogic.states.State;
import hg.interfaces.INetInterface;
import hg.interfaces.IUpdateable;

/** Gamemodes manage things like round start, progression, end, player managing, etc. */
public abstract class Gamemode implements IUpdateable, INetInterface {
    abstract public void onMatchStart();

    abstract public void onMatchEnd();

    abstract public void restart();

    /** This should return true if this gamemode relies on teams. Otherwise, it should return false (Free-For-All style gamemodes) */
    abstract public boolean isTeamGamemode();

    public State tryGenerateState() {
        return null;
    }

    public void tryApplyState(State state) { }

    // Next we have some generic callbacks to inject into code

    public void onKillCallback(Entity killer, Entity victim) {}
}
