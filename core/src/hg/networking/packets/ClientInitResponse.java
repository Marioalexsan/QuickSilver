package hg.networking.packets;

import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.networking.PlayerView;

public class ClientInitResponse extends Packet {
    public int clientViewID;
    public PlayerView[] allViews;

    public ClientInitResponse(int clientViewID, PlayerView[] allViews) {
        this.clientViewID = clientViewID;
        this.allViews = allViews;
    }

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

    public ClientInitResponse() {} // For Kryonet
}
