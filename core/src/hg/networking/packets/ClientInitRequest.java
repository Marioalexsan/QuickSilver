package hg.networking.packets;

import com.badlogic.gdx.math.Vector2;
import hg.directors.Director;
import hg.directors.GameSession;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.enums.types.ActorType;
import hg.enums.types.DirectorType;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.playerlogic.LuigiAI;
import hg.gamelogic.playerlogic.NetworkPlayerLogic;
import hg.networking.Packet;
import hg.networking.PlayerView;
import hg.utils.BadCoderException;

/** ClientInitRequest is sent by clients to tell the server some basic info, such as their name.
 * Servers parse this by creating a new player view, sending the connecting client the full playerview list, then telling
 * existing clients about a new connected client. */
public class ClientInitRequest extends Packet {
    public String clientName;

    public ClientInitRequest(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void parseOnServer(int connectionID) {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        PlayerView newView = manager.createPlayerView(PlayerView.Type.Client);
        newView.name = clientName;
        newView.connectionID = connectionID;

        // Tell info to source

        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        GameSession.SessionOptions options = null;
        boolean matchStarted = false;
        if (director != null) {
            options = director.getSettings();
            matchStarted = director.getState() == 2;

            if (matchStarted) {
                PlayerEntity entity = (PlayerEntity) manager.addActor(ActorType.PlayerEntity, new Vector2(3450, 1500), 0f);
                entity.setLogic(new NetworkPlayerLogic());

                newView.playerEntity = entity;
            }
        }

        ClientInitResponse response = new ClientInitResponse(newView.uniqueID, manager.getPlayerViews(), options, matchStarted);
        response.copyServerEntitiesExcept(newView.playerEntity != null ? newView.playerEntity.getID() : -1);
        network.sendToClient(response, true, connectionID);

        // Tell everyone else about a new player view

        PlayerViewConnected msg = new PlayerViewConnected(newView);
        network.sendToAllClientsExcept(msg, true, newView.connectionID);

        manager.onClientInitialized(newView.uniqueID);
    }

    public ClientInitRequest() {} // For Kryonet
}