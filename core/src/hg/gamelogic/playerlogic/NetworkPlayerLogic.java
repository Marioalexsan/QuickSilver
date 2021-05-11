package hg.gamelogic.playerlogic;

import com.badlogic.gdx.math.Vector2;
import hg.engine.NetworkEngine;
import hg.game.HgGame;
import hg.networking.packets.InputUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This player logic processes inputs from a network player
 */
public class NetworkPlayerLogic extends PlayerLogic {
    private final int timeToHoldInput = 4;
    private final HashMap<Integer, Integer> mappedActionHold = new HashMap<>();
    private final Vector2 aimPosition = new Vector2(0, 0);

    public void receiveActions(int[] mappedActions) {
        for (int action: mappedActions) {
            mappedActionHold.put(action, timeToHoldInput);
        }
    }

    public void receiveAimPosition(Vector2 position) {
        aimPosition.set(position);
    }

    @Override
    public void update() {
        for (var action: mappedActionHold.entrySet()) {
            if (action.getValue() > 0) action.setValue(action.getValue() - 1);
        }
    }

    @Override
    public List<Integer> obtainActions() {
        ArrayList<Integer> inputs = new ArrayList<>();
        for (var action: mappedActionHold.entrySet()) {
            if (action.getValue() > 0) inputs.add(action.getKey());
        }
        return inputs;
    }

    @Override
    public Vector2 obtainAimPosition() {
        return aimPosition;
    }
}
