package hg.libraries;

import hg.entities.*;
import hg.entities.pickups.AmmoPackPickup;
import hg.entities.pickups.ArmorPlatePickup;
import hg.entities.pickups.AssaultRiflePickup;
import hg.entities.pickups.MedkitPickup;
import hg.entities.spawners.AmmoPackSpawner;
import hg.entities.spawners.ArmorPlateSpawner;
import hg.entities.spawners.AssaultRifleSpawner;
import hg.entities.spawners.MedkitSpawner;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.types.ActorType;

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
        }
        return which;
    }
}
