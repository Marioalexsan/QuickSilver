package hg.drawables;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import hg.engine.GraphicsEngine;
import hg.enums.HPos;
import hg.utils.HgGraphicsUtils;
import hg.enums.VPos;

/** TO DO: Take a look at GlyphLayout / GlyphRun
 * Drawable that renders text using a BitmapFont.
 * BitmapFonts are not thread safe for rendering, *regardless* of what you do!
 */
public class BasicText extends Drawable {

    private BitmapFont font;
    private String text;

    private VPos vpos = VPos.Top;
    private HPos hpos = HPos.Left;
    private float wrap;

    public BasicText() {}

    public BasicText(BitmapFont font, String text) {
        this.font = font;
        this.text = text;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setConstraints(HPos hpos, VPos vpos, float wrap) {
        this.hpos = hpos;
        this.vpos = vpos;
        this.wrap = Math.max(0f, wrap);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (font == null || text == null) {
            return;
        }

        Affine2 transform = HgGraphicsUtils.GetAffineForPCAO(position, center, angle, posOffset, cenOffset, angOffset);
        GraphicsEngine.RenderText(batch, transform, color, relativeToCamera, font, text, hpos, vpos, wrap);
    }
}
