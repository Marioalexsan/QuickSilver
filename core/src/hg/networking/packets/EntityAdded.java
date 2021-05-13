package hg.networking.packets;

import com.badlogic.gdx.math.Vector2;
import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.networking.NetworkRole;
import hg.networking.Packet;
import hg.types.TargetType;
import hg.utils.DebugLevels;

public class EntityAdded extends Packet {
    public int entityType;
    public int entitySubType;
    public int entityID;
    public float posX;
    public float posY;
    public float angle;

    public EntityAdded(int entityType, int entitySubType, int entityID, float posX, float posY, float angle) {
        this.entityType = entityType;
        this.entitySubType = entitySubType;
        this.entityID = entityID;
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        switch (entityType) {
            case TargetType.Actors -> {
                Entity actor = manager.addActor(entityID, entitySubType, new Vector2(posX, posY), angle);

                if (actor instanceof PlayerEntity) {
                    if (network.getNetRole() == NetworkRole.Client) {
                        ((PlayerEntity) actor).setLogic(new NetworkPlayerLogic());
                    }
                }
            }
            default -> manager.getChatSystem().addDebugMessage("Unknown entity type " + entityType, DebugLevels.Warn);
        }
    }

    public EntityAdded() {} // For Kryonet
}
