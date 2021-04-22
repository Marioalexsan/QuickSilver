package hg.drawables;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;
import hg.engine.GraphicsEngine;
import hg.utils.HgEngineUtils;

/** TO DO: Take a look at GlyphLayout / GlyphRun
 * Drawable that renders text using a BitmapFont.
 * BitmapFonts are not thread safe for rendering, *regardless* of what you do!
 */
public class BasicText extends Drawable {
    public enum RenderMode {
        ContinuousCentered, // Line is centered on position
        ContinuousLeft, // Line is placed to the left of position
        ContinuousRight, // Line is placed to the right of position
        BlockCentered, // Block is centered on position
        BlockLeft, // Block is placed to the left of position
        BlockRight // Block is placed to the right of position
    }

    private BitmapFont font;
    private String text;

    private Rectangle blockConstraints = new Rectangle(0, 0, 100, 100);
    private RenderMode mode = RenderMode.ContinuousRight;

    public BasicText() {}

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setBlockConstraints(Rectangle rect) {
        this.blockConstraints.set(rect);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRenderMode(RenderMode mode) {
        this.mode = mode;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (font == null || text == null) {
            return;
        }
        // RenderMode needs to be implemented

        Affine2 transform = HgEngineUtils.GetAffineForPCAO(position, center, angle, posOffset, cenOffset, angOffset);
        GraphicsEngine.RenderText(batch, transform, color, relativeToCamera, font, text);
    }
}
