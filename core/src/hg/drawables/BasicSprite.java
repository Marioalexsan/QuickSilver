package hg.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import hg.utils.Angle;
import hg.utils.GFXTools;
import hg.utils.GraphicsContext;

/** BasicSprite displays a portion of a texture to the screen. */
public class BasicSprite extends Drawable {
    protected TextureRegion frame = new TextureRegion();

    protected boolean mirrored = false;
    protected boolean flipped = false;

    protected float scaleX = 1f;
    protected float scaleY = 1f;

    public BasicSprite() {}

    public BasicSprite(Texture texture) {
        setTexture(texture);
    }

    public BasicSprite(Texture texture, int x, int y, int width, int height) {
        setTexture(texture, x, y, width, height);
    }

    public void setTexture(Texture texture) {
        frame.setTexture(texture);
        if (texture != null)
            frame.setRegion(texture);
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Vector2 getScale() {
        return new Vector2(scaleX, scaleY);
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setTexture(Texture texture, int x, int y, int width, int height) {
        frame.setTexture(texture);
        if (texture != null)
            frame.setRegion(x, y, width, height);
    }

    public void centerToRegion() {
        if (frame.getTexture() != null) cenOffset.set(frame.getRegionWidth() / 2f, frame.getRegionHeight() / 2f);
    }

    public Rectangle getRegion() {
        return new Rectangle(frame.getRegionX(), frame.getRegionY(), frame.getRegionWidth(), frame.getRegionHeight());
    }

    public Texture getTexture() {
        return frame.getTexture();
    }

    @Override
    public void draw(GraphicsContext env) {
        if (frame.getTexture() == null) return;

        Affine2 transform = GFXTools.AffineFromPCAOScaled(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle), new Vector2(scaleX, scaleY));
        GFXTools.RenderTextureRegion(env, transform, color, relativeToCamera, frame, mirrored, flipped);
    }

    // Getters and Setters

    public void setFlipped(boolean flipped) { this.flipped = flipped; }
    public void setMirrored(boolean mirrored) { this.mirrored = mirrored; }
}