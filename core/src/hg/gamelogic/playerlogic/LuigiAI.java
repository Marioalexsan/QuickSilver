package hg.gamelogic.playerlogic;

import com.badlogic.gdx.math.Vector2;
import hg.engine.MappedAction;
import hg.entities.PlayerEntity;
import hg.utils.Angle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Luigi wins by doing absolutely nuffin'!
 * NOTE: This AI is really outdated code which should not be used in its current state.
 * TODO If you plan on using this, make it multiplayer-ready
 */
public class LuigiAI extends PlayerLogic {
    private PlayerEntity controlledPlayerEntity;
    private int keksimus = 0;

    private final float rotationSpeed;

    public LuigiAI() {
        rotationSpeed = (float) new Random().nextDouble() * 2.0f + 2.0f;
    }

    @Override
    public void setControlledPlayer(PlayerEntity playerEntity) {
        controlledPlayerEntity = playerEntity;
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
        return controlledPlayerEntity != null ? Angle.NormalVector(controlledPlayerEntity.getAngle().getDeg() - rotationSpeed) : null;
    }

    @Override
    public Vector2 obtainAimPosition() {
        return controlledPlayerEntity != null ? Angle.NormalVector(controlledPlayerEntity.getAngle().getDeg() - rotationSpeed) : null;
    }

    @Override
    public void update() {
        keksimus = (keksimus + 1) % 120;
    }
}