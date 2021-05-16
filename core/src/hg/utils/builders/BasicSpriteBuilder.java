package hg.utils.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.interfaces.ICopy;

public class BasicSpriteBuilder implements ICopy {
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
    private boolean _centerToNW = false;
    private boolean _centerToW = false;

    private final Vector2 _position = new Vector2(0, 0);
    private float _angle = 0f;

    public BasicSpriteBuilder() {}

    public BasicSpriteBuilder(BasicSpriteBuilder other) {
        _tex = other._tex;
        _mirrored = other._mirrored;
        _flipped = other._flipped;
        _position.set(other._position);
        _angle = other._angle;
        _x = other._x;
        _y = other._y;
        _width = other._width;
        _height = other._height;
        _cameraUse = other._cameraUse;
        _drawLayer = other._drawLayer;
        _centerToRegion = other._centerToRegion;
        _centerToNW = other._centerToNW;
        _centerToW = other._centerToW;
    }

    @Override
    public BasicSpriteBuilder copy() {
        return new BasicSpriteBuilder(this);
    }

    public BasicSpriteBuilder texture(Texture tex) {
        this._tex = tex;
        return this;
    }

    public BasicSpriteBuilder crop(int x, int y, int width, int height) {
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
        return this;
    }

    public BasicSpriteBuilder position(float x, float y) {
        _position.set(x, y);
        return this;
    }

    public BasicSpriteBuilder angle(float angle) {
        this._angle = angle;
        return this;
    }

    public BasicSpriteBuilder cameraUse(boolean use) {
        this._cameraUse = use;
        return this;
    }

    public BasicSpriteBuilder layer(int drawLayer) {
        this._drawLayer = drawLayer;
        return this;
    }

    public BasicSpriteBuilder makeGUI() {
        this._drawLayer = DrawLayer.GUIDefault;
        this._cameraUse = false;
        return this;
    }

    public BasicSpriteBuilder centerToRegion(boolean center) {
        this._centerToRegion = center;
        return this;
    }

    public BasicSpriteBuilder centerToNW(boolean center) {
        this._centerToNW = center;
        return this;
    }

    public BasicSpriteBuilder centerToW(boolean center) {
        this._centerToW = center;
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
        else if (_centerToNW) sprite.setCenterOffset(new Vector2(0, _tex.getHeight()));
        else if (_centerToW) sprite.setCenterOffset(new Vector2(0, _tex.getHeight() / 2f));
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
