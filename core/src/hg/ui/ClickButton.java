package hg.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.game.HgGame;
import hg.interfaces.callbacks.ICallback;
import hg.physics.BoxCollider;
import hg.physics.CollisionAlgorithms;
import hg.enums.HPos;
import hg.enums.VPos;

/** ClickButton is a button with a square trigger area that can execute a callback upon being clicked */
public class ClickButton extends UIElement {
    protected final BasicSprite drawable;
    protected final BoxCollider collider;
    protected final BasicText contents;

    protected ICallback callback;
    protected boolean clickEnabled = true;

    public ClickButton(Texture tex, int width, int height, BitmapFont font, String text) {
        drawable = new BasicSprite(tex);
        collider = new BoxCollider(width, height);
        contents = new BasicText(font, text);

        drawable.setLayer(DrawLayer.GUIDefault);
        drawable.setCameraUse(false);
        drawable.centerToRegion();

        contents.setLayer(DrawLayer.GUIDefault + 1);
        contents.setCameraUse(false);
        contents.setConstraints(HPos.Center, VPos.Center, tex.getWidth());

        drawable.setPCA(position, center, angle);
        collider.setPCA(position, center, angle);
        contents.setPCA(position, center, angle);

        drawable.registerToEngine();
        contents.registerToEngine();
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        collider.setEnabled(enabled);
        drawable.setEnabled(enabled);
        contents.setEnabled(enabled);
    }

    public void setClickEnabled(boolean enabled) { this.clickEnabled = enabled; }

    @Override
    public void destroy() {
        drawable.unregisterFromEngine();
        contents.unregisterFromEngine();
    }

    @Override
    public void onLMBDown(float x, float y) {
        if (enabled && clickEnabled && callback != null && CollisionAlgorithms.PointHit(new Vector2(x, y), collider)) {
            callback.doCallback();
            HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
        }
    }
}
