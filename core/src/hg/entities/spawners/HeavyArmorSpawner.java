package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.HeavyArmorPickup;
import hg.game.HgGame;
import hg.enums.types.ActorType;

/** Spawns HeavyArmorPickups */
public class HeavyArmorSpawner extends Spawner {
    private final BasicSprite silhouette;

    public HeavyArmorSpawner() {
        super(4800, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/HeavyArmorSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        HeavyArmorPickup pickup = (HeavyArmorPickup) HgGame.Manager().addActor(ActorType.HeavyArmorPickup, position, angle.getDeg());
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
