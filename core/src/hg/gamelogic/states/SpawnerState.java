package hg.gamelogic.states;

/** Holds the network state of a Spawner */
public class SpawnerState extends PositionState {
    public int spawnTime;
    public int currentTime;
    public int remainingObjects;
    public int startingObjectPool;
}
