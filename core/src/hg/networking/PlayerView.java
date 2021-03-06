package hg.networking;

import hg.entities.PlayerEntity;

/** PlayerView represents a connected and instantiated client */
public class PlayerView {
    public enum Type {
        HostAI,
        Host,
        Client
    }

    public int uniqueID;
    public Type viewType;

    public String name;

    public transient int connectionID = -1;
    public transient PlayerEntity playerEntity;

    public boolean serverInited;
}
