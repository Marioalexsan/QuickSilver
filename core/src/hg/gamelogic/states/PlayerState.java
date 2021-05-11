package hg.gamelogic.states;

import hg.gamelogic.BaseStats;

import java.util.HashMap;


public class PlayerState extends State {
    public float posX;
    public float posY;
    public float angle;
    public float smoothSpeedX;
    public float smoothSpeedY;
    public BaseStats baseStats; // Does not send Entity

    public int currentWeapon;
    public HashMap<Integer, State> weaponStates;
}