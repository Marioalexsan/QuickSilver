package hg.interfaces;

import hg.gamelogic.states.State;

public interface IWeapon {

    // "Callbacks"
    // PF, SF, Reload, Equip, Unequip, Update, BackpackUpdate

    /** Called when weapon should be fired via Primary Fire. Returns true if action succeeded */
    boolean onPrimaryFire();

    /** Called when weapon should be fired via Secondary Fire. Returns true if action succeeded */
    boolean onSecondaryFire();

    /** Called when Reload is active. Returns true if action succeeded */
    boolean onReload();

    /** Called when the weapon is equipped */
    void onEquip();

    /** Called when weapon is unequipped */
    void onUnequip();

    /** Called with owner's update() if active weapon */
    void onUpdate();

    /** Called with owner's update() if not active weapon */
    void onBackpackUpdate();

    /** Called if the owner dies */
    void onOwnerDeath();

    /** Called if owner picks up a generic ammo pack */
    void onAmmoPackPickup();

    /** Called if owner picks up another weapon of the same type */
    void onWeaponPickup();

    /** Gets weapon state */
    default State tryGetState() {
        return null;
    }

    /** Applies weapon state */
    default void tryApplyState(State state) {}
}
