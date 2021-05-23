package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.KevlarVestPickup;
import hg.game.HgGame;
import hg.enums.types.ActorType;

/** Spawns KevlarVestPickups */
public class KevlarVestSpawner extends Spawner {
    private final BasicSprite silhouette;

    public KevlarVestSpawner() {
        super(4000, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/KevlarVestSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        KevlarVestPickup pickup = (KevlarVestPickup) HgGame.Manager().addActor(ActorType.KevlarVestPickup, position, angle.getDeg());
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
