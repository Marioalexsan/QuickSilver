package hg.networking;

import com.esotericsoftware.kryonet.Connection;

public class ConnectedClient extends Connection {
    public float timeSinceLastMessage = 0f;
    public boolean aknowledged = false;
}
