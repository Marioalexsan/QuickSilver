package hg.libraries;

import hg.entities.Entity;
import hg.entities.GenericBullet;
import hg.entities.Player;
import hg.playerlogic.EmptyAI;

/**
 * Holds descriptions of entities that are related to the gameplay.
 * Example: Player, NPCs, etc.
 */
public class ActorLibrary {
    public enum Types {
        Player,
        GenericBullet
    }

    public static Entity CreateActor(Types type) {
        switch(type) {
            case Player -> {
                return new Player(new EmptyAI());
            }
            case GenericBullet -> {
                return new GenericBullet();
            }
        }
        return null;
    }
}
