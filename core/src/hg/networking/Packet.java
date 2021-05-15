package hg.networking;

public abstract class Packet {
    public void parseOnClient() {}
    public void parseOnServer(int connectionID) {}
}
