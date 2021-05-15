package hg.gamelogic.states;

import hg.gamelogic.BaseStats;

import java.util.HashMap;


public class PlayerState extends PositionState {
    public float smoothSpeedX;
    public float smoothSpeedY;
    public BaseStats baseStats; // Does not send Entity

    public int currentWeapon;
    public HashMap<Integer, State> weaponStates;
}