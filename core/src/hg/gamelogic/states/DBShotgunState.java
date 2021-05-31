package hg.gamelogic.states;

/** Holds the network state of a Double Barrel Shotgun */
public class DBShotgunState extends State {
    public int currentAmmo = 0;
    public int reserveAmmo = 30;
    public int weaponCooldown = 0;
    public int reloadCounter = 0;
    public float pushbackDirection = 0f;
    public float pushbackPower = 0f;
}