package hg.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import hg.engine.GraphicsEngine;
import hg.utils.Angle;
import hg.utils.HgGraphicsUtils;

public class BasicSprite extends Drawable {
    protected TextureRegion frame = new TextureRegion();

    protected boolean mirrored = false;
    protected boolean flipped = false;

    public BasicSprite() {}

    public BasicSprite(Texture texture) {
        setTexture(texture);
    }

    public BasicSprite(Texture texture, int x, int y, int width, int height) {
        setTexture(texture, x, y, width, height);
    }

    public void setTexture(Texture texture) {
        frame.setTexture(texture);
        frame.setRegion(texture);
    }

    public void setTexture(Texture texture, int x, int y, int width, int height) {
        frame.setTexture(texture);
        frame.setRegion(x, y, width, height);
    }

    public void centerToRegion() {
        if (frame.getTexture() != null) cenOffset.set(frame.getRegionWidth() / 2f, frame.getRegionHeight() / 2f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (frame.getTexture() == null) return;

        Affine2 transform = HgGraphicsUtils.GetAffineForPCAO(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle));
        GraphicsEngine.RenderTextureRegion(batch, transform, color, relativeToCamera, frame, mirrored, flipped);
    }

    // Getters and Setters

    public void setFlipped(boolean flipped) { this.flipped = flipped; }
    public void setMirrored(boolean mirrored) { this.mirrored = mirrored; }
}