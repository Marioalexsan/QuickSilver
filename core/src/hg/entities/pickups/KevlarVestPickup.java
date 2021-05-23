package hg.entities.pickups;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.NetworkEngine;
import hg.entities.PlayerEntity;
import hg.entities.spawners.Spawner;
import hg.game.HgGame;
import hg.networking.packets.NetInstruction;
import hg.enums.types.TargetType;

/** KevlarVestPickup gives a kevlar vest to the player. */
public class KevlarVestPickup extends Pickup {
    private final BasicSprite box;
    private Spawner creator;

    public KevlarVestPickup() {
        super(50);
        box = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/KevlarVest.png"));
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
            target.obtainVest();
        }

        if (HgGame.Network().isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, target.getID(), 7);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }
}