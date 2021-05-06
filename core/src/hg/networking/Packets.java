package hg.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Packets {
    public static class ConnectionDenied {
        public String reason = "unknown";
    }

    public static class ClientInitRequest {

    }

    public static class ClientInitResponse {

    }

    public static class DisconnectNotice {

    }
}
