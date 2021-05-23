package hg.gamelogic.states;

/** Holds the network state of a Revolver */
public class RevolverState extends State {
    public int currentAmmo = 0;
    public int reserveAmmo = 30;
    public int weaponCooldown = 0;
    public int reloadCounter = 0;
}
