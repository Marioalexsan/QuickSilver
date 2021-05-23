package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.AssaultRiflePickup;
import hg.game.HgGame;
import hg.enums.types.ActorType;

/** Spawns AssaultRiflePickups */
public class AssaultRifleSpawner extends Spawner {
    private final BasicSprite silhouette;

    public AssaultRifleSpawner() {
        super(2400, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/RifleSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        AssaultRiflePickup pickup = (AssaultRiflePickup) HgGame.Manager().addActor(ActorType.AssaultRiflePickup, position, angle.getDeg());
        pickup.setCreator(this);
    }

    @Override
    public void destroy() {
        silhouette.unregisterFromEngine();
    }

    @Override
    public Drawable getDrawableIfAny() {
        return silhouette;
    }
}
