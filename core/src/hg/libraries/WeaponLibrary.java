package hg.libraries;

import hg.interfaces.IWeapon;
import hg.enums.types.WeaponType;
import hg.weapons.AssaultRifle;
import hg.weapons.DBShotgun;
import hg.weapons.Revolver;
import org.sqlite.core.DB;

/** Holds weapons used in the game. */
public class WeaponLibrary {
    public static IWeapon GetWeapon(int type) {
        IWeapon which = null;
        switch (type) {
            case WeaponType.Revolver -> which = new Revolver(null);
            case WeaponType.AssaultRifle -> which = new AssaultRifle(null);
            case WeaponType.DBShotgun -> which = new DBShotgun(null);
        }
        return which;
    }

    public static String GetWeaponPickupHint(int type) {
        String which = "Got a Weapon!";
        switch (type) {
            case WeaponType.Revolver -> which = "Picked up a Revolver!";
            case WeaponType.AssaultRifle -> which = "Picked up an Assault Rifle";
            case WeaponType.DBShotgun -> which = "Picked up a Double Barrel Shotgun!";
        }
        return which;
    }
}
