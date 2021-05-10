package hg.networking.packets;

import hg.game.HgGame;
import hg.networking.Packet;

public class DisconnectNotice extends Packet {
    public String reason = "unknown";
    public int uniqueID = -1;

    @Override
    public void parseOnClient() {

    }
}