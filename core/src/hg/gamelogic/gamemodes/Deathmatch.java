package hg.gamelogic.gamemodes;

import hg.directors.Level;
import hg.drawables.DrawLayer;
import hg.drawables.DeathmatchScoreboard;
import hg.entities.Pickup;
import hg.entities.Spawner;
import hg.enums.DirectorType;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.DataManager;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.ObjectState;
import hg.networking.PlayerView;
import hg.networking.packets.NetInstruction;
import hg.enums.GMType;
import hg.enums.TargetType;
import hg.utils.DebugLevels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/** Deathmatch:
 * - Winner is the player with the highest score after the round time ends
 * - Winner can also be the first player to reach X kills
 * - Free for all (each player is in their own team)
 * - Spawns are randomized in the level
 */
public class Deathmatch extends Gamemode {
    public static class State extends ObjectState {
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

        public State() {} // For Kryonet
    }

    private int timeElapsed = 0;
    private int roundStart = 180;
    private int roundTime = 18000;
    private int roundEnd = 480;

    private final HashMap<Integer, Integer> playerViewScores = new HashMap<>();
    private int scoreToWin = 5;

    private int gameState; // 0 = starting, 1 = in progress, 2 = ending

    private final DeathmatchScoreboard scoreDrawable = new DeathmatchScoreboard();

    public Deathmatch() {
        scoreDrawable.getPosition().set(930, 510);
        scoreDrawable.registerToEngine();
        scoreDrawable.setLayer(DrawLayer.GUIDefault);
    }

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
            HgGame.Chat().addDebugMessage("Added missing score entry", DebugLevels.Warn);
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

        HgGame.Chat().addMessage(victimView.name + " got " + troll + " by " + killerView.name);
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
        scoreDrawable.updateScores(playerViewScores);
        scoreDrawable.updateStatus(gameState, roundTime - timeElapsed);
    }

    private void startRound() {
        NetworkEngine network = HgGame.Network();

        timeElapsed = 0;
        gameState = 1;
        HgGame.SetNotice("Match started!", 90);

        for (var view: HgGame.Manager().getPlayerViews()) {
            onPlayerViewAdded(view);
        }

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 3);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

    // Only server calls this
    private void tryReviveDeadPlayers() {
        GameManager manager = HgGame.Manager();
        Level level = (Level) manager.getDirector(DirectorType.Level);
        PlayerEntity[] deadPlayers = manager.getDeadPlayerEntities();

        for (var player: deadPlayers) {
            if (player.getStats().deathCounter > 180) {
                player.revive();
                if (level != null) {
                    player.getPosition().set(level.getRandomSpawnpoint());

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

        HgGame.SetNotice((view != null ? view.name : "Incognito") + " wins, with a total of " + maxScore + " kills!", 120);

        ArrayList<DataManager.DeathmatchResult> results = new ArrayList<>();

        for (var score: playerViewScores.entrySet()) {
            var scoreView = manager.getPlayerViewByUniqueID(score.getKey());
            results.add(new DataManager.DeathmatchResult(scoreView.name, score.getValue(), view == scoreView));
        }

        HgGame.Data().addDeathmatchResult(results);

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
        GameManager manager = HgGame.Manager();

        HgGame.SetNotice("Get ready..." , 60);

        timeElapsed = 0;
        gameState = 0;

        playerViewScores.clear();

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Gamemodes, -1337, 2);
            network.sendToAllClients(msg, true);

            for (var views: manager.getPlayerViews()) {
                PlayerEntity player = views.playerEntity;
                if (player != null) restartPlayer(player);
            }

            for (var entity: manager.getAllActors()) {
                if (entity instanceof Pickup) entity.signalDestroy();
                if (entity instanceof Spawner) {
                    ((Spawner) entity).reset();
                    ((Spawner) entity).advanceRatio(0.75f);
                }
            }
        }
    }

    private void restartPlayer(PlayerEntity player) {
        Level level = (Level) HgGame.Manager().getDirector(DirectorType.Level);

        player.revive();
        if (level != null) {
            player.getPosition().set(level.getRandomSpawnpoint());

            NetInstruction msg2 = new NetInstruction(TargetType.Gamemodes, -1337, 4);
            msg2.setInts(player.getID()).setFloats(player.getPosition().x, player.getPosition().y);

            HgGame.Network().sendToAllClients(msg2, true);
        }
    }

    @Override
    public void onEntityAdded(Entity entity) {
        if (!HgGame.Network().isLocalOrServer()) return;
        if (entity instanceof PlayerEntity) {
            restartPlayer((PlayerEntity) entity);
        }
    }

    @Override
    public void onPlayerViewAdded(PlayerView view) {
        if (!HgGame.Network().isLocalOrServer()) return;
        playerViewScores.put(view.uniqueID, 0);
    }

    @Override
    public void onPlayerViewRemoved(PlayerView view) {
        if (!HgGame.Network().isLocalOrServer()) return;
        playerViewScores.remove(view.uniqueID);
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
                    target.getPosition().set(msg.floatParams[0], msg.floatParams[1]);
                    if (target instanceof PlayerEntity) {
                        ((PlayerEntity) target).revive();
                    }
                }
            }
            default -> HgGame.Chat().addDebugMessage("Unknown Deathmatch instruction " + msg.insType, DebugLevels.Warn);
        }
    }

    @Override
    public ObjectState tryGenerateState() {
        State stuff = new State();
        stuff.timeElapsed = timeElapsed;
        stuff.roundStart = roundStart;
        stuff.roundTime = roundTime;
        stuff.roundEnd = roundEnd;
        stuff.scoreToWin = scoreToWin;
        stuff.copyScores(playerViewScores);
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            timeElapsed = stuff.timeElapsed;
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

    @Override
    public void destroy() {
        scoreDrawable.unregisterFromEngine();
    }
}
