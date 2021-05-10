package hg.networking.packets;

import hg.engine.NetworkEngine;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

/** ClientInitRequest is sent by clients to tell the server some basic info, such as their name.
 * Servers parse this by creating a new player view, sending the connecting client the full playerview list, then telling
 * existing clients about a new connected client. */
public class ClientInitRequest extends Packet {
    public String clientName;

    @Override
    public void parseOnServer(int connectionID) {
        GameManager manager = HgGame.Manager();
        NetworkEngine network = HgGame.Network();

        PlayerView newView = manager.createPlayerView(PlayerView.Type.Client);
        newView.name = clientName;
        newView.connectionID = connectionID;

        // Tell info to source

        ClientInitResponse response = new ClientInitResponse();
        response.clientViewID = newView.uniqueID;
        response.allViews = manager.getPlayerViews();
        network.sendPacketToClient(connectionID, response, true);

        // Tell everyone else about a new player view

        PlayerViewConnected msg = new PlayerViewConnected();
        msg.newView = newView;

        for (var view: manager.getPlayerViews()) {
            if (view.uniqueID != newView.uniqueID && view != manager.localView) {
                network.sendPacketToClient(view.connectionID, msg, true);
            }
        }

        HgGame.Manager().getChatSystem().addMessage(newView.name + " connected.");
    }
}