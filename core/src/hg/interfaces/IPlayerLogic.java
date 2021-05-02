package hg.interfaces;


import com.badlogic.gdx.math.Vector2;
import hg.engine.MappedAction;
import hg.entities.Player;

import java.util.List;

/** IPlayerLogic commands a player entity by providing actions and input for it.
 * A PlayerLogic shall NOT directly manipulate the player entity! It should only read its state to make input decisions
 */
public interface IPlayerLogic {

    default void setControlledPlayer(Player entity) {}

    default void localUpdate() {}

    default Vector2 obtainAimPosition() { return null; }

    default List<Integer> obtainActions() { return null; }

    default Vector2 obtainAdvancedMove() { return null; }

}
