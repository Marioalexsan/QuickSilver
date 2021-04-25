package hg.interfaces;

public interface IWeapon {

    // "Callbacks"
    // PF, SF, Reload, Equip, Unequip, Update, BackpackUpdate

    /** Called when weapon should be fired via Primary Fire
     * @return True if the action was successful (i.e. the weapon fired) */
    boolean onPrimaryFire();

    /** Called when weapon should be fired via Secondary Fire
     * @return True if the action was successful (i.e. the weapon fired) */
    boolean onSecondaryFire();

    /** Called when Reload is active
     * @return True if the action was successful (i.e. the weapon is now reloading) */
    boolean onReload();

    /** Called when the weapon is equipped */
    void onEquip();

    /** Called when weapon is unequipped */
    void onUnequip();

    /** Called when the owner's update method is called */
    void onUpdate();

    /** Called when the owner's update method is called, and the weapon is in the player's inventory,
     * but not the currently equipped weapon */
    void onBackpackUpdate();

    /** Called if the owner dies */
    void onOwnerDeath();
}
