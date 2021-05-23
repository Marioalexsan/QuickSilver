package hg.networking.packets;

import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.enums.types.TargetType;
import hg.utils.DebugLevels;

/** Server message that tells clients an existing entity was removed */
public class EntityDestroyed extends Packet {
    public int entityType;
    public int entityID;

    public EntityDestroyed(int entityType, int entityID) {
        this.entityType = entityType;
        this.entityID = entityID;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (entityType) {
            case TargetType.Actors -> {
                Entity which = manager.getActor(entityID);

                if (which == null)
                    HgGame.Chat().addDebugMessage("Tried to remove missing actor " + entityID, DebugLevels.Warn);
                else which.signalDestroy();
            }
            default -> HgGame.Chat().addDebugMessage("Unknown entity type to remove: " + entityType, DebugLevels.Warn);
        }
    }

    public EntityDestroyed() {} // For Kryonet
}
