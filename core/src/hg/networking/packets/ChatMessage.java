package hg.networking.packets;

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

    public ChatMessage(String message, int senderUniqueID) {
        this.message = message;
        this.senderUniqueID = senderUniqueID;
    }

    @Override
    public void parseOnClient() {
        HgGame.Manager().getChatSystem().addMessageFromView(message, HgGame.Manager().getPlayerViewByUniqueID(senderUniqueID));
    }

    @Override
    public void parseOnServer(int connectionID) {
        GameManager manager = HgGame.Manager();
        manager.getChatSystem().addMessageFromView(message, HgGame.Manager().getPlayerViewByUniqueID(senderUniqueID));

        PlayerView sender = manager.getPlayerViewByConnectionID(connectionID);

        ChatMessage msg = new ChatMessage(message, sender == null ? -1 : sender.uniqueID);
        HgGame.Network().sendToAllClientsExcept(msg, true, connectionID);
    }

    public ChatMessage() {} // For Kryonet
}
