package hg.utils.builders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.interfaces.ICopy;
import hg.ui.BasicTextInput;

public class TextInputBuilder implements ICopy {
    private BitmapFont _font;
    private String _emptyText = "";

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    private HPos _hpos;
    private VPos _vpos;
    private float _wrap = 0f;

    private int _width = 100;
    private int _height = 100;
    private int _maxChars = 12345678;

    public TextInputBuilder() {}

    public TextInputBuilder(TextInputBuilder toCopy) {
        _font = toCopy._font;
        _emptyText = toCopy._emptyText;
        _position.set(toCopy._position);
        _angle = toCopy._angle;
        _hpos = toCopy._hpos;
        _vpos = toCopy._vpos;
        _wrap = toCopy._wrap;
        _width = toCopy._width;
        _height = toCopy._height;
        _maxChars = toCopy._maxChars;
    }

    @Override
    public TextInputBuilder copy() {
        return new TextInputBuilder(this);
    }

    public TextInputBuilder clickArea(int width, int height) {
        this._width = width;
        this._height = height;
        return this;
    }

    public TextInputBuilder font(BitmapFont font) {
        this._font = font;
        return this;
    }

    public TextInputBuilder emptyText(String emptyText) {
        this._emptyText = emptyText;
        return this;
    }

    public TextInputBuilder maxChars(int count) {
        this._maxChars = count;
        return this;
    }

    public TextInputBuilder position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public TextInputBuilder textPos(HPos hpos, VPos vpos) {
        this._hpos = hpos;
        this._vpos = vpos;
        return this;
    }

    public TextInputBuilder textPos(HPos hpos, VPos vpos, float wrap) {
        this._hpos = hpos;
        this._vpos = vpos;
        this._wrap = wrap;
        return this;
    }

    public BasicTextInput build() {
        BasicTextInput text = new BasicTextInput(_font, _maxChars, _width, _height);
        text.getPosition().set(_position);
        text.getAngle().set(_angle);
        text.setEmptyText(_emptyText);
        return text;
    }
}
