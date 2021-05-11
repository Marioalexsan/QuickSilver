package hg.libraries;

import hg.engine.NetworkEngine;
import hg.entities.Entity;
import hg.entities.GenericBullet;
import hg.entities.PlayerEntity;
import hg.game.HgGame;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.types.ActorType;

/**
 * Holds descriptions of entities that are related to the gameplay.
 * Example: Player, NPCs, etc.
 */
public class ActorLibrary {
    public static Entity CreateActor(int type) {
        switch(type) {
            case ActorType.PlayerEntity -> {
                return new PlayerEntity(new EmptyAI());
            }
            case ActorType.GenericBullet -> {
                return new GenericBullet();
            }
        }
        return null;
    }
}
