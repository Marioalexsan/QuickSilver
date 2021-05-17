package hg.networking.packets;

import hg.directors.GameSession;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.enums.types.DirectorType;

public class SessionSettingsUpdate extends Packet {
    public GameSession.SessionOptions settings;

    public SessionSettingsUpdate(GameSession.SessionOptions settings) {
        this.settings = settings;
    }

    @Override
    public void parseOnClient() {
        GameSession match = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (match != null) match.updateSettings(settings);
    }

    public SessionSettingsUpdate() {} // For Kryonet
}
