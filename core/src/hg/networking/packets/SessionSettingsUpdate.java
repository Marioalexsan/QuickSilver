package hg.networking.packets;

import hg.directors.GameSession;
import hg.directors.MainMenu;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.enums.DirectorType;

/** Tells the client the current game session options. Usually sent by server when clients join, or when said options change. */
public class SessionSettingsUpdate extends Packet {
    public GameSession.SessionOptions settings;

    public SessionSettingsUpdate(GameSession.SessionOptions settings) {
        this.settings = settings;
    }

    @Override
    public void parseOnClient() {
        GameSession match = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (match != null) match.updateSettings(settings);
        else {
            // Give them to Main Menu, it will forward them to Match
            MainMenu main = (MainMenu) HgGame.Manager().getDirector(DirectorType.MainMenu);
            if (main != null) main.receiveOptions(settings);
        }
    }

    public SessionSettingsUpdate() {} // For Kryonet
}
