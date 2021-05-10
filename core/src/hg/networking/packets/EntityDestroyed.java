package hg.networking.packets;

import com.badlogic.gdx.math.Vector2;
import hg.entities.Entity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.types.EntityType;

public class EntityDestroyed extends Packet {
    public int entityType;
    public int entityID;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        switch (entityType) {
            case EntityType.Actors -> {
                Entity which = manager.getActor(entityID);
                if (which == null) manager.getChatSystem().addMessage("[Warn] Tried to remove missing actor " + entityID);
                else which.signalDestroy();
            }
            default -> manager.getChatSystem().addMessage("[Warn] Unallowed remove of entity type " + entityType);
        }
    }
}
