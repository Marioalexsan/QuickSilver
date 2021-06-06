package hg.entities;

import com.badlogic.gdx.graphics.Texture;
import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.interfaces.callbacks.IGenericPickupCallback;
import hg.physics.Collider;

/** HeavyArmorPickup gives heavy armor to the player.
 * "Rip and tear, until it is done"
 *     - professional demon exterminator
 */
public class GenericPickup extends Pickup {
    private final BasicSprite item;
    private final IGenericPickupCallback onPickupCallback;

    public GenericPickup(Texture itemTex, IGenericPickupCallback onPickupCallback) {
        super(50);
        item = new BasicSprite(itemTex);
        item.setLayer(DrawLayer.FloorAir);
        item.centerToRegion();
        item.registerToEngine();

        item.setPosition(position);
        item.setAngle(angle);

        item.setAlpha(0f);

        pickupZone.setPosition(position);
        pickupZone.setAngle(angle);

        this.onPickupCallback = onPickupCallback;
    }

    @Override
    public Drawable getDrawable() {
        return item;
    }

    @Override
    public Drawable[] getDrawableArray() {
        return new Drawable[] { item };
    }

    @Override
    public Collider getCollider() {
        return pickupZone;
    }

    @Override
    public Collider[] getColliderArray() {
        return new Collider[] { pickupZone };
    }

    @Override
    public void destroy() {
        super.destroy();
        item.unregisterFromEngine();
    }

    @Override
    public void update() {
        float fadeIn = item.getColor().a;
        if (fadeIn < 1f) item.setAlpha(Math.min(fadeIn + 0.05f, 1f));
    }

    @Override
    public void onPickup(PlayerEntity target) {
        if (onPickupCallback != null) onPickupCallback.onPickup(target);
    }
}