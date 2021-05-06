package hg.playerlogic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.entities.PlayerEntity;
import hg.interfaces.IPlayerLogic;

import java.util.ArrayList;
import java.util.List;

public class LocalPlayerLogic implements IPlayerLogic {
    private PlayerEntity controlledPlayerEntity;

    @Override
    public void setControlledPlayer(PlayerEntity playerEntity) {
        controlledPlayerEntity = playerEntity;
        if (playerEntity == null) HgGame.Input().removeFocusInput(this);
        else HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.PlayerInputs);
    }

    @Override
    public List<Integer> obtainActions() {
        InputEngine input = HgGame.Input();

        boolean hasFocus = HgGame.Input().inputHasFocus(this);

        // Returns the current actions after an update
        ArrayList<Integer> actionsThisFrame = new ArrayList<>();

        if (hasFocus) {
            if (input.isActionHeld(MappedAction.MoveUp)) actionsThisFrame.add(MappedAction.MoveUp);
            if (input.isActionHeld(MappedAction.MoveDown)) actionsThisFrame.add(MappedAction.MoveDown);
            if (input.isActionHeld(MappedAction.MoveLeft)) actionsThisFrame.add(MappedAction.MoveLeft);
            if (input.isActionHeld(MappedAction.MoveRight)) actionsThisFrame.add(MappedAction.MoveRight);

            if (input.isActionHeld(MappedAction.Reload)) actionsThisFrame.add(MappedAction.Reload);

            if (input.isButtonHeld(Input.Buttons.LEFT)) actionsThisFrame.add(MappedAction.PrimaryFire);
            if (input.isButtonHeld(Input.Buttons.RIGHT)) actionsThisFrame.add(MappedAction.SecondaryFire);

            if (input.isActionTapped(MappedAction.QuickSwitchWeapon)) actionsThisFrame.add(MappedAction.QuickSwitchWeapon);
        }

        return actionsThisFrame;
    }

    @Override
    public Vector2 obtainAimPosition() {
        // Returns the current aim position after an update, relative to the player
        return HgGame.Input().inputHasFocus(this) ? HgGame.Input().getFOVWorldMouse(HgGame.Game().getFOVFactor()).sub(controlledPlayerEntity.getPosition()) : null;
    }
}
