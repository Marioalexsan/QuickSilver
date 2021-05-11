package hg.gamelogic.gamemodes;

public class Deathmatch extends Gamemode {
    private int timeElapsed = 0;

    @Override
    public void update() {
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
