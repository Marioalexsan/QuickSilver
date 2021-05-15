package hg.entities.pickups;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.entities.spawners.Spawner;
import hg.game.HgGame;
import hg.networking.packets.NetInstruction;
import hg.types.TargetType;
import hg.types.WeaponType;

public class AssaultRiflePickup extends Pickup {
    private final BasicSprite rifle;
    private Spawner creator;

    public AssaultRiflePickup() {
        super(50);
        rifle = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/WorldRifle.png"));
        rifle.setLayer(DrawLayer.FloorAir);
        rifle.centerToRegion();
        rifle.registerToEngine();

        rifle.setPosition(position);
        rifle.setAngle(angle);

        rifle.setAlpha(0f);

        pickupZone.setPosition(position);
        pickupZone.setAngle(angle);
    }

    public void setCreator(Spawner creator) {
        this.creator = creator;
    }

    @Override
    public void destroy() {
        super.destroy();
        rifle.unregisterFromEngine();
    }

    @Override
    public void update() {
        float fadeIn = rifle.getColor().a;
        if (fadeIn < 1f) rifle.setAlpha(Math.min(fadeIn + 0.05f, 1f));
    }

    @Override
    public void onPickup(PlayerEntity target) {
        NetworkEngine network = HgGame.Network();
        if (creator != null) creator.addSpawns(1);

        if (target != null) {
            target.onWeaponPickup(WeaponType.AssaultRifle);
        }

        if (HgGame.Network().isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 3).setInts(WeaponType.AssaultRifle);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

}
