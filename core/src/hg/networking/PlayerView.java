package hg.networking;

import com.esotericsoftware.kryonet.Connection;
import hg.entities.PlayerEntity;

public class PlayerView {
    public enum Type {
        HostAI,
        Host,
        Client
    }

    public int uniqueID;
    public Type viewType;

    public Connection netConnection;

    public String name;
    public transient PlayerEntity playerEntity;

    public boolean serverInited;

    public void onAdd() {

    }

    public void onRemove() {

    }
}
