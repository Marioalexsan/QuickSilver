package hg.networking.packets;

import hg.game.HgGame;
import hg.networking.Packet;

public class DisconnectNotice extends Packet {
    public String reason = "unknown";
    @Override
    public void parseOnClient() {
        HgGame.Manager().onDisconnectFromServer();
    }
}