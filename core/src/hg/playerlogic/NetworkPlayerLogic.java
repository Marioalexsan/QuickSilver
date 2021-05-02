package hg.playerlogic;

import com.badlogic.gdx.math.Vector2;
import hg.engine.MappedAction;
import hg.interfaces.IPlayerLogic;

import java.util.List;

/**
 * This player logic processes inputs from a network player
 */
public class NetworkPlayerLogic implements IPlayerLogic {
    @Override
    public void localUpdate() {}

    @Override
    public List<Integer> obtainActions() {
        // TO DO
        return null;
    }

    @Override
    public Vector2 obtainAimPosition() {
        // TO DO
        return null;
    }
}
