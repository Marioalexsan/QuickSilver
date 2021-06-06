package hg.drawables.gfxeffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.Drawable;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.utils.Angle;
import hg.utils.GFXTools;
import hg.utils.GraphicsContext;

public class BulletPath extends GFXEffect {
    protected TextureRegion vSlice;
    protected float length = 1f;

    private final int duration;

    public BulletPath(TextureRegion vSlice, Vector2 start, float angle, float length, int duration) {
        super(duration);

        this.vSlice = new TextureRegion(vSlice);
        this.length = length;
        this.duration = duration;

        position.set(start);
        if (this.vSlice.getTexture() != null)
            center.set(0f, this.vSlice.getRegionHeight() / 2f);
        this.angle.set(angle);
    }

    @Override
    public void draw(GraphicsContext env) {
        if (vSlice.getTexture() == null) return;

        Affine2 transform = GFXTools.AffineFromPCAO(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle));
        transform.scale(length, 1f);

        Color colorToUse = new Color(color).mul(1f, 1f, 1f, (float) timeLeft / duration);
        GFXTools.RenderTextureRegion(env, transform, colorToUse, relativeToCamera, vSlice, false, false);
    }
}
