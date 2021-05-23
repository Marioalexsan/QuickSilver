package hg.networking;

import com.esotericsoftware.kryonet.Connection;

/** NOT REALLY USED */
public class ConnectedClient extends Connection {
    public boolean aknowledged = false;
    public PlayerView playerView = null;
}
