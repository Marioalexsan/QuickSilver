package hg.networking.packets;

import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.game.State;
import hg.networking.Packet;
import hg.types.EntityType;

public class StateUpdate extends Packet {

    /** The ID subset to target (Entities / Directors / Environments etc.). Takes values from StateTargetType */
    public int targetType;

    /** The ID object to target (Specific entity / director etc.) IDs depend on current status of GameManager. */
    public int targetID;

    public State payload;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (targetType) {
            case EntityType.Actors -> {
                Entity target = manager.getActor(targetID);
                if (target == null) {
                    //manager.getChatSystem().addMessage("[Warn] Update for unknown actor " + targetID);
                    return;
                }
                target.tryApplyState(payload);
            }
            //default -> manager.getChatSystem().addMessage("[Warn] Update for unallowed type " + targetType);
        }
    }
}
