package hg.drawables.gfxeffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.utils.Angle;
import hg.utils.GFXTools;
import hg.utils.GraphicsContext;

public class AfterImage extends GFXEffect {
    private final TextureRegion frame;
    private boolean mirrored;
    private boolean flipped;
    private float scaleX = 1f;
    private float scaleY = 1f;

    private final int duration;

    /** Creates a new AfterImage with the given texture */
    public AfterImage(Texture tex, int duration) {
        super(duration);
        frame = new TextureRegion(tex);
        this.duration = duration;
    }

    /** Creates a new AfterImage by copying information from origin, including PCAO. Sets layer as one below origin. */
    public AfterImage(BasicSprite origin, int duration) {
        super(duration);

        setPCA(new Vector2(origin.getPosition()), new Vector2(origin.getCenter()), new Angle(origin.getAngle()));
        posOffset.set(origin.getPositionOffset());
        cenOffset.set(origin.getCenterOffset());
        angOffset.set(origin.getAngleOffset());
        textureAngle.set(origin.getTextureAngle());
        layer = origin.getLayer() - 1;

        Rectangle region = origin.getRegion();
        frame = new TextureRegion(origin.getTexture(), (int) region.x, (int) region.y, (int) region.width, (int) region.height);

        scaleX = origin.getScale().x;
        scaleY = origin.getScale().y;
        mirrored = origin.isMirrored();
        flipped = origin.isFlipped();

        this.duration = duration;
    }

    @Override
    public void draw(GraphicsContext env) {
        if (frame.getTexture() == null) return;

        float alphaToUse = color.a * 0.25f;

        int twoThirds = duration * 2 / 3;

        if (timeLeft < twoThirds) {
            float factor = (float) timeLeft / twoThirds;
            alphaToUse *= factor;
        }

        Color colorToUse = new Color(color.r, color.g, color.b, alphaToUse);
        Affine2 transform = GFXTools.AffineFromPCAOScaled(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle), new Vector2(scaleX, scaleY));
        GFXTools.RenderTextureRegion(env, transform, colorToUse, relativeToCamera, frame, mirrored, flipped);
    }
}
