package hg.utils.builders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.interfaces.ICopy;

public class BasicTextBuilder implements ICopy {
    private BitmapFont _font;
    private String _text = "";

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    private int _drawLayer;
    private boolean _cameraUse = true;

    private HPos _hpos;
    private VPos _vpos;
    private float _wrap = 0f;

    public BasicTextBuilder() {}

    public BasicTextBuilder(BasicTextBuilder toCopy) {
        _font = toCopy._font;
        _text = toCopy._text;
        _position.set(toCopy._position);
        _angle = toCopy._angle;
        _hpos = toCopy._hpos;
        _vpos = toCopy._vpos;
        _wrap = toCopy._wrap;
        _drawLayer = toCopy._drawLayer;
        _cameraUse = toCopy._cameraUse;
    }

    @Override
    public BasicTextBuilder copy() {
        return new BasicTextBuilder(this);
    }

    public BasicTextBuilder text(String text) {
        this._text = text;
        return this;
    }

    public BasicTextBuilder font(BitmapFont font) {
        this._font = font;
        return this;
    }

    public BasicTextBuilder position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public BasicTextBuilder textPos(HPos hpos, VPos vpos) {
        this._hpos = hpos;
        this._vpos = vpos;
        return this;
    }

    public BasicTextBuilder textPos(HPos hpos, VPos vpos, float wrap) {
        this._hpos = hpos;
        this._vpos = vpos;
        this._wrap = wrap;
        return this;
    }

    public BasicTextBuilder cameraUse(boolean use) {
        this._cameraUse = use;
        return this;
    }

    public BasicTextBuilder layer(int drawLayer) {
        this._drawLayer = drawLayer;
        return this;
    }

    public BasicTextBuilder makeGUI() {
        this._drawLayer = DrawLayer.GUIDefault;
        this._cameraUse = false;
        return this;
    }

    public BasicText build() {
        BasicText text = new BasicText(_font, _text);
        text.setCameraUse(false);
        text.getPosition().set(_position);
        text.getAngle().set(_angle);
        text.setConstraints(_hpos, _vpos, _wrap);
        text.setLayer(_drawLayer);
        text.registerToEngine();
        return text;
    }
}
