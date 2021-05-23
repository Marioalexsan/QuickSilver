package hg.gamelogic.states;

/** Holds the network state of an Assault Rifle */
public class AssaultRifleState extends State {
    public int currentAmmo;
    public int reserveAmmo;
    public int bulletsToFire;
    public int burstShotCooldown;
    public int reloadCounter;
}
