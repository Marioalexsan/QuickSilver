package hg.playerlogic;

import com.badlogic.gdx.math.Vector2;
import hg.engine.MappedAction;
import hg.entities.Player;
import hg.game.HgGame;
import hg.interfaces.IPlayerLogic;
import hg.utils.Angle;

import java.util.ArrayList;
import java.util.List;


/**
 * Luigi wins by doing absolutely nuffin'!
 * (testing AI)
 */
public class LuigiAI implements IPlayerLogic {
    private Player controlledPlayer;
    private int keksimus = 0;

    private final float rotationSpeed;

    public LuigiAI() {
        rotationSpeed = (float) HgGame.getLogicRandom() * 2.0f + 2.0f;
    }

    @Override
    public void setControlledPlayer(Player player) {
        controlledPlayer = player;
    }

    @Override
    public List<Integer> obtainActions() {
        ArrayList<Integer> actionsThisFrame = new ArrayList<>();

        if (keksimus <= 3)
            actionsThisFrame.add(MappedAction.Reload);
        else
            actionsThisFrame.add(MappedAction.PrimaryFire);

        return actionsThisFrame;
    }

    @Override
    public Vector2 obtainAdvancedMove() {
        return controlledPlayer != null ? Angle.NormalVector(controlledPlayer.getAngle().getDeg() - rotationSpeed) : null;
    }

    @Override
    public Vector2 obtainAimPosition() {
        return controlledPlayer != null ? Angle.NormalVector(controlledPlayer.getAngle().getDeg() - rotationSpeed) : null;
    }

    @Override
    public void localUpdate() {
        keksimus = (keksimus + 1) % 120;
    }
}