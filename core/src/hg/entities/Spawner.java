package hg.entities;

import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.gamelogic.ObjectState;
import hg.maps.Description;

/** Generic world entity that can spawn stuff, usually pickups. */
public abstract class Spawner extends Entity {
    public static class State extends ObjectState.PositionState {
        public int spawnTime;
        public int currentTime;
        public int remainingObjects;
        public int startingObjectPool;
    }

    protected int spawnTime;
    protected int currentTime;
    protected int remainingObjects;
    protected int startingObjectPool;

    protected float localOnly_alpha = 0.2f; // Not updated over network

    public void updateCycleAlpha() {
        float target = remainingObjects == 0 ? 0.8f : 0.2f;
        float ratio = (float) currentTime / spawnTime;
        if (ratio > 0.5f) target = 0.4f;
        if (spawnTime - currentTime < 180) target = 1f;
        localOnly_alpha = (1f - 0.07f) * localOnly_alpha + 0.07f * target;
    }

    public Spawner(int spawnTime, int startingObjectPool) {
        this.spawnTime = spawnTime;
        this.startingObjectPool = startingObjectPool;
        this.remainingObjects = Math.max(startingObjectPool, 0);
        this.currentTime = 0;
    }

    public void editSpawntime(int spawnTime) {
        this.spawnTime = spawnTime;
    }

    public void editObjectPool(int objectPool) {
        this.startingObjectPool = objectPool;
    }

    @Override
    public void update() {
        if (remainingObjects > 0) {
            if (currentTime >= spawnTime) {
                if (HgGame.Network().isLocalOrServer()) trySpawn();
                remainingObjects--;
                currentTime = 0;
            }
            currentTime++;
        }
        Drawable silhouette = getDrawableArray().length > 0 ? getDrawableArray()[0] : null;
        if (silhouette != null) {
            updateCycleAlpha();
            silhouette.setAlpha(localOnly_alpha);
        }
    }

    public void forceSpawn() {
        currentTime = spawnTime;
        remainingObjects = Math.max(remainingObjects, 1);
    }

    public void reset() {
        this.remainingObjects = Math.max(startingObjectPool, 0);
        this.currentTime = 0;
    }

    public void addSpawns(int count) {
        remainingObjects += count;
    }

    public void advanceFrames(int frames) {
        currentTime += Math.max(frames, 0);
    }

    public void advanceRatio(float ratio) {
        currentTime += Math.max(ratio * spawnTime, 0);
    }


    /** This function should be overriden by subclasses to spawn objects.
     * This function is only called by the Spawner if the machine is a server! */
    abstract public void trySpawn();

    @Override
    public ObjectState tryGenerateState() {
        State stuff = new State();
        stuff.copyPosition(this);
        stuff.spawnTime = spawnTime;
        stuff.currentTime = currentTime;
        stuff.remainingObjects = remainingObjects;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            stuff.applyPosition(this);
            spawnTime = stuff.spawnTime;
            remainingObjects = stuff.remainingObjects;
            currentTime = stuff.currentTime;
            startingObjectPool = stuff.startingObjectPool;
        }
    }

    @Override
    public void tryApplyDescription(Description desc) {
        int[] ints = desc.intParams;
        if (ints != null && ints.length >= 1) spawnTime = ints[0];
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }
}
