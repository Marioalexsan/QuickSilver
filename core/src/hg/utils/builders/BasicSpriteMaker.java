package hg.utils.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.enums.HPos;
import hg.enums.VPos;

public class BasicSpriteMaker {
    private Texture _tex;
    private int _x;
    private int _y;
    private int _width;
    private int _height;

    private int _drawLayer;

    private boolean _mirrored = false;
    private boolean _flipped = false;

    private boolean _cameraUse = true;
    private boolean _centerToRegion = false;

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    public BasicSpriteMaker() {}

    public BasicSpriteMaker(BasicSpriteMaker toCopy) {
        _tex = toCopy._tex;;
        _mirrored = toCopy._mirrored;
        _flipped = toCopy._flipped;
        _position.set(toCopy._position);
        _angle = toCopy._angle;
        _x = toCopy._x;
        _y = toCopy._y;
        _width = toCopy._width;
        _height = toCopy._height;
        _cameraUse = toCopy._cameraUse;
        _drawLayer = toCopy._drawLayer;
    }

    public BasicSpriteMaker texture(Texture tex) {
        this._tex = tex;
        return this;
    }

    public BasicSpriteMaker crop(int x, int y, int width, int height) {
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
        return this;
    }

    public BasicSpriteMaker position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public BasicSpriteMaker angle(float angle) {
        this._angle = angle;
        return this;
    }

    public BasicSpriteMaker cameraUse(boolean use) {
        this._cameraUse = use;
        return this;
    }

    public BasicSpriteMaker layer(int drawLayer) {
        this._drawLayer = drawLayer;
        return this;
    }

    public BasicSpriteMaker makeGUI() {
        this._drawLayer = DrawLayer.GUIDefault;
        this._cameraUse = false;
        return this;
    }

    public BasicSpriteMaker centerToRegion(boolean center) {
        this._centerToRegion = center;
        return this;
    }

    public BasicSprite build() {
        BasicSprite sprite;
        if (_width == 0 && _height == 0) {
            sprite = new BasicSprite(_tex);
        }
        else {
            sprite = new BasicSprite(_tex, _x, _y, _width, _height);
        }
        if (_centerToRegion) sprite.centerToRegion();
        sprite.setCameraUse(_cameraUse);
        sprite.getPosition().set(_position);
        sprite.getAngle().set(_angle);
        sprite.setLayer(_drawLayer);
        sprite.setMirrored(_mirrored);
        sprite.setFlipped(_flipped);
        sprite.registerToEngine();
        return sprite;
    }
}
