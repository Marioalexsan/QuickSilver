package hg.engine;

/** MappedActions are an abstract form of input.
 * Keyboard, mouse input, AI logic, and network messages are translated to this, instead of being used directly! */
public class MappedAction {
    public static final int PrimaryFire = 0;
    public static final int SecondaryFire = 1;
    public static final int Reload = 2;
    public static final int MoveUp = 3;
    public static final int MoveDown = 4;
    public static final int MoveLeft = 5;
    public static final int MoveRight = 6;
    public static final int Escape = 7;
    public static final int QuickSwitchWeapon = 8;
    public static final int WeaponOne = 9;
    public static final int WeaponTwo = 10;
    public static final int WeaponThree = 11;
    public static final int WeaponFour = 12;
    public static final int ChatSubmit = 14;
    public static final int Boost = 15;
}
