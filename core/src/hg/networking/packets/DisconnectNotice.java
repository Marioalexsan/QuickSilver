package hg.networking.packets;

import hg.networking.Packet;

/** UNUSEd */
public class DisconnectNotice extends Packet {
    public String reason = "unknown";
    public int uniqueID = -1;

    public DisconnectNotice(String reason, int uniqueID) {
        this.reason = reason;
        this.uniqueID = uniqueID;
    }

    @Override
    public void parseOnClient() {

    }

    public DisconnectNotice() {} // For Kryonet
}