package hg.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import hg.engine.NetworkEngine;
import hg.entities.GenericBullet;
import hg.entities.PlayerEntity;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.BaseStats;
import hg.networking.packets.*;

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
        kryo.register(MappedActionUpdate.class);
        kryo.register(PlayerViewConnected.class);
        kryo.register(PlayerViewDisconnected.class);
        kryo.register(PlayerViewUpdate.class);
        kryo.register(StateUpdate.class);

        // Other things which are sent / used by packets

        kryo.register(BaseStats.class);
        kryo.register(PlayerView.class);
        kryo.register(PlayerView.Type.class);
        kryo.register(PlayerView[].class);

        // Entity States

        kryo.register(PlayerEntity.PlayerState.class);
        kryo.register(GenericBullet.GenericBulletState.class);
    }

    public static void SendToClients(Packet message) {
        NetworkEngine network = HgGame.Network();
        GameManager manager = HgGame.Manager();

        for (var view: manager.getPlayerViews()) {

        }
    }
}
