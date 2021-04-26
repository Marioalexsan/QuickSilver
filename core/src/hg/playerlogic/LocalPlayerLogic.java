package hg.playerlogic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.entities.Player;
import hg.interfaces.IPlayerLogic;

import java.util.ArrayList;
import java.util.List;

public class LocalPlayerLogic implements IPlayerLogic {
    private Player controlledPlayer;

    @Override
    public void setControlledPlayer(Player player) {
        controlledPlayer = player;
    }

    @Override
    public List<MappedAction> obtainActions() {
        InputEngine input = HgGame.Input();

        // Returns the current actions after an update
        ArrayList<MappedAction> actionsThisFrame = new ArrayList<>();
        if (input.isActionHeld(MappedAction.MoveUp)) actionsThisFrame.add(MappedAction.MoveUp);
        if (input.isActionHeld(MappedAction.MoveDown)) actionsThisFrame.add(MappedAction.MoveDown);
        if (input.isActionHeld(MappedAction.MoveLeft)) actionsThisFrame.add(MappedAction.MoveLeft);
        if (input.isActionHeld(MappedAction.MoveRight)) actionsThisFrame.add(MappedAction.MoveRight);

        if (input.isActionHeld(MappedAction.Reload)) actionsThisFrame.add(MappedAction.Reload);

        if (input.isButtonHeld(Input.Buttons.LEFT)) actionsThisFrame.add(MappedAction.PrimaryFire);
        if (input.isButtonHeld(Input.Buttons.RIGHT)) actionsThisFrame.add(MappedAction.SecondaryFire);

        if (input.isActionTapped(MappedAction.QuickSwitchWeapon)) actionsThisFrame.add(MappedAction.QuickSwitchWeapon);

        return actionsThisFrame;
    }

    @Override
    public Vector2 obtainAimPosition() {
        // Returns the current aim position after an update, relative to the player
        return HgGame.Input().getFOVWorldMouse(HgGame.Game().getFOVFactor()).sub(controlledPlayer.getPosition());
    }
}
