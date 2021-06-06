package hg.weapons;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Animation;
import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.ObjectState;
import hg.interfaces.IWeapon;
import hg.networking.NetworkRole;
import hg.physics.ColliderGroup;
import hg.enums.ActorType;
import hg.utils.Angle;

/** A rifle that shoots in burst of three. Has high DPS, and excels in all situations */
public class AssaultRifle implements IWeapon {
    public static class State extends ObjectState {
        public int currentAmmo;
        public int reserveAmmo;
        public int bulletsToFire;
        public int burstShotCooldown;
        public int reloadCounter;
    }

    // Owner related stuff

    private Entity owner = null;
    private Animation ownerAnimation = null;

    // Weapon stats

    private final int magazineSize = 15;
    private final int maxTotalAmmo = 60;
    private final int burstCooldown = 52;
    private final int burstShotInterval = 5;
    private final int burstCount = 3;
    private final int timeToReload = 120;
    private final int weaponPickupAmmo = 15;
    private final int ammoPackAmmo = 9;

    // Weapon state

    private int currentAmmo = 15;
    private int reserveAmmo = 15;
    private int bulletsToFire = 0;
    private int burstShotCooldown = 0;
    private int weaponCooldown = 0;

    private int reloadCounter = 0;

    private int localOnly_clickCooldown;

    public AssaultRifle(Entity owner) {
        setOwner(owner);
    }

    public void setOwner(Entity entity) {
        owner = entity;
        if (owner != null) {
            Drawable drawable = owner.getDrawable();
            if (drawable instanceof Animation) ownerAnimation = (Animation) drawable;
        }
    }

    @Override
    public String getAmmoDisplay() {
        return IWeapon.getGenericAmmoDisplay(currentAmmo, magazineSize, reserveAmmo, maxTotalAmmo);
    }

    @Override
    public String getWeaponDisplay() {
        return "Assets/Sprites/Pickups/WorldRifle.png";
    }

    private void createBullet() {
        if (HgGame.Network().getNetRole() == NetworkRole.Client) return;

        Angle ownerAngle = owner.getAngle();
        Vector2 spawnPosition = new Vector2(owner.getPosition()).add(ownerAngle.normalVector().scl(75).add(ownerAngle.normalVector().rotate90(-1).scl(25)));

        var boolet = HgGame.Manager().addActor(ActorType.Bullet, spawnPosition, ownerAngle.getDeg());
        boolet.getColliderArray()[0].attackStats = new AttackStats(owner, 17f, ColliderGroup.Player);
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
        weaponCooldown = 1;
        bulletsToFire = 0;
        burstShotCooldown = 0;
    }

    @Override
    public boolean onPrimaryFire() {
        if (owner == null || weaponCooldown > 0) return false;
        if (currentAmmo <= 0) {
            if (reserveAmmo == 0 && localOnly_clickCooldown <= 0) {
                localOnly_clickCooldown = burstCooldown;
                HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
            }
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
        if (ownerAnimation == null) return;
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

        if (localOnly_clickCooldown > 0) localOnly_clickCooldown--;
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

    @Override
    public ObjectState tryGetState() {
        AssaultRifle.State stuff = new AssaultRifle.State();
        stuff.currentAmmo = currentAmmo;
        stuff.reserveAmmo = reserveAmmo;
        stuff.bulletsToFire = bulletsToFire;
        stuff.burstShotCooldown = burstShotCooldown;
        stuff.reloadCounter = reloadCounter;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof AssaultRifle.State) {
            AssaultRifle.State stuff = (AssaultRifle.State) state;
            currentAmmo = stuff.currentAmmo;
            reserveAmmo = stuff.reserveAmmo;
            bulletsToFire = stuff.bulletsToFire;
            burstShotCooldown = stuff.burstShotCooldown;
            reloadCounter = stuff.reloadCounter;
        }
    }
}
