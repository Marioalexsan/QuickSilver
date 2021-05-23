package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.AmmoPackPickup;
import hg.game.HgGame;
import hg.enums.types.ActorType;

/** Spawns AmmoPackPickups */
public class AmmoPackSpawner extends Spawner {
    private final BasicSprite silhouette;

    public AmmoPackSpawner() {
        super(1200, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/AmmoPackSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        AmmoPackPickup pickup = (AmmoPackPickup) HgGame.Manager().addActor(ActorType.AmmoPackPickup, position, angle.getDeg());
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
