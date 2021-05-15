package hg.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import hg.gamelogic.BaseStats;
import hg.gamelogic.states.*;
import hg.networking.packets.*;

import java.util.HashMap;

public class NetworkHelper {
    public static void KryonetRegisterClasses(EndPoint point) {
        Kryo kryo = point.getKryo();
        // The order of registering could be changed to improve serialization!

        // Packets

        kryo.register(ChatMessage.class);
        kryo.register(ClientInitRequest.class);
        kryo.register(ClientInitResponse.class);
        kryo.register(DisconnectNotice.class);
        kryo.register(EntityAdded.class);
        kryo.register(EntityDestroyed.class);
        kryo.register(GameSessionStart.class);
        kryo.register(InputUpdate.class);
        kryo.register(NetInstruction.class);
        kryo.register(PlayerViewConnected.class);
        kryo.register(PlayerViewDisconnected.class);
        kryo.register(PlayerViewUpdate.class);
        kryo.register(PositionUpdate.class);
        kryo.register(StateUpdate.class);

        // Other things which are sent / used by packets

        kryo.register(BaseStats.class);
        kryo.register(PlayerView.class);
        kryo.register(PlayerView.Type.class);
        kryo.register(PlayerView[].class);

        // States

        kryo.register(AssaultRifleState.class);
        kryo.register(DeathmatchState.class);
        kryo.register(BulletState.class);
        kryo.register(PickupState.class);
        kryo.register(PlayerState.class);
        kryo.register(RevolverState.class);
        kryo.register(SpawnerState.class);

        // Other

        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(HashMap.class);
    }
}
