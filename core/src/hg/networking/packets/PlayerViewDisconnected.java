package hg.networking.packets;

import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

public class PlayerViewDisconnected extends Packet {
    public int deadUniqueID;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        PlayerView deadView = manager.removePlayerView(deadUniqueID);
        HgGame.Manager().getChatSystem().addMessage(deadView.name + " disconnected.");
    }
}
