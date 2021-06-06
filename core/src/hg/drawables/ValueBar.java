package hg.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.utils.Angle;
import hg.utils.GFXTools;
import hg.utils.GraphicsContext;

import java.text.DecimalFormat;

/** ValueBars are drawables that have a 1xN base texture that is stretched to a certain length.
 * They can also have an optional container texture, and text / font pair to display. */
public class ValueBar extends Drawable {
    protected float currentUnits = 1;
    protected float maxUnits = 1;
    protected float maxLength = 1f;

    protected TextureRegion vSlice = new TextureRegion();
    protected TextureRegion container = new TextureRegion();
    protected BitmapFont valueFont = null;

    private static final DecimalFormat noDigits = new DecimalFormat();

    protected final Vector2 containerOffset = new Vector2();
    protected final Vector2 textOffset = new Vector2();

    public ValueBar() {
        noDigits.setMaximumFractionDigits(0);
    }

    public ValueBar(Texture vSlice, float maxUnits, float maxLength) {
        setTexture(vSlice);
        this.maxUnits = maxUnits;
        this.currentUnits = maxUnits;
        this.maxLength = maxLength;

        noDigits.setMaximumFractionDigits(0);
    }

    public void setProperties(float maxUnits, float maxLength) {
        this.maxUnits = maxUnits;
        this.maxLength = maxLength;
    }

    public void setContainerTex(Texture tex) {
        container.setTexture(tex);
        if (tex != null)
            container.setRegion(tex);
    }

    public void setContainerTex(TextureRegion region) {
        container.setTexture(region.getTexture());
        if (region.getTexture() != null)
            container.setRegion(region);
    }

    public void setContainerOffset(float x, float y) {
        containerOffset.set(x, y);
    }

    public void setTextOffset(float x, float y) {
        textOffset.set(x, y);
    }

    public void setValueFont(BitmapFont font) {
        valueFont = font;
    }

    public void updateFill(float currentUnits) {
        this.currentUnits = currentUnits;
    }

    public void setTexture(Texture texture) {
        vSlice.setTexture(texture);
        if (texture != null)
            vSlice.setRegion(texture);
    }

    public void setTexture(Texture texture, int x, int y, int width, int height) {
        vSlice.setTexture(texture);
        if (texture != null)
            vSlice.setRegion(x, y, width, height);
    }

    @Override
    public void draw(GraphicsContext env) {
        Affine2 transform = GFXTools.AffineFromPCAO(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle));

        Affine2 sliceTransform = new Affine2(transform).scale(maxLength * currentUnits / maxUnits, 1f);
        Affine2 textTransform = new Affine2(transform).preTranslate(textOffset);
        Affine2 containerTransform = new Affine2(transform).preTranslate(containerOffset);

        if (container.getTexture() != null) {
            GFXTools.RenderTextureRegion(env, containerTransform, color, relativeToCamera, container, false, false);
        }

        if (vSlice.getTexture() != null) {
            textTransform.preTranslate(10, vSlice.getRegionHeight() / 2f);
            GFXTools.RenderTextureRegion(env, sliceTransform, color, relativeToCamera, vSlice, false, false);
        }

        if (valueFont != null) {
            GFXTools.RenderText(env, textTransform, color, relativeToCamera, valueFont, noDigits.format(currentUnits), HPos.Left, VPos.Center, 0f);
        }
    }
}
