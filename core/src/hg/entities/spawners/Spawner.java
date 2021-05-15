package hg.entities.spawners;

import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.gamelogic.states.SpawnerState;
import hg.gamelogic.states.State;

public abstract class Spawner extends Entity {
    protected int spawnTime;
    protected int currentTime;
    protected int remainingObjects;

    protected float localOnly_alpha = 0.2f; // Not updated over network

    public void updateCycleAlpha() {
        float target = remainingObjects == 0 ? 0.8f : 0.2f;
        float ratio = (float) currentTime / spawnTime;
        if (ratio > 0.33f) target = 0.4f;
        if (ratio > 0.66f) target = 0.7f;
        if (spawnTime - currentTime < 120) target = 1f;
        localOnly_alpha = (1f - 0.07f) * localOnly_alpha + 0.07f * target;
    }

    public Spawner(int spawnTime, int startingObjectPool) {
        this.spawnTime = spawnTime;
        this.remainingObjects = Math.max(startingObjectPool, 0);
        this.currentTime = 0;
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
        Drawable silhouette = getDrawableIfAny();
        if (silhouette != null) {
            updateCycleAlpha();
            silhouette.setAlpha(localOnly_alpha);
        }
    }

    public void forceSpawn() {
        currentTime = spawnTime;
        remainingObjects = Math.max(remainingObjects, 1);
    }

    public void addSpawns(int count) {
        remainingObjects += count;
    }

    /** This function should be overriden by subclasses to spawn objects.
     * This function is only called by the Spawner if the machine is a server! */
    abstract public void trySpawn();

    @Override
    public State tryGenerateState() {
        SpawnerState stuff = new SpawnerState();
        stuff.copyPosition(this);
        stuff.spawnTime = spawnTime;
        stuff.currentTime = currentTime;
        stuff.remainingObjects = remainingObjects;
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof SpawnerState) {
            SpawnerState stuff = (SpawnerState) state;
            stuff.applyPosition(this);
            spawnTime = stuff.spawnTime;
            remainingObjects = stuff.remainingObjects;
            currentTime = stuff.currentTime;
        }
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }
}
