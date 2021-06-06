package hg.networking;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import hg.directors.GameSession;
import hg.entities.*;
import hg.gamelogic.BaseStats;
import hg.gamelogic.gamemodes.Deathmatch;
import hg.networking.packets.*;
import hg.weapons.AssaultRifle;
import hg.weapons.DBShotgun;
import hg.weapons.Revolver;

import java.util.HashMap;

/** Helper class for network actions. */
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
        kryo.register(SessionSettingsUpdate.class);
        kryo.register(StateUpdate.class);

        // Other things which are sent / used by packets

        kryo.register(BaseStats.class);
        kryo.register(PlayerView.class);
        kryo.register(PlayerView.Type.class);
        kryo.register(PlayerView[].class);

        // States

        kryo.register(AssaultRifle.State.class);
        kryo.register(Bullet.State.class);
        kryo.register(DBShotgun.State.class);
        kryo.register(Deathmatch.State.class);
        kryo.register(Pickup.State.class);
        kryo.register(PlayerEntity.State.class);
        kryo.register(Revolver.State.class);
        kryo.register(Spawner.State.class);
        kryo.register(GenericSpawner.State.class);
        kryo.register(GenericPickup.State.class);

        // Other

        kryo.register(Vector2.class);
        kryo.register(EntityAdded[].class);
        kryo.register(GameSession.SessionOptions.class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(HashMap.class);
    }
}
