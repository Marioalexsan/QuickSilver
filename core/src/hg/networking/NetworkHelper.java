package hg.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

public class NetworkHelper {
    public static void KryonetRegisterClasses(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(Packets.ClientInitRequest.class);
        kryo.register(Packets.ClientInitResponse.class);
        kryo.register(Packets.DisconnectNotice.class);
    }
}
