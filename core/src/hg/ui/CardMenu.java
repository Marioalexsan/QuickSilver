package hg.ui;

import com.badlogic.gdx.Input;
import hg.drawables.Drawable;
import hg.game.HgGame;
import hg.interfaces.IDestroyable;
import hg.interfaces.IEnable;

import java.util.HashMap;
import java.util.LinkedList;

/** CardMenu is a simple way of creating menus by switching between states (submenus) */
public class CardMenu extends UIElement {
    private final HashMap<String, LinkedList<Object>> states = new HashMap<>();

    private String currentState = "";
    private String scheduledState = "";

    @Override
    public void onUpdate() {
        var pos = HgGame.Input().getMouse();
        var clicked = HgGame.Input().isButtonTapped(Input.Buttons.LEFT);

        for (var elementLists: states.values()) {
            for (var element : elementLists) {
                if (element instanceof UIElement) {
                    UIElement ui = (UIElement) element;

                    if (clicked)
                        ui.onLMBDown(pos.x, pos.y);
                    ui.onUpdate();
                }
            }
        }

        // State switches are best done after an update ends, to prevent "double interactions"
        if (!scheduledState.isEmpty() && states.containsKey(scheduledState)) {
            switchState(scheduledState);
            scheduledState = "";
        }
    }

    /** Adds a state with no elements if it doesn't exist yet. */
    public void addState(String state) {
        states.computeIfAbsent(state, k -> new LinkedList<>());
    }

    /** Bulk adds objects to given state. Ignores null objects.
     * If the states does not exist, it will be created. */
    public void addObjects(String state, Object... things) {
        for (var thing: things) addObject(state, thing);
    }

    /** Adds an objects to the given state. Ignores null objects.
     * If the states does not exist, it will be created (even if object given is null). */
    public void addObject(String state, Object thing) {
        var targetState = states.computeIfAbsent(state, k -> new LinkedList<>());
        if (thing == null) return;
        targetState.add(thing);

        // IEnable elements need to be disabled at start
        if (thing instanceof IEnable) ((IEnable) thing).setEnabled(false);
    }

    public String getCurrentState() {
        return currentState;
    }

    /** Schedules a menu state switch.
     * The actual switch will happen after onUpdate() is called! */
    public void scheduleStateSwitch(String newState) {
        scheduledState = newState;

    }

    private void switchState(String newState) {
        exitState(currentState);
        enterState(newState);
        currentState = newState;
    }

    private void exitState(String state) {
        var elements = states.get(state);
        if (elements == null) return;

        for (var element: elements) {
            if (element instanceof IEnable)
                ((IEnable) element).setEnabled(false);
        }
    }

    private void enterState(String state) {
        var elements = states.get(state);
        if (elements == null) return;

        for (var element: elements) {
            if (element instanceof IEnable)
                ((IEnable) element).setEnabled(true);
        }
    }

    @Override
    public void destroy() {
        for (var elements: states.values()) {
            for (var element: elements) {
                if (element instanceof IEnable) ((IEnable) element).setEnabled(false);

                if (element instanceof Drawable) ((Drawable) element).unregisterFromEngine();

                if (element instanceof IDestroyable) ((IDestroyable) element).destroy();
            }
        }
    }
}
