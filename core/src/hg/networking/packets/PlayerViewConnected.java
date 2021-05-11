package hg.networking.packets;

import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

public class PlayerViewConnected extends Packet {
    public PlayerView newView;

    public PlayerViewConnected(PlayerView newView) {
        this.newView = newView;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        manager.addPlayerView(newView);
        HgGame.Manager().getChatSystem().addMessage(newView.name + " connected.");
    }

    public PlayerViewConnected() {} // For Kryonet
}
