package hg.networking.packets;

import hg.enums.DirectorType;
import hg.directors.GameSession;
import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Gamemode;
import hg.networking.Packet;
import hg.enums.TargetType;
import hg.utils.DebugLevels;

/** Message that tells the client the given target needs to do an action.
 * Used for actions which cannot be triggered via state updates. */
public class NetInstruction extends Packet {
    public int targetType;
    public int targetID;

    public int insType;
    public int[] intParams = null;
    public float[] floatParams = null;
    public String stringParam = null;

    public NetInstruction(int targetType, int targetID, int insType) {
        this.targetType = targetType;
        this.targetID = targetID;
        this.insType = insType;
    }

    public NetInstruction setFloats(float... params) {
        floatParams = params;
        return this;
    }

    public NetInstruction setInts(int... params) {
        intParams = params;
        return this;
    }

    public NetInstruction setString(String param) {
        stringParam = param;
        return this;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (targetType) {
            case TargetType.Actors -> {
                Entity target = manager.getActor(targetID);
                if (target == null) {
                    HgGame.Chat().addDebugMessage("Instruction " + insType + " for unknown actor " + targetID, DebugLevels.Warn);
                    return;
                }
                target.onInstructionFromServer(this);
            }
            case TargetType.Gamemodes -> {
                GameSession match = (GameSession) manager.getDirector(DirectorType.GameSession);
                Gamemode mode = null;
                if (match != null) mode = match.getGamemode();
                if (mode != null) mode.onInstructionFromServer(this);
                if (mode == null) HgGame.Chat().addDebugMessage("Mode is null in NetInstruction", DebugLevels.Warn);
            }
            default -> HgGame.Chat().addDebugMessage("Instruction for other type " + targetType, DebugLevels.Warn);
        }
    }

    public NetInstruction() {} // For Kryonet
}
