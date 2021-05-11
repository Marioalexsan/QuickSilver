package hg.networking.packets;

import hg.directors.DirectorTypes;
import hg.directors.LobbyDirector;
import hg.directors.MatchDirector;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.networking.Packet;

public class GameSessionStart extends Packet {
    @Override
    public void parseOnClient() {
        GameManager manager = HgGame.Manager();

        MatchDirector match = (MatchDirector) manager.getDirector(DirectorTypes.MatchDirector);
        if (match != null) match.startMatch();

        LobbyDirector lobby = (LobbyDirector) manager.getDirector(DirectorTypes.LobbyDirector);
        if (lobby != null) lobby.signalDestroy();
    }

    public GameSessionStart() {} // For Kryonet
}
