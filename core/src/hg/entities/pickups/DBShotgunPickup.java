package hg.entities.pickups;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.entities.spawners.Spawner;
import hg.game.HgGame;
import hg.networking.packets.NetInstruction;
import hg.enums.types.TargetType;
import hg.enums.types.WeaponType;

/** DBShotgunPickup gives a Double Barrel Shotgun to the player. */
public class DBShotgunPickup extends Pickup {
    private final BasicSprite shotty;
    private Spawner creator;

    public DBShotgunPickup() {
        super(50);
        shotty = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/WorldDBShotgun.png"));
        shotty.setLayer(DrawLayer.FloorAir);
        shotty.centerToRegion();
        shotty.registerToEngine();

        shotty.setPosition(position);
        shotty.setAngle(angle);

        shotty.setAlpha(0f);

        pickupZone.setPosition(position);
        pickupZone.setAngle(angle);
    }

    public void setCreator(Spawner creator) {
        this.creator = creator;
    }

    @Override
    public void destroy() {
        super.destroy();
        shotty.unregisterFromEngine();
    }

    @Override
    public void update() {
        float fadeIn = shotty.getColor().a;
        if (fadeIn < 1f) shotty.setAlpha(Math.min(fadeIn + 0.05f, 1f));
    }

    @Override
    public void onPickup(PlayerEntity target) {
        NetworkEngine network = HgGame.Network();
        if (creator != null) creator.addSpawns(1);

        if (target != null) {
            target.onWeaponPickup(WeaponType.DBShotgun);
        }

        if (HgGame.Network().isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 3).setInts(WeaponType.DBShotgun);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

}
