package hg.networking.packets;

import hg.enums.types.DirectorType;
import hg.directors.MainMenu;
import hg.directors.GameSession;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;

/** Server message that tells the game started */
public class GameSessionStart extends Packet {
    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        GameSession match = (GameSession) manager.getDirector(DirectorType.GameSession);
        if (match != null) {
            match.startMatch();
        }
        else {
            MainMenu menu = (MainMenu) manager.getDirector(DirectorType.MainMenu);
            if (menu != null) menu.signalEarlyStart();
        }


    }

    public GameSessionStart() {} // For Kryonet
}
