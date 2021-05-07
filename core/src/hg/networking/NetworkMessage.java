package hg.networking;

public class NetworkMessage {
    public int connectionID; // Is unused for clients
    public Packet packet;

    public NetworkMessage(int connectionID, Packet packet) {
        this.connectionID = connectionID;
        this.packet = packet;
    }
}
