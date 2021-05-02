package hg.libraries;

import hg.entities.Entity;
import hg.entities.GenericBullet;
import hg.entities.Player;
import hg.playerlogic.EmptyAI;
import hg.types.ActorType;

/**
 * Holds descriptions of entities that are related to the gameplay.
 * Example: Player, NPCs, etc.
 */
public class ActorLibrary {
    public static Entity CreateActor(int type) {
        switch(type) {
            case ActorType.Player -> {
                return new Player(new EmptyAI());
            }
            case ActorType.GenericBullet -> {
                return new GenericBullet();
            }
        }
        return null;
    }
}
