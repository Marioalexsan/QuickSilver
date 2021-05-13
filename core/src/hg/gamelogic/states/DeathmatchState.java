package hg.gamelogic.states;

import java.util.HashMap;

public class DeathmatchState extends State {
    public int timeElapsed;
    public int roundStart;
    public int roundTime;
    public int roundEnd;

    public final HashMap<Integer, Integer> playerViewScores = new HashMap<>();
    public int scoreToWin;

    public void copyScores(HashMap<Integer, Integer> scores) {
        playerViewScores.clear();
        for (var score: scores.entrySet()) {
            playerViewScores.put(score.getKey(), score.getValue());
        }
    }

    public DeathmatchState() {} // For Kryonet
}
