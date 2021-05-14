package hg.utils.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.interfaces.callbacks.ICallback;
import hg.ui.ClickButton;

public class ClickButtonMaker {
    private Texture _texture;
    private BitmapFont _font;
    private String _text = "";

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    private int _width = 100;
    private int _height = 100;

    private ICallback _callback;

    public ClickButtonMaker() {}

    public ClickButtonMaker(ClickButtonMaker toCopy) {
        _position.set(toCopy._position);
        _angle = toCopy._angle;
        _callback = toCopy._callback;
        _texture = toCopy._texture;
        _font = toCopy._font;
        _text = toCopy._text;
        _width = toCopy._width;
        _height = toCopy._height;
    }

    public ClickButtonMaker clickArea(int width, int height) {
        this._width = width;
        this._height = height;
        return this;
    }

    public ClickButtonMaker display(Texture tex) {
        this._texture = tex;
        return this;
    }

    public ClickButtonMaker text(String text) {
        this._text = text;
        return this;
    }

    public ClickButtonMaker font(BitmapFont font) {
        this._font = font;
        return this;
    }

    public ClickButtonMaker position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public ClickButtonMaker angle(float angle) {
        this._angle = angle;
        return this;
    }

    public ClickButtonMaker onClick(ICallback callback) {
        this._callback = callback;
        return this;
    }

    public ClickButton build() {
        ClickButton button = new ClickButton(_texture, _width, _height, _font, _text);
        button.getPosition().set(_position);
        button.getAngle().set(_angle);
        button.setCallback(_callback);
        return button;
    }
}
