package hg.entities.spawners;

        import hg.drawables.BasicSprite;
        import hg.drawables.DrawLayer;
        import hg.drawables.Drawable;
        import hg.entities.pickups.ArmorPlatePickup;
        import hg.game.HgGame;
        import hg.enums.types.ActorType;

public class ArmorPlateSpawner extends Spawner {
    private final BasicSprite silhouette;

    public ArmorPlateSpawner() {
        super(1800, 1);
        silhouette = new BasicSprite(HgGame.Assets().loadTexture("Assets/Sprites/Pickups/APlateSilhouette.png"));
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);
    }

    @Override
    public void trySpawn() {
        ArmorPlatePickup pickup = (ArmorPlatePickup) HgGame.Manager().addActor(ActorType.ArmorPlatePickup, position, angle.getDeg());
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