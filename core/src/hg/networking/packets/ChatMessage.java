package hg.networking.packets;

import hg.game.ChatSystem;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

/** ChatMessage is a packet that contains a message and a senderID
 * ChatMessages are sent by a client to the server, who then broadcasts them to the other clients.
 * Servers will also broadcast their own ChatMessages to everyone else */
public class ChatMessage extends Packet {
    public String message;
    public int senderUniqueID;

    @Override
    public void parseOnClient() {
        HgGame.Manager().getChatSystem().addMessageFromView(message, HgGame.Manager().getUniqueIDPlayerView(senderUniqueID));
    };

    @Override
    public void parseOnServer(int connectionID) {
        parseOnClient();

        GameManager manager = HgGame.Manager();

        PlayerView sender = manager.getConnectionIDPlayerView(connectionID);

        for (var view : manager.getPlayerViews()) {
            if (view != manager.localView && view.connectionID != connectionID) {
                ChatMessage msg = new ChatMessage();
                msg.message = message;
                msg.senderUniqueID = sender == null ? -1 : sender.uniqueID;
                HgGame.Network().sendPacketToClient(view.connectionID, this, true);
            }
        }
    };
}
