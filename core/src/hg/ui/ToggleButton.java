package hg.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.game.HgGame;
import hg.interfaces.callbacks.ICallback;
import hg.networking.Packet;
import hg.physics.BoxCollider;
import hg.physics.CollisionAlgorithms;

public class ToggleButton extends UIElement {
    protected final BasicSprite drawable;
    protected final BoxCollider collider;

    protected Texture inactiveTex;
    protected Texture activeTex;

    protected boolean activated = false;
    protected boolean clickEnabled = true;

    protected ICallback activateCallback;
    protected ICallback inactivateCallback;

    public ToggleButton(Texture inactive, Texture active, int width, int height, boolean startsActive) {
        activated = startsActive;

        drawable = new BasicSprite(activated ? active : inactive);
        collider = new BoxCollider(width, height);

        drawable.setLayer(DrawLayer.GUIDefault);
        drawable.setCameraUse(false);
        drawable.centerToRegion();

        drawable.setPCA(position, center, angle);
        collider.setPCA(position, center, angle);

        drawable.registerToEngine();

        this.inactiveTex = inactive;
        this.activeTex = active;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        collider.setEnabled(enabled);
        drawable.setEnabled(enabled);
    }

    public void setClickEnabled(boolean enabled) { this.clickEnabled = enabled; }

    @Override
    public void destroy() {
        drawable.unregisterFromEngine();
    }

    public void toggle() {
        activated = !activated;
        drawable.setTexture(activated ? activeTex : inactiveTex);
        drawable.centerToRegion();

        if (activated) {
            if (activateCallback != null) activateCallback.doCallback();
        }
        else {
            if (inactivateCallback != null) inactivateCallback.doCallback();
        }
    }

    public void setActivateCallback(ICallback callback) {
        this.activateCallback = callback;
    }

    public void setInactivateCallback(ICallback callback) {
        this.inactivateCallback = callback;
    }

    public boolean isActive() {
        return activated;
    }

    @Override
    public void onLMBDown(float x, float y) {
        if (enabled && clickEnabled && CollisionAlgorithms.PointHit(new Vector2(x, y), collider)) {
            toggle();
            HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
        }
    }
}