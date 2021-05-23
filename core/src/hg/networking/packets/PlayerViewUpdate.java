package hg.networking.packets;

import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.networking.Packet;

/** Updates the player view of a client */
public class PlayerViewUpdate extends Packet {
    public int targetUniqueID;
    public int controlledEntityID;

    public PlayerViewUpdate(int targetUniqueID, int controlledEntityID) {
        this.targetUniqueID = targetUniqueID;
        this.controlledEntityID = controlledEntityID;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        for (var view: manager.getPlayerViews()) {
            if (view.uniqueID == targetUniqueID) {
                if (controlledEntityID == -1) {
                    view.playerEntity = null;
                    return;
                }
                Entity player = manager.getActor(controlledEntityID);
                if (player instanceof PlayerEntity) {
                    view.playerEntity = (PlayerEntity) player;

                    // This also needs to set the Logic to LocalPlayerLogic or NetworkPlayerLogic

                    if (view.uniqueID == manager.localView.uniqueID)
                        view.playerEntity.setLogic(new LocalPlayerLogic());
                    else if (view.playerEntity.getLogic() instanceof EmptyAI)
                        view.playerEntity.setLogic(new NetworkPlayerLogic());
                }
                return;
            }
        }
    }

    public PlayerViewUpdate() {} // For Kryonet
}
