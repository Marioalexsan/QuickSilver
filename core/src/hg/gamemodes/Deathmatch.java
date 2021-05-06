package hg.gamemodes;

public class Deathmatch extends Gamemode {
    private int timeElapsed = 0;

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        timeElapsed++;

        if (timeElapsed > 6000) {
            status = GameStatus.Finished;
        }
    }

    @Override
    public void onMatchStarted() {
        status = GameStatus.InProgress;
    }

    @Override
    public void onMatchEnd() {

    }

    @Override
    public boolean isTeamGamemode() {
        return false;
    }
}
