package hg.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import hg.networking.packets.ClientInitRequest;
import hg.networking.packets.ClientInitResponse;
import hg.networking.packets.DisconnectNotice;

public class NetworkHelper {
    public static void KryonetRegisterClasses(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(ClientInitRequest.class);
        kryo.register(ClientInitResponse.class);
        kryo.register(DisconnectNotice.class);
    }
}
