package hg.entities.pickups;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.entities.pickups.Pickup;
import hg.entities.spawners.Spawner;
import hg.game.HgGame;
import hg.networking.packets.NetInstruction;
import hg.types.TargetType;
import hg.types.WeaponType;

public class MedkitPickup extends Pickup {
    private final BasicSprite box;
    private Spawner creator;

    private final float healAmount = 20;

    public MedkitPickup() {
        super(50);
        box = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/Medkit.png"));
        box.setLayer(DrawLayer.FloorAir);
        box.centerToRegion();
        box.registerToEngine();

        box.setPosition(position);
        box.setAngle(angle);

        box.setAlpha(0f);

        pickupZone.setPosition(position);
        pickupZone.setAngle(angle);
    }

    public void setCreator(Spawner creator) {
        this.creator = creator;
    }

    @Override
    public void destroy() {
        super.destroy();
        box.unregisterFromEngine();
    }

    @Override
    public void update() {
        float fadeIn = box.getColor().a;
        if (fadeIn < 1f) box.setAlpha(Math.min(fadeIn + 0.05f, 1f));
    }

    @Override
    public void onPickup(PlayerEntity target) {
        NetworkEngine network = HgGame.Network();
        if (creator != null) creator.addSpawns(1);

        if (target != null) {
            target.heal(healAmount);
        }

        if (HgGame.Network().isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 5).setFloats(healAmount);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

}
