package hg.ui;

import com.badlogic.gdx.Input;
import hg.drawables.Drawable;
import hg.game.HgGame;
import hg.interfaces.IDestroyable;
import hg.interfaces.IEnable;

import java.util.HashMap;

public class BasicUIStates extends UIElement {
    private final HashMap<String, HashMap<String, Object>> states = new HashMap<>();

    private String currentState = "";
    private String scheduledState = "";

    @Override
    public void onUpdate() {
        var pos = HgGame.Input().getMouse();
        var clicked = HgGame.Input().isButtonTapped(Input.Buttons.LEFT);

        for (var elements: states.entrySet()) {
            for (var KVP : elements.getValue().entrySet()) {
                var element = KVP.getValue();

                if (element instanceof UIElement) {
                    UIElement ui = (UIElement) element;
                    if (clicked) ui.onLMBDown(pos.x, pos.y);

                    ui.onUpdate();
                }
            }
        }

        // State switches are best done after an update ends, to prevent stupid interactions
        if (!scheduledState.equals("")) {
            switchState(scheduledState);
            scheduledState = "";
        }
    }

    public boolean addState(String state) {
        if (states.containsKey(state)) return false;

        states.put(state, new HashMap<>());

        return true;
    }

    public boolean addStateElement(String state, String element, Object thing) {
        var targetState = states.get(state);
        if (targetState == null || targetState.containsKey(element)) return false;

        targetState.put(element, thing);

        // IEnable elements need to be disabled at start
        if (thing instanceof IEnable) ((IEnable) thing).setEnabled(false);

        return true;
    }

    public Object getStateElement(String state, String element) {
        var targetState = states.get(state);
        if (targetState == null) return null;

        return targetState.get(element);
    }

    public String getCurrentState() {
        return currentState;
    }

    /** Schedules a menu state switch.
     * The actual switch will happen after onUpdate() is called!
     */
    public boolean scheduleSwitchState(String newState) {
        if (!states.containsKey(newState)) return false;

        scheduledState = newState;

        return true;
    }

    private void switchState(String newState) {
        exitState(currentState);
        enterState(newState);
        currentState = newState;
    }

    private void exitState(String state) {
        var elements = states.get(state);
        if (elements == null) return;

        for (var KVP: elements.entrySet()) {
            var element = KVP.getValue();
            if (element instanceof IEnable) {
                ((IEnable) element).setEnabled(false);
            }
        }
    }

    private void enterState(String state) {
        var elements = states.get(state);
        if (elements == null) return;

        for (var KVP: elements.entrySet()) {
            var element = KVP.getValue();
            if (element instanceof IEnable) {
                ((IEnable) element).setEnabled(true);
            }
        }
    }

    @Override
    public void destroy() {
        for (var elements: states.entrySet()) {
            for (var KVP: elements.getValue().entrySet()) {
                var element = KVP.getValue();

                if (element instanceof IEnable) ((IEnable) element).setEnabled(false);

                if (element instanceof Drawable) ((Drawable) element).unregisterFromEngine();

                if (element instanceof IDestroyable) ((IDestroyable) element).destroy();
            }
        }
    }
}
