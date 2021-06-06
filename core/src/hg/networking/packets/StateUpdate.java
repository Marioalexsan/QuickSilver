package hg.networking.packets;

import hg.enums.DirectorType;
import hg.directors.GameSession;
import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Gamemode;
import hg.gamelogic.ObjectState;
import hg.networking.Packet;
import hg.enums.TargetType;
import hg.utils.DebugLevels;

/** Message that tells clients the given target needs to have the payload state applied to it. */
public class StateUpdate extends Packet {

    public int targetType; // The ID subset to target . Takes values from StateTargetType
    public int targetID; // The ID object to target (Specific entity / director etc.) IDs depend on current status of GameManager.
    public ObjectState payload; // What we actually want to send. Take care to register subclasses of State with NetworkHelper

    public StateUpdate(int targetType, int targetID, ObjectState payload) {
        this.targetType = targetType;
        this.targetID = targetID;
        this.payload = payload;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (targetType) {
            case TargetType.Actors -> {
                Entity target = manager.getActor(targetID);
                if (target == null) {
                    HgGame.Chat().addDebugMessage("Update for unknown actor " + targetID, DebugLevels.Warn);
                    return;
                }
                target.tryApplyState(payload);
            }
            case TargetType.Gamemodes -> {
                // targetID ignored. There's only one gamemode running
                GameSession match = (GameSession) manager.getDirector(DirectorType.GameSession);
                Gamemode mode = null;
                if (match != null) mode = match.getGamemode();
                if (mode != null) mode.tryApplyState(payload);

            }
            default -> HgGame.Chat().addDebugMessage("Update for unallowed type " + targetType, DebugLevels.Warn);
        }
    }

    public StateUpdate() {} // For Kryonet
}
