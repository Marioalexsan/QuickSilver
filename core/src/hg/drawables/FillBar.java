package hg.drawables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import hg.engine.GraphicsEngine;
import hg.utils.Angle;
import hg.utils.GFXTools;

public class FillBar extends Drawable {
    protected TextureRegion vSlice = new TextureRegion();
    protected float currentUnits = 1;
    protected float maxUnits = 1;
    protected float maxLength = 1f;

    public FillBar() {}

    public FillBar(Texture vSlice, float maxUnits, float maxLength) {
        setTexture(vSlice);
        this.maxUnits = maxUnits;
        this.currentUnits = maxUnits;
        this.maxLength = maxLength;
    }

    public void setProperties(float maxUnits, float maxLength) {
        this.maxUnits = maxUnits;
        this.maxLength = maxLength;
    }

    public void updateFill(float currentUnits) {
        this.currentUnits = currentUnits;
    }

    public void setTexture(Texture texture) {
        vSlice.setTexture(texture);
        vSlice.setRegion(texture);
    }

    public void setTexture(Texture texture, int x, int y, int width, int height) {
        vSlice.setTexture(texture);
        vSlice.setRegion(x, y, width, height);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (vSlice.getTexture() == null) return;

        Affine2 transform = GFXTools.GetAffineForPCAO(position, center, angle, posOffset, cenOffset, new Angle(angOffset).add(textureAngle));
        transform.scale(maxLength * currentUnits / maxUnits, 1f);
        GraphicsEngine.RenderTextureRegion(batch, transform, color, relativeToCamera, vSlice, false, false);
    }
}
