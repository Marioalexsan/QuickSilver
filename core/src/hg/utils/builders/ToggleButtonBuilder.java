package hg.utils.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hg.interfaces.ICopy;
import hg.interfaces.callbacks.ICallback;
import hg.ui.ToggleButton;

/** Builds ToggleButtons using the given options */
public class ToggleButtonBuilder implements ICopy {
    private Texture _inactiveTex;
    private Texture _activeTex;

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    private int _width = 100;
    private int _height = 100;

    private ICallback _activate;
    private ICallback _inactivate;

    private boolean _startActive = false;

    public ToggleButtonBuilder() {}

    public ToggleButtonBuilder(ToggleButtonBuilder toCopy) {
        _position.set(toCopy._position);
        _angle = toCopy._angle;
        _activate = toCopy._activate;
        _inactivate = toCopy._inactivate;
        _inactiveTex = toCopy._inactiveTex;
        _activeTex = toCopy._activeTex;
        _width = toCopy._width;
        _height = toCopy._height;
        _startActive = toCopy._startActive;
    }

    @Override
    public ToggleButtonBuilder copy() {
        return new ToggleButtonBuilder(this);
    }

    public ToggleButtonBuilder startActive(boolean active) {
        this._startActive = active;
        return this;
    }

    public ToggleButtonBuilder clickArea(int width, int height) {
        this._width = width;
        this._height = height;
        return this;
    }

    public ToggleButtonBuilder display(Texture inactive, Texture active) {
        this._inactiveTex = inactive;
        this._activeTex = active;
        return this;
    }

    public ToggleButtonBuilder position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public ToggleButtonBuilder angle(float angle) {
        this._angle = angle;
        return this;
    }

    public ToggleButtonBuilder whenOn(ICallback onActivation) {
        this._activate = onActivation;
        return this;
    }

    public ToggleButtonBuilder whenOff(ICallback onInactivation) {
        this._inactivate = onInactivation;
        return this;
    }

    public ToggleButton build() {
        ToggleButton button = new ToggleButton(_inactiveTex, _activeTex, _width, _height, _startActive);
        button.getPosition().set(_position);
        button.getAngle().set(_angle);
        button.setActivateCallback(_activate);
        button.setInactivateCallback(_inactivate);
        return button;
    }
}