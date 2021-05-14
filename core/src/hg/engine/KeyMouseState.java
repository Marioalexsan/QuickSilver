package hg.engine;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.utils.HgMathUtils;

import java.util.HashMap;

public class KeyMouseState extends InputAdapter {
    @Override
    public boolean keyDown (int keycode) {
        if (!HgGame.Game().isFocused()) return false;
        keyActivity.put(keycode, 0);
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (!HgGame.Game().isFocused()) return false;
        keyActivity.put(keycode, -1);
        return false;
    }

    @Override
    public boolean keyTyped(char ch) {
        if (!HgGame.Game().isFocused()) return false;
        if (ch < 32) return false; // Ignore control characters
        textActivity.append(ch);
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (!HgGame.Game().isFocused()) return false;
        // pointer is not used
        mouseActivity[button] = 0;
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (!HgGame.Game().isFocused()) return false;
        // pointer is not used
        mouseActivity[button] = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // Same as mouseMoved
        if (!HgGame.Game().isFocused()) return false;
        processMouse(x, y);
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        if (!HgGame.Game().isFocused()) return false;
        processMouse(x, y);
        return false;
    }

    private void processMouse(int x, int y) {
        Vector2 resolution = HgGame.Graphics().getCurrentResolution();
        virtualMouseX = (int) HgMathUtils.ClampValue(virtualMouseX + Math.ceil(mouseSensitivity * (x - mouseX) * resolution.x / 1920.0f), 0, 1920);
        virtualMouseY = (int) HgMathUtils.ClampValue(virtualMouseY + Math.ceil(mouseSensitivity * (y - mouseY) * resolution.y / 1080.0f), 0, 1080);
        mouseX = x;
        mouseY = y;
    }

    public KeyMouseState() {
    }

    // Members

    // This map records the time a button was held so far, in frames.
    // A key that was just tapped means that time = 1, exactly
    // A hold for a certain duration means that time >= duration
    protected final HashMap<Integer, Integer> keyActivity = new HashMap<>();

    protected boolean textEnabled = false;
    protected final StringBuffer textActivity = new StringBuffer();

    public int mouseX = 0;
    public int mouseY = 0;

    protected int virtualMouseX = 0;
    protected int virtualMouseY = 0;
    protected int[] mouseActivity = {-1, -1, -1, -1, -1}; // Left, Right, Middle, Back, Forward

    protected float mouseSensitivity = 1.0f;
}