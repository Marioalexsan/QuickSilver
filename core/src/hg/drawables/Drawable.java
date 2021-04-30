package hg.drawables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hg.game.HgGame;
import hg.utils.Angle;

/**
 * Abstract class that can draw to the screen.
 */
public abstract class Drawable {
    protected Vector2 position = new Vector2();
    protected Vector2 center = new Vector2();
    protected Angle angle = new Angle();

    protected Vector2 posOffset = new Vector2();
    protected Vector2 cenOffset = new Vector2();
    protected Angle angOffset = new Angle();

    protected Angle textureAngle = new Angle(); // Texture orientation offset (yes, this is the third one)

    protected boolean relativeToCamera = true;
    protected int layer = DrawLayer.Default;
    protected final Color color = new Color(1, 1, 1, 1);

    protected boolean enabled = true;

    public Drawable() {}

    public abstract void draw(SpriteBatch batch);

    public void registerToEngine() {
        HgGame.Graphics().registerDrawable(this);
    }

    public void unregisterFromEngine() {
        HgGame.Graphics().unregisterDrawable(this);
    }

    // Getters and setters

    public Angle getTextureAngle() { return textureAngle; }
    public Vector2 getPosition() { return position; }
    public Vector2 getPositionOffset() { return posOffset; }
    public Angle getAngle() { return angle; }
    public Angle getAngleOffset() { return angOffset; }
    public Vector2 getCenter() { return center; }
    public Vector2 getCenterOffset() { return cenOffset; }
    public int getLayer() { return this.layer; }
    public boolean usesCamera() { return relativeToCamera; }

    public void setTextureAngle(Angle angle) { this.textureAngle = angle; }
    public void setPosition(Vector2 position) { this.position = position; }
    public void setPositionOffset(Vector2 offset) { this.posOffset = offset; }
    public void setAngle(Angle angle) { this.angle = angle; }
    public void setAngleOffset(Angle offset) { this.angOffset = offset; }
    public void setCenter(Vector2 center) { this.center = center; }
    public void setCenterOffset(Vector2 offset) { this.cenOffset = offset; }
    public void setCameraUse(boolean relativeToCamera) { this.relativeToCamera = relativeToCamera; }
    public void setLayer(int layer) { this.layer = layer; }

    public void setRGB(float r, float g, float b) { this.color.set(r, g, b, color.a); }
    public void setAlpha(float a) { this.color.a = a; }
    public void setColor(Color color) { this.color.set(color); }
    public Color getColor() { return this.color; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isActive() { return enabled; }
}
