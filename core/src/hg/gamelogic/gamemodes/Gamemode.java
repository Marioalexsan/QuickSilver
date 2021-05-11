package hg.gamelogic.gamemodes;

import hg.interfaces.IUpdateable;

public abstract class Gamemode implements IUpdateable {
    public enum GameStatus {
        Preparing,
        InProgress,
        Finished
    }

    protected GameStatus status;

    abstract public void onMatchStarted();

    abstract public void onMatchEnd();

    /** This should return true if this gamemode relies on teams. Otherwise, it should return false (Free-For-All style gamemodes) */
    abstract public boolean isTeamGamemode();
}
