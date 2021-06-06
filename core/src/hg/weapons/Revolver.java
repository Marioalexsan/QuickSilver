package hg.weapons;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Animation;
import hg.drawables.Drawable;
import hg.entities.Entity;
import hg.entities.Bullet;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.ObjectState;
import hg.interfaces.IWeapon;
import hg.networking.NetworkRole;
import hg.physics.ColliderGroup;
import hg.enums.ActorType;
import hg.utils.Angle;

/** A revolver that acts as the starting weapon for players. It doesn't have a lot of power, but it can still help in a pinch. */
public class Revolver implements IWeapon {
    public static class State extends ObjectState {
        public int currentAmmo = 0;
        public int reserveAmmo = 30;
        public int weaponCooldown = 0;
        public int reloadCounter = 0;
    }
    // Owner related stuff

    private Entity owner = null;
    private Animation ownerAnimation = null;

    // Weapon stats

    private final int magazineSize = 6;
    private final int maxTotalAmmo = 30;
    private final int shotCooldown = 30;
    private final int timeToReload = 120;
    private final int weaponPickupAmmo = 6;
    private final int ammoPackAmmo = 3;

    // Weapon state

    private int currentAmmo = 6;
    private int reserveAmmo = 18;
    private int weaponCooldown = 0;

    private int reloadCounter = 0;

    private int localOnly_clickCooldown;

    public Revolver(Entity owner) {
        setOwner(owner);
    }

    @Override
    public String getAmmoDisplay() {
        return IWeapon.getGenericAmmoDisplay(currentAmmo, magazineSize, reserveAmmo, maxTotalAmmo);
    }

    @Override
    public String getWeaponDisplay() {
        return "Assets/Sprites/Pickups/WorldRevolver.png";
    }


    public void setOwner(Entity entity) {
        owner = entity;
        if (owner != null) {
            Drawable drawable = owner.getDrawable();
            if (drawable instanceof Animation) ownerAnimation = (Animation) drawable;
        }
    }

    private void createBullet() {
        if (HgGame.Network().getNetRole() == NetworkRole.Client) return;

        Angle ownerAngle = owner.getAngle();
        Vector2 spawnPosition = new Vector2(owner.getPosition()).add(ownerAngle.normalVector().scl(75).add(ownerAngle.normalVector().rotate90(-1).scl(13)));

        var boolet = HgGame.Manager().addActor(ActorType.Bullet, spawnPosition, ownerAngle.getDeg());
        ((Bullet) boolet).setSpeed(42);
        boolet.getColliderArray()[0].attackStats = new AttackStats(owner, 24f, ColliderGroup.Player);
    }

    private void tryShot() {
        if (weaponCooldown > 0 || currentAmmo <= 0 || reloadCounter > 0) return;
        currentAmmo--;
        weaponCooldown = shotCooldown;
        createBullet();
        if (ownerAnimation != null) {
            //ownerAnimation.switchAnimation("Shotgun_PowerShoot");
            ownerAnimation.switchAnimation("Revolver_Shoot");
        }
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
        if (owner == null || weaponCooldown > 0) return false;
        if (currentAmmo <= 0) {
            if (reserveAmmo == 0 && localOnly_clickCooldown <= 0) {
                localOnly_clickCooldown = shotCooldown;
                HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
            }
            onReload(); // Automatically attempts reload
            return false;
        }
        tryShot();
        weaponCooldown = shotCooldown;
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
        if (ownerAnimation != null) ownerAnimation.switchAnimation("Revolver_Reload");
        return true;
    }

    @Override
    public void onEquip() {
        weaponCooldown = 30;
        if (ownerAnimation == null) return;
        ownerAnimation.setDefaultAnimation("Revolver_Idle");
        //ownerAnimation.setDefaultAnimation("Shotgun_Idle");
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
        State stuff = new State();
        stuff.currentAmmo = currentAmmo;
        stuff.reserveAmmo = reserveAmmo;
        stuff.weaponCooldown = weaponCooldown;
        stuff.reloadCounter = reloadCounter;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            currentAmmo = stuff.currentAmmo;
            reserveAmmo = stuff.reserveAmmo;
            weaponCooldown = stuff.weaponCooldown;
            reloadCounter = stuff.reloadCounter;
        }
    }
}