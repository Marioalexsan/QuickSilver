package hg.networking;

/** Packets are objects that can be sent over the network, and have parsers defined for them. */
public abstract class Packet {
    public void parseOnClient() {}
    public void parseOnServer(int connectionID) {}
}
