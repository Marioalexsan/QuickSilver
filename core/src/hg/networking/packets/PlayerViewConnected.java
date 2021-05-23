package hg.networking.packets;

import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

/** Tells clients a new player connected */
public class PlayerViewConnected extends Packet {
    public PlayerView newView;

    public PlayerViewConnected(PlayerView newView) {
        this.newView = newView;
    }

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        manager.addPlayerView(newView);
        HgGame.Chat().addMessage(newView.name + " connected.");
    }

    public PlayerViewConnected() {} // For Kryonet
}
