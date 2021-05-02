package hg.weapons;

import com.badlogic.gdx.math.Vector2;
import hg.animation.Animation;
import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.interfaces.IWeapon;
import hg.libraries.ActorLibrary;
import hg.physics.ColliderGroup;
import hg.types.ActorType;
import hg.utils.Angle;

public class AssaultRifle implements IWeapon {

    // Owner related stuff

    private Entity owner = null;
    private Animation ownerAnimation = null;

    // Weapon stats

    private final int magazineSize = 30;
    private final int maxTotalAmmo = 120;
    private final int burstCooldown = 35;
    private final int burstShotInterval = 7;
    private final int burstCount = 3;
    private final int timeToReload = 120;
    private final int weaponPickupAmmo = 30;
    private final int ammoPackAmmo = 6;

    // Weapon state

    private int currentAmmo = 0;
    private int reserveAmmo = 90;
    private int bulletsToFire = 0;
    private int burstShotCooldown = 0;
    private int weaponCooldown = 0;

    private int reloadCounter = 0;

    public AssaultRifle(Entity owner) {
        setOwner(owner);
    }

    public void setOwner(Entity entity) {
        owner = entity;
        Drawable drawable = owner.getDrawableIfAny();
        if (drawable instanceof Animation) ownerAnimation = (Animation) drawable;
    }

    private void createBullet() {
        Angle ownerAngle = owner.getAngle();
        Vector2 ownerPosition = owner.getPosition();

        var boolet = HgGame.Entities().addActor(ActorType.GenericBullet, ownerPosition, ownerAngle.getDeg());
        boolet.getPosition().add(ownerAngle.normalVector().scl(75).add(ownerAngle.normalVector().rotate90(-1).scl(25)));
        boolet.getColliderIfAny().attackStats = new AttackStats(owner, 17f, ColliderGroup.Player);
    }

    private void tryBurstShot() {
        if (burstShotCooldown > 0 || currentAmmo <= 0 || bulletsToFire <= 0 || reloadCounter > 0) return;
        currentAmmo--;
        bulletsToFire--;
        burstShotCooldown = burstShotInterval;
        createBullet();
        if (ownerAnimation != null) ownerAnimation.switchAnimation("Rifle_Shoot");
    }

    private void doReload() {
        int ammoToReplenish = Math.min(Math.max(magazineSize - currentAmmo, 0), reserveAmmo);
        currentAmmo += ammoToReplenish;
        reserveAmmo -= ammoToReplenish;
    }

    private void reset() {
        reloadCounter = 0;
        weaponCooldown = 0;
        bulletsToFire = 0;
    }

    @Override
    public boolean onPrimaryFire() {
        if (owner == null || weaponCooldown > 0) return false;
        if (currentAmmo <= 0) {
            onReload(); // Automatically attempts reload
            return false;
        }
        weaponCooldown = burstCooldown;
        bulletsToFire = burstCount;
        return true;
    }

    @Override
    public boolean onSecondaryFire() {
        return false; // Doesn't have one for now
    }

    @Override
    public boolean onReload() {
        if (weaponCooldown > 0 || reloadCounter > 0 || currentAmmo >= magazineSize || reserveAmmo <= 0) return false;
        reloadCounter = timeToReload;
        if (ownerAnimation != null) ownerAnimation.switchAnimation("Rifle_Reload");
        return true;
    }

    @Override
    public void onEquip() {
        weaponCooldown = 30;
        ownerAnimation.setDefaultAnimation("Rifle_Idle");
        ownerAnimation.switchToDefault();
    }

    @Override
    public void onUnequip() {
        reset();
    }

    @Override
    public void onUpdate() {
        tryBurstShot();

        if (burstShotCooldown > 0) burstShotCooldown--;
        if (weaponCooldown > 0) weaponCooldown--;
        if (reloadCounter > 0) {
            reloadCounter--;
            if (reloadCounter == 0) doReload();
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

}
