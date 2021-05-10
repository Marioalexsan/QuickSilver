package hg.networking.packets;

import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

public class ClientInitResponse extends Packet {
    public int clientViewID;
    public PlayerView[] allViews;

    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        for (var view : allViews) {
            manager.addPlayerView(view);
            if (view.uniqueID == clientViewID) {
                manager.localView = view;
            }
        }
    }
}
