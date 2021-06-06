package hg.entities;

import com.badlogic.gdx.graphics.Texture;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.game.HgGame;
import hg.gamelogic.ObjectState;
import hg.maps.Description;

public class GenericSpawner extends Spawner {
    public static class State extends Spawner.State {
        public int entityToSpawn = -1;
    }

    private final BasicSprite silhouette;
    private int entityToSpawn;

    public GenericSpawner(int spawnTime, int entityToSpawn, Texture silhouetteTex) {
        super(spawnTime, 1);
        silhouette = new BasicSprite(silhouetteTex);
        silhouette.setLayer(DrawLayer.FloorAir - 1);
        silhouette.centerToRegion();
        silhouette.registerToEngine();

        silhouette.setPosition(position);
        silhouette.setAngle(angle);

        this.entityToSpawn = entityToSpawn;
    }

    @Override
    public void trySpawn() {
        Entity thing = HgGame.Manager().addActor(entityToSpawn, position, angle.getDeg());
        if (thing instanceof Pickup) {
            ((Pickup) thing).setCreator(this);
        }
    }

    @Override
    public void destroy() {
        silhouette.unregisterFromEngine();
    }

    @Override
    public Drawable getDrawable() {
        return silhouette;
    }

    @Override
    public Drawable[] getDrawableArray() {
        return new Drawable[] { silhouette };
    }

    @Override
    public ObjectState tryGenerateState() {
        GenericSpawner.State stuff = new GenericSpawner.State();
        stuff.copyPosition(this);
        stuff.spawnTime = spawnTime;
        stuff.currentTime = currentTime;
        stuff.remainingObjects = remainingObjects;
        stuff.entityToSpawn = entityToSpawn;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof GenericSpawner.State) {
            GenericSpawner.State stuff = (GenericSpawner.State) state;
            stuff.applyPosition(this);
            spawnTime = stuff.spawnTime;
            remainingObjects = stuff.remainingObjects;
            currentTime = stuff.currentTime;
            startingObjectPool = stuff.startingObjectPool;
            entityToSpawn = stuff.entityToSpawn;
        }
    }

    @Override
    public void tryApplyDescription(Description desc) {
        int[] ints = desc.intParams;
        if (ints != null && ints.length >= 1) spawnTime = ints[0];
    }
}