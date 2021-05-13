package hg.gamelogic.gamemodes;

import com.badlogic.gdx.math.Vector2;
import hg.directors.DirectorTypes;
import hg.directors.LevelDirector;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.states.DeathmatchState;
import hg.gamelogic.states.State;
import hg.networking.PlayerView;
import hg.networking.packets.NetInstruction;
import hg.types.GMType;
import hg.types.TargetType;
import hg.utils.DebugLevels;

import java.util.HashMap;
import java.util.Random;

/** Deathmatch:
 * - Winner is the player with the highest score after the round time ends
 * - Winner can also be the first player to reach X kills
 * - Free for all (each player is in their own team)
 * - Spawns are randomized in the level
 */
public class Deathmatch extends Gamemode {
    private int timeElapsed = 0;
    private int roundStart = 180;
    private int roundTime = 18000;
    private int roundEnd = 480;

    private final HashMap<Integer, Integer> playerViewScores = new HashMap<>();
    private int scoreToWin = 5;

    private int gameState; // 0 = starting, 1 = in progress, 2 = ending

    /** Set Deathmatch settings.
     * Setting roundTime to 0 is equivalent to infinity.
     * Setting killsToWin to 0 is equivalent to infinity.
     * Note: Setting both to 0 will result in an infinite Deathmatch game. */
    public void setSettings(int roundTime, int killsToWin) {
        this.roundTime = roundTime;
        this.scoreToWin = killsToWin;
    }

    @Override
    public void onKillCallback(Entity killer, Entity victim) {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        if (gameState != 1) return;
        if (!network.isLocalOrServer() || killer == null || victim == null) return;

        PlayerView killerView = manager.getPlayerViewByActorID(killer.getID());
        PlayerView victimView = manager.getPlayerViewByActorID(victim.getID());
        if (killerView != null && victimView != null) {
            registerScore(killerView, victimView);

            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, GMType.Deathmatch, 0).setInts(killerView.uniqueID, victimView.uniqueID);
            network.sendToAllClients(msg, true);

            if (scoreToWin != 0 && playerViewScores.get(killerView.uniqueID) >= scoreToWin) {
                onMatchEnd();
            }
        }
    }

    public void registerScore(PlayerView killerView, PlayerView victimView) {
        Integer score = playerViewScores.get(killerView.uniqueID);

        if (score == null) {
            HgGame.Manager().getChatSystem().addDebugMessage("Added missing score entry", DebugLevels.Warn);
            playerViewScores.put(killerView.uniqueID, 0);
            score = 0;
        }

        playerViewScores.put(killerView.uniqueID, score + 1);

        String[] trollWords = {
                "rekt", "fragged", "destroyed", "obliterated", "yeeted",
                "owned", "discombobulated", "disassembled", "scrapped",
                "rickrolled", "dominated", "gotted"
        };
        String troll = trollWords[Math.abs(new Random().nextInt()) % trollWords.length];

        HgGame.Manager().getChatSystem().addMessage(victimView.name + " got " + troll + " by " + killerView.name);
    }

    @Override
    public void update() {
        NetworkEngine network = HgGame.Network();
        boolean isServer = network.isLocalOrServer();

        timeElapsed++;

        if (isServer) {
            if (gameState == 0 || gameState == 1)
                tryReviveDeadPlayers();

            if (gameState == 0) {
                if (timeElapsed > roundStart) {
                    startRound();
                }
            }
            else if (gameState == 1) {

                if (roundTime != 0 && timeElapsed > roundTime) {
                    onMatchEnd();
                }
            }
            else if (gameState == 2) {
                if (timeElapsed > roundEnd) {
                    restart();
                }
            }
        }
    }

    private void startRound() {
        NetworkEngine network = HgGame.Network();

        timeElapsed = 0;
        gameState = 1;
        HgGame.Manager().setNotice("Match started!", 90);

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 3);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

    // Only server calls this
    private void tryReviveDeadPlayers() {
        GameManager manager = HgGame.Manager();
        LevelDirector level = (LevelDirector) manager.getDirector(DirectorTypes.LevelDirector);
        PlayerEntity[] deadPlayers = manager.getDeadPlayerEntities();

        for (var player: deadPlayers) {
            if (player.getStats().deathCounter > 180) {
                player.revive();
                if (level != null) {
                    player.setPosition(level.getRandomSpawnpoint());

                    NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 4);
                    msg.setInts(player.getID()).setFloats(player.getPosition().x, player.getPosition().y);

                    HgGame.Network().sendToAllClients(msg, true);
                }
            }
        }
    }

    @Override
    public void onMatchStart() {
        GameManager manager = HgGame.Manager();

        for (var view: manager.getPlayerViews())
            playerViewScores.put(view.uniqueID, 0);

        restart();
    }

    @Override
    public void onMatchEnd() {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        int maxScore = 0;
        int maxUniqueID = -1;

        for (var scores: playerViewScores.entrySet()) {
            if (maxScore < scores.getValue()) {
                maxScore = scores.getValue();
                maxUniqueID = scores.getKey();
            }
        }

        PlayerView view = manager.getPlayerViewByUniqueID(maxUniqueID);

        manager.setNotice((view != null ? view.name : "Incognito") + " wins, with a total of " + maxScore + " kills!", 120);

        timeElapsed = 0;
        gameState = 2;

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 1);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

    @Override
    public void restart() {
        NetworkEngine network = HgGame.Network();

        HgGame.Manager().setNotice("Get ready..." , 60);

        timeElapsed = 0;
        gameState = 0;

        for (var score: playerViewScores.entrySet()) {
            score.setValue(0);
        }

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 2);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

    @Override
    public boolean isTeamGamemode() {
        return false;
    }

    @Override
    public void onInstructionFromServer(NetInstruction msg) {
        GameManager manager = HgGame.Manager();

        switch (msg.insType) {
            case 0 -> {
                PlayerView killerView = manager.getPlayerViewByUniqueID(msg.intParams[0]);
                PlayerView victimView = manager.getPlayerViewByUniqueID(msg.intParams[1]);
                registerScore(killerView, victimView);
            }
            case 1 -> onMatchEnd();
            case 2 -> restart();
            case 3 -> startRound();
            case 4 -> {
                Entity target = manager.getActor(msg.intParams[0]);
                if (target != null) {
                    target.setPosition(msg.floatParams[0], msg.floatParams[1]);
                }
            }
            default -> manager.getChatSystem().addDebugMessage("Unknown Deathmatch instruction " + msg.insType, DebugLevels.Warn);
        }
    }

    @Override
    public State tryGenerateState() {
        DeathmatchState stuff = new DeathmatchState();
        stuff.timeElapsed = timeElapsed;
        stuff.roundStart = roundStart;
        stuff.roundTime = roundTime;
        stuff.roundEnd = roundEnd;
        stuff.scoreToWin = scoreToWin;
        stuff.copyScores(playerViewScores);
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof DeathmatchState) {
            DeathmatchState stuff = (DeathmatchState) state;
            timeElapsed = stuff.timeElapsed;;
            roundStart = stuff.roundStart;
            roundTime = stuff.roundTime;
            roundEnd = stuff.roundEnd;
            scoreToWin = stuff.scoreToWin;
            playerViewScores.clear();
            for (var score: stuff.playerViewScores.entrySet()) {
                playerViewScores.put(score.getKey(), score.getValue());
            }
        }
    }
}
