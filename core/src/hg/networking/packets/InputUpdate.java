package hg.networking.packets;

import com.badlogic.gdx.math.Vector2;
import hg.game.ChatSystem;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.gamelogic.playerlogic.PlayerLogic;
import hg.networking.Packet;
import hg.networking.PlayerView;

// Holds input from a player
public class InputUpdate extends Packet {
    public int uniqueID = -1;
    public int[] mappedActions;
    public float aimX;
    public float aimY;

    public InputUpdate(int[] mappedActions, float aimX, float aimY) {
        this.mappedActions = mappedActions;
        this.aimX = aimX;
        this.aimY = aimY;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        PlayerView target = manager.getPlayerViewByUniqueID(uniqueID);
        if (target == null || target.playerEntity == null) return;

        PlayerLogic logic = target.playerEntity.getLogic();
        if (logic instanceof NetworkPlayerLogic) {
            NetworkPlayerLogic netLogic = (NetworkPlayerLogic) logic;
            netLogic.receiveActions(mappedActions);
            netLogic.receiveAimPosition(new Vector2(aimX, aimY));
        }
    }

    @Override
    public void parseOnServer(int connectionID) {
        GameManager manager = HgGame.Manager();

        PlayerView target = manager.getPlayerViewByConnectionID(connectionID);
        if (target == null || target.playerEntity == null) return;

        PlayerLogic logic = target.playerEntity.getLogic();
        if (logic instanceof NetworkPlayerLogic) {
            NetworkPlayerLogic netLogic = (NetworkPlayerLogic) logic;
            netLogic.receiveActions(mappedActions);
            netLogic.receiveAimPosition(new Vector2(aimX, aimY));

            InputUpdate broadcast = new InputUpdate(mappedActions, aimX, aimY);
            broadcast.uniqueID = target.uniqueID;
            HgGame.Network().sendToAllClientsExcept(broadcast, false, connectionID);
        }
    }

    public InputUpdate() {} // For Kryonet
}
