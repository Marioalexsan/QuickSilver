package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.utils.HgMath;

import java.util.HashMap;

/**
 * Records input.
 * Used internally by InputEngine.
 */

public class InputEngine {

    public static final int ButtonMappingOffset = 1024;

    private final KeyMouseState keyMouseState = new KeyMouseState();
    private final HashMap<MappedAction, Integer> keyActionMap = new HashMap<>();

    public InputEngine() {
        Gdx.input.setCursorCatched(true);
        Gdx.input.setInputProcessor(keyMouseState);

        // Read these from somewhere
        keyActionMap.put(MappedAction.MoveUp, Input.Keys.W);
        keyActionMap.put(MappedAction.MoveLeft, Input.Keys.A);
        keyActionMap.put(MappedAction.MoveDown, Input.Keys.S);
        keyActionMap.put(MappedAction.MoveRight, Input.Keys.D);

        keyActionMap.put(MappedAction.Reload, Input.Keys.R);
        keyActionMap.put(MappedAction.Escape, Input.Keys.ESCAPE);

        keyActionMap.put(MappedAction.QuickSwitchWeapon, Input.Keys.Q);

        keyActionMap.put(MappedAction.PrimaryFire, Input.Buttons.LEFT + ButtonMappingOffset);
        keyActionMap.put(MappedAction.SecondaryFire, Input.Buttons.RIGHT + ButtonMappingOffset);
    }

    public void update() {
        for (var key : keyMouseState.keyActivity.entrySet()) {
            if (key.getValue() >= 0) {
                key.setValue(key.getValue() + 1);
            }
        }
        for (int i = 0; i <= 4; i++) {
            if (keyMouseState.mouseActivity[i] >= 0) {
                keyMouseState.mouseActivity[i]++;
            }
        }
    }

    // Raw Key / Button checking

    public boolean isKeyTapped(int key) {
        Integer value = keyMouseState.keyActivity.get(key);
        return value != null && value == 1;
    }

    public boolean isKeyHeldForX(int key, int frames) {
        Integer value = keyMouseState.keyActivity.get(key);
        return value != null && (value > frames);
    }

    public boolean isKeyHeld(int key) {
        return isKeyHeldForX(key, 1);
    }

    public int getKeyFrames(int key) {
        Integer value = keyMouseState.keyActivity.get(key);
        return value != null ? value : 0;
    }

    public boolean isButtonTapped(int key) {
        return key >= 0 && key <= 4 && keyMouseState.mouseActivity[key] == 1;
    }

    public boolean isButtonHeldForX(int key, int frames) {
        return key >= 0 && key <= 4 && keyMouseState.mouseActivity[key] > frames;
    }

    public boolean isButtonHeld(int key) {
        return isButtonHeldForX(key, 1);
    }

    public int getButtonFrames(int key) {
        return (key >= 0 && key <= 4) ? keyMouseState.mouseActivity[key] : 0;
    }

    // Mapped Action checking



    public boolean isActionTapped(MappedAction action) {
        return getActionFrames(action) == 1;
    }

    public boolean isActionHeldForX(MappedAction action, int frames) {
        return getActionFrames(action) > frames;
    }

    public boolean isActionHeld(MappedAction action) {
        return isActionHeldForX(action, 1);
    }

    public int getActionFrames(MappedAction action) {
        Integer mappedKey = keyActionMap.get(action);
        Integer value;
        if (mappedKey >= ButtonMappingOffset) {
            mappedKey -= ButtonMappingOffset;
            value = mappedKey < keyMouseState.mouseActivity.length ? keyMouseState.mouseActivity[mappedKey] : null;
        }
        else value = keyMouseState.keyActivity.get(mappedKey);
        return value != null ? value : 0;
    }

    // Mouse checking

    public Vector2 getRawMouse() {
        return new Vector2(keyMouseState.virtualMouseX, keyMouseState.virtualMouseY);
    }

    public Vector2 getMouse() {
        Vector2 resolution = HgGame.Graphics().getCurrentResolution();
        return new Vector2(
                keyMouseState.virtualMouseX - HgGame.WorldWidth / 2f,
                1080 - keyMouseState.virtualMouseY - HgGame.WorldHeight / 2f
        );
    }

    // FOV Mechanic - factor:
    // Consider the center of the world as a vector, and the camera's position as a vector.
    // The cursor always renders independent of camera.
    // Now, calculate the difference between mouse vector and center vector as the distance vector.
    // FOV mechanic causes the camera to be translated by -1.0 * factor * distance vector.
    // The input engine can also calculate the "world position" of the cursor based on this information.

    /** TO DO: Better documentation of FOV.
     * Returns a skewed mouse position.
     * @param factor - a value in range [0.0, 1.0].
     * @return Two vectors - the mouse world position according to factor, and the camera displacement
     */
    public Vector2[] getWorldMouseAndFOVOffset(float factor) {
        factor = HgMath.ClampValue(factor, 0f, 1f);
        Vector2 camera = HgGame.Graphics().getCameraCenter();
        Vector2 originalMouse = getMouse();
        Vector2 distance = new Vector2(originalMouse).sub(camera);
        return new Vector2[] {
                new Vector2(distance).scl(factor).add(originalMouse),
                new Vector2(distance).scl(factor)
        };
    }

    public Vector2 getWorldMouse() {
        return new Vector2(getMouse()).scl(HgGame.Graphics().getCameraZoom()).add(HgGame.Graphics().getCameraCenter());
    }

    public Vector2 getFOVWorldMouse(float factor) {
        factor = HgMath.ClampValue(factor, 0f, 1f);
        Vector2 distance = getMouse();
        return new Vector2(distance).scl(factor * HgGame.Graphics().getCameraZoom()).add(getWorldMouse());
    }

    public Vector2 getFOVCameraOffset(float factor) {
        factor = HgMath.ClampValue(factor, 0f, 1f);
        return new Vector2(getMouse()).scl(factor * HgGame.Graphics().getCameraZoom());
    }
}