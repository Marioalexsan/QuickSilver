package hg.entities.spawners;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.entities.pickups.AmmoPackPickup;
import hg.entities.pickups.MedkitPickup;
import hg.game.HgGame;
import hg.types.ActorType;

public class MedkitSpawner extends Spawner {
    private final BasicSprite silhouette;

    public MedkitSpawner() {
        super(1800, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/MedkitSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        MedkitPickup pickup = (MedkitPickup) HgGame.Manager().addActor(ActorType.MedkitPickup, position, angle.getDeg());
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
