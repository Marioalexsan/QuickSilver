package hg.weapons;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import hg.animation.Animation;
import hg.drawables.BasicSprite;
import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.entities.Bullet;
import hg.entities.PlayerEntity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.states.DBShotgunState;
import hg.gamelogic.states.RevolverState;
import hg.gamelogic.states.State;
import hg.interfaces.IWeapon;
import hg.networking.NetworkRole;
import hg.physics.ColliderGroup;
import hg.enums.types.ActorType;
import hg.utils.Angle;
import hg.utils.MathTools;

import java.util.Random;

/** A shotgun that shoots multiple pellets upon firing. Can do a Power Shot using secondary fire, where you fire both shells at the same time.
 * Knocks back the user upon shooting. Has high damage which falls off with distance due to spread.
 * Well placed Power Shots are likely to kill a player in one attack. */
public class DBShotgun implements IWeapon {

    // Owner related stuff

    private Entity owner = null;
    private Animation ownerAnimation = null;

    // Weapon stats

    private static final int ShotRandomSeed = 133742069;
    private static final int ShotPelletCount = 12;
    private final int magazineSize = 2;
    private final int maxTotalAmmo = 28;
    private final int shotCooldown = 40;
    private final int timeToReload = 95;
    private final int weaponPickupAmmo = 8;
    private final int ammoPackAmmo = 4;

    private float pushbackDirection = 0;
    private float pushbackPower = 0f;

    // Weapon state

    private int currentAmmo = 2;
    private int reserveAmmo = 10;
    private int weaponCooldown = 0;

    private int reloadCounter = 0;

    private int localOnly_clickCooldown;

    public DBShotgun(Entity owner) {
        setOwner(owner);
    }

    @Override
    public String getAmmoDisplay() {
        return currentAmmo + " / " + magazineSize + " [" + reserveAmmo + (currentAmmo + reserveAmmo == maxTotalAmmo ? " Max!" : "") + "]";
    }

    @Override
    public String getWeaponDisplay() {
        return "Assets/Sprites/Pickups/WorldDBShotgun.png";
    }


    public void setOwner(Entity entity) {
        owner = entity;
        if (owner != null) {
            Drawable drawable = owner.getDrawableIfAny();
            if (drawable instanceof Animation) ownerAnimation = (Animation) drawable;
        }
    }

    private void createPellets(int count, float deviation) {
        if (HgGame.Network().getNetRole() == NetworkRole.Client) return;

        Angle ownerAngle = owner.getAngle();
        Vector2 spawnPosition = new Vector2(owner.getPosition()).add(ownerAngle.normalVector().scl(87).add(ownerAngle.normalVector().rotate90(-1).scl(23)));

        for (int pellet = 0; pellet < count; pellet++) {
            Angle moddedAngle = new Angle(ownerAngle).add(MathTools.Interp((float) pellet / (count - 1), -deviation, deviation));
            Vector2 moddedPos = new Vector2(spawnPosition).add(ownerAngle.normalVector().rotate90(-1).scl(MathTools.Interp((float) pellet / (count - 1), -4, 4)));
            var boolet = HgGame.Manager().addActor(ActorType.Bullet, moddedPos, moddedAngle.getDeg());
            ((Bullet) boolet).setSpeed(30);
            boolet.getColliderIfAny().attackStats = new AttackStats(owner, 8f, ColliderGroup.Player);
            ((BasicSprite) boolet.getDrawableIfAny()).setTexture(HgGame.Assets().loadTexture("Assets/Sprites/Projectiles/Pellet.png"));
        }
    }

    private void tryShot(boolean isPowerShot) {
        int ammoConsumed = isPowerShot ? 2 : 1;
        if (weaponCooldown > 0 || currentAmmo < ammoConsumed || reloadCounter > 0) return;
        currentAmmo -= ammoConsumed;
        weaponCooldown = shotCooldown + (isPowerShot ? 22 : 0);
        createPellets(isPowerShot ? 14 : 7, isPowerShot ? 16 : 7);
        if (ownerAnimation != null) {
            ownerAnimation.switchAnimation(isPowerShot ? "Shotgun_PowerShoot" : "Shotgun_Shoot");
        }
        pushbackPower = isPowerShot ? 24f : 14f;
        pushbackDirection = owner != null ? owner.getAngle().getDeg() : 0;
    }

    private void doReload() {
        int ammoToReplenish = Math.min(Math.max(magazineSize - currentAmmo, 0), reserveAmmo);
        currentAmmo += ammoToReplenish;
        reserveAmmo -= ammoToReplenish;
    }

    private void reset() {
        reloadCounter = 0;
        weaponCooldown = 1;
    }

    @Override
    public boolean onPrimaryFire() {
        return onFire(false);
    }

    @Override
    public boolean onSecondaryFire() {
        return onFire(true);
    }

    private boolean onFire(boolean isPowerShot) {
        if (owner == null || weaponCooldown > 0) return false;
        int ammoRequired = isPowerShot ? 2 : 1;
        if (currentAmmo < ammoRequired) {
            if (currentAmmo == 1) return onFire(false); // Attempt non-power shot

            if (reserveAmmo == 0 && localOnly_clickCooldown <= 0) {
                localOnly_clickCooldown = shotCooldown;
                HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
            }
            onReload(); // Automatically attempts reload
            return false;
        }
        tryShot(isPowerShot);
        weaponCooldown = shotCooldown + (isPowerShot ? 16 : 0);
        return true;
    }

    @Override
    public boolean onReload() {
        if (weaponCooldown > 0 || reloadCounter > 0 || currentAmmo >= magazineSize || reserveAmmo <= 0) return false;
        reloadCounter = timeToReload;
        if (ownerAnimation != null) ownerAnimation.switchAnimation("Shotgun_Reload");
        return true;
    }

    @Override
    public void onEquip() {
        weaponCooldown = 18;
        if (ownerAnimation == null) return;
        ownerAnimation.setDefaultAnimation("Shotgun_Idle");
        ownerAnimation.switchToDefault();
    }

    @Override
    public void onUnequip() {
        reset();
    }

    @Override
    public void onUpdate() {
        if (localOnly_clickCooldown > 0) localOnly_clickCooldown--;
        if (weaponCooldown > 0) weaponCooldown--;
        if (reloadCounter > 0) {
            reloadCounter--;
            if (reloadCounter == 0) doReload();
        }
        if (pushbackPower > 0f) {
            if (owner != null && owner instanceof PlayerEntity) {
                owner.getPosition().add(Angle.NormalVector(pushbackDirection).scl(-pushbackPower));
            }
            pushbackPower = Math.max(pushbackPower * 0.94f - 0.08f, 0f);
            if (MathTools.EqualEPS(pushbackPower, 0f, 0.1f))
                pushbackPower = 0f;
        }
    }

    @Override
    public void onBackpackUpdate() {

    }

    @Override
    public void onOwnerDeath() {
        reset();
    }

    @Override
    public void onWeaponPickup() {
        reserveAmmo = Math.min(reserveAmmo + weaponPickupAmmo, maxTotalAmmo - currentAmmo);
    }

    @Override
    public void onAmmoPackPickup() {
        reserveAmmo = Math.min(reserveAmmo + ammoPackAmmo, maxTotalAmmo - currentAmmo);
    }

    @Override
    public State tryGetState() {
        DBShotgunState stuff = new DBShotgunState();
        stuff.currentAmmo = currentAmmo;
        stuff.reserveAmmo = reserveAmmo;
        stuff.weaponCooldown = weaponCooldown;
        stuff.reloadCounter = reloadCounter;
        stuff.pushbackDirection = pushbackDirection;
        stuff.pushbackPower = pushbackPower;
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof DBShotgunState) {
            DBShotgunState stuff = (DBShotgunState) state;
            currentAmmo = stuff.currentAmmo;
            reserveAmmo = stuff.reserveAmmo;
            weaponCooldown = stuff.weaponCooldown;
            reloadCounter = stuff.reloadCounter;
            pushbackDirection = stuff.pushbackDirection;
            pushbackPower = stuff.pushbackPower;
        }
    }
}