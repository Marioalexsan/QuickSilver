package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.AssaultRiflePickup;
import hg.entities.pickups.DBShotgunPickup;
import hg.game.HgGame;
import hg.enums.types.ActorType;

/** Spawns DBShotgunPickups */
public class DBShotgunSpawner extends Spawner {
    private final BasicSprite silhouette;

    public DBShotgunSpawner() {
        super(2400, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/DBShotgunSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        DBShotgunPickup pickup = (DBShotgunPickup) HgGame.Manager().addActor(ActorType.DBShotgunPickup, position, angle.getDeg());
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