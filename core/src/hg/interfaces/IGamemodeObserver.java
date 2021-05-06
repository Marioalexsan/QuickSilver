package hg.interfaces;

import hg.gamelogic.Team;

/** IGameModeObserver is notified of changes in the game mode state */
public interface IGamemodeObserver {

    /** Called when the game mode starts. Doesn't necessarily have to coincide with the match start */
    default void onGameStart() {

    }

    /** Called when the game mode ends, i.e. there is a conclusion of some kind. */
    default void onGameEnd(Team winner) {

    }
}
