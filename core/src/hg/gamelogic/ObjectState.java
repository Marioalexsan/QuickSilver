package hg.gamelogic;

import hg.entities.Entity;

import java.util.HashMap;

/** Base class for all structures which deal with saving / restoring entity / game / whatever state.
 * Ideally, State should only hold primitive data, since State objects may also be sent over the network.
 * States should be registered in NetworkHelper if sent over the network, along with any other non-primitive objects held. */
public abstract class ObjectState {
    public abstract static class PositionState extends ObjectState {
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

}
