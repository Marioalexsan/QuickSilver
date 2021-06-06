package hg.libraries;

import hg.engine.AssetEngine;
import hg.entities.*;
import hg.enums.TargetType;
import hg.enums.WeaponType;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.enums.ActorType;
import hg.networking.packets.NetInstruction;

/**
 * Holds descriptions of entities that are related to the gameplay.
 * Example: Player, NPCs, etc.
 */
public class ActorLibrary {
    public static Entity CreateActor(int type) {
        AssetEngine assets = HgGame.Assets();
        Entity which = switch(type) {
            case ActorType.PlayerEntity -> new PlayerEntity(new EmptyAI());
            case ActorType.Bullet -> new Bullet();
            case ActorType.AssaultRifleSpawner -> new GenericSpawner(1200, ActorType.AssaultRiflePickup, assets.loadTexture("Assets/Sprites/Pickups/RifleSilhouette.png"));
            case ActorType.AmmoPackSpawner -> new GenericSpawner(1200, ActorType.AmmoPackPickup, assets.loadTexture("Assets/Sprites/Pickups/AmmoPackSilhouette.png"));
            case ActorType.MedkitSpawner -> new GenericSpawner(2400, ActorType.MedkitPickup, assets.loadTexture("Assets/Sprites/Pickups/MedkitSilhouette.png"));
            case ActorType.ArmorPlateSpawner -> new GenericSpawner(1800, ActorType.ArmorPlatePickup, assets.loadTexture("Assets/Sprites/Pickups/APlateSilhouette.png"));
            case ActorType.KevlarVestSpawner -> new GenericSpawner(1800, ActorType.KevlarVestPickup, assets.loadTexture("Assets/Sprites/Pickups/KevlarVestSilhouette.png"));
            case ActorType.HeavyArmorSpawner -> new GenericSpawner(4800, ActorType.HeavyArmorPickup, assets.loadTexture("Assets/Sprites/Pickups/HeavyArmorSilhouette.png"));
            case ActorType.DBShotgunSpawner -> new GenericSpawner(2400, ActorType.DBShotgunPickup, assets.loadTexture("Assets/Sprites/Pickups/DBShotgunSilhouette.png"));

            case ActorType.AssaultRiflePickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/WorldRifle.png"), (target) -> {
                if (target == null) return;
                target.onWeaponPickup(WeaponType.AssaultRifle);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 3).setInts(WeaponType.AssaultRifle);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.AmmoPackPickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/AmmoPack.png"), (target) -> {
                if (target == null) return;
                target.onAmmoPickup(WeaponType.AllWeapons);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 4).setInts(WeaponType.AllWeapons);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.MedkitPickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/Medkit.png"), (target) -> {
                if (target == null) return;
                final float healAmount = 20f;
                target.heal(healAmount);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 5).setFloats(healAmount);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.ArmorPlatePickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/APlate.png"), (target) -> {
                if (target == null) return;
                final int plateCount = 1;
                target.obtainArmorPlates(plateCount);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 6).setInts(plateCount);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.KevlarVestPickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/KevlarVest.png"), (target) -> {
                if (target == null) return;
                target.obtainVest();

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 7);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.HeavyArmorPickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/HeavyArmor.png"), (target) -> {
                if (target == null) return;
                target.obtainHeavyArmor(100f);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 8).setFloats(100f);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            case ActorType.DBShotgunPickup -> new GenericPickup(assets.loadTexture("Assets/Sprites/Pickups/WorldDBShotgun.png"), (target) -> {
                if (target == null) return;
                target.onWeaponPickup(WeaponType.DBShotgun);

                if (HgGame.Network().isLocalOrServer()) {
                    NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 3).setInts(WeaponType.DBShotgun);
                    HgGame.Network().sendToAllClients(msg, true);
                }
            });

            default -> null;
        };
        if (which != null) which.setType(type);
        return which;
    }
}
