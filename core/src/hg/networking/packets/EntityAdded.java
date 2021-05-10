package hg.networking.packets;

import com.badlogic.gdx.math.Vector2;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.types.EntityType;

public class EntityAdded extends Packet {
    public int entityType;
    public int entitySubType;
    public int entityID;
    public float posX;
    public float posY;
    public float angle;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (entityType) {
            case EntityType.Actors -> {
                manager.addActor(entityID, entitySubType, new Vector2(posX, posY), angle);
                manager.getChatSystem().addMessage("Added entity of type " + entityType + ", ID " + entityID);
            }
            default -> manager.getChatSystem().addMessage("[Warn] Unallowed add of entity type " + entityType);
        }
    }
}
