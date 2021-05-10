package hg.networking.packets;

import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

public class PlayerViewUpdate extends Packet {
    public int targetUniqueID;
    public int controlledEntityID;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        for (var view: manager.getPlayerViews()) {
            if (view.uniqueID == targetUniqueID) {
                Entity player = manager.getActor(controlledEntityID);
                if (player instanceof PlayerEntity) {
                    view.playerEntity = (PlayerEntity) player;
                }
            }
        }
    }
}
