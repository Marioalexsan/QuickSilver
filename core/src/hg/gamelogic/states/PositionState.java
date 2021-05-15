package hg.gamelogic.states;

import hg.entities.Entity;

/** Just a state which has position baked in. Use this for states that also need position. */
public abstract class PositionState extends State {
    public float posX;
    public float posY;
    public float angle;

    public void copyPosition(Entity target) {
        posX = target.getPosition().x;
        posY = target.getPosition().y;
        angle = target.getAngle().getDeg();
    }

    public void applyPosition(Entity target) {
        target.getPosition().set(posX, posY);
        target.getAngle().set(angle);
    }
}
