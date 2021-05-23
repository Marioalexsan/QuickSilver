package hg.libraries;

import hg.entities.*;
import hg.entities.pickups.*;
import hg.entities.spawners.*;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.enums.types.ActorType;

/**
 * Holds descriptions of entities that are related to the gameplay.
 * Example: Player, NPCs, etc.
 */
public class ActorLibrary {
    public static Entity CreateActor(int type) {
        Entity which = null;
        switch(type) {
            case ActorType.PlayerEntity -> which = new PlayerEntity(new EmptyAI());
            case ActorType.Bullet -> which = new Bullet();
            case ActorType.AssaultRifleSpawner -> which = new AssaultRifleSpawner();
            case ActorType.AssaultRiflePickup -> which = new AssaultRiflePickup();
            case ActorType.AmmoPackSpawner -> which = new AmmoPackSpawner();
            case ActorType.AmmoPackPickup -> which = new AmmoPackPickup();
            case ActorType.MedkitSpawner -> which = new MedkitSpawner();
            case ActorType.MedkitPickup -> which = new MedkitPickup();
            case ActorType.ArmorPlateSpawner -> which = new ArmorPlateSpawner();
            case ActorType.ArmorPlatePickup -> which = new ArmorPlatePickup();
            case ActorType.KevlarVestSpawner -> which = new KevlarVestSpawner();
            case ActorType.KevlarVestPickup -> which = new KevlarVestPickup();
            case ActorType.HeavyArmorSpawner -> which = new HeavyArmorSpawner();
            case ActorType.HeavyArmorPickup -> which = new HeavyArmorPickup();
            case ActorType.DBShotgunSpawner -> which = new DBShotgunSpawner();
            case ActorType.DBShotgunPickup -> which = new DBShotgunPickup();
        }
        if (which != null) which.setType(type);
        return which;
    }
}
