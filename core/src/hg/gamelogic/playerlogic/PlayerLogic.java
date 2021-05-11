package hg.gamelogic.playerlogic;


import com.badlogic.gdx.math.Vector2;
import hg.entities.PlayerEntity;
import hg.interfaces.IUpdateable;

import java.util.List;

/** IPlayerLogic commands a player entity by providing actions and input for it.
 * A PlayerLogic shall NOT directly manipulate the player entity! It should only read its state to make input decisions
 */
public abstract class PlayerLogic implements IUpdateable {

    public void setControlledPlayer(PlayerEntity entity) {}

    public Vector2 obtainAimPosition() { return null; }

    public List<Integer> obtainActions() { return null; }

    public Vector2 obtainAdvancedMove() { return null; }

}
