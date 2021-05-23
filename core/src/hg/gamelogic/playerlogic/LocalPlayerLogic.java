package hg.gamelogic.playerlogic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.engine.NetworkEngine;
import hg.game.HgGame;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.entities.PlayerEntity;
import hg.networking.packets.InputUpdate;

import java.util.ArrayList;
import java.util.List;

/** Player Logic that converts keyboard and mouse input to actions */
public class LocalPlayerLogic extends PlayerLogic {
    private PlayerEntity controlledPlayerEntity;

    public void sendActions() {
        NetworkEngine network = HgGame.Network();
        List<Integer> actions = obtainActions();
        int[] mappedActions = new int[actions.size()];
        for (int i = 0; i < mappedActions.length; i++)
            mappedActions[i] = actions.get(i);

        Vector2 aimPosition = obtainAimPosition();
        if (aimPosition == null) aimPosition = new Vector2();
        InputUpdate msg = new InputUpdate(mappedActions, aimPosition.x, aimPosition.y);

        if (network.isLocalOrServer()) {
            msg.uniqueID = HgGame.Manager().localView.uniqueID;
            network.sendToAllClients(msg, false);
        }
        else network.sendToServer(msg, false);
    }

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

            if (input.isActionTapped(MappedAction.WeaponOne)) actionsThisFrame.add(MappedAction.WeaponOne);
            if (input.isActionTapped(MappedAction.WeaponTwo)) actionsThisFrame.add(MappedAction.WeaponTwo);
            if (input.isActionTapped(MappedAction.WeaponThree)) actionsThisFrame.add(MappedAction.WeaponThree);
            if (input.isActionTapped(MappedAction.WeaponFour)) actionsThisFrame.add(MappedAction.WeaponFour);

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
