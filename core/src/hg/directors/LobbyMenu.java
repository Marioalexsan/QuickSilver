package hg.directors;

import hg.drawables.BasicText;
import hg.enums.MapType;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.enums.DirectorType;
import hg.ui.CardMenu;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.ui.ToggleButton;
import hg.utils.builders.BasicTextBuilder;
import hg.utils.builders.ClickButtonBuilder;
import hg.utils.builders.ToggleButtonBuilder;

/** LobbyMenu is the menu that appears prior to a game session start. Server can set game session settings here (map, etc.), and wait for clients to join. */
public class LobbyMenu extends Director {
    private final CardMenu menus = new CardMenu();
    private final ToggleButton hardcoreSelect;
    private int mapSelection = MapType.Grinder;
    private final BasicText mapLabel;

    public LobbyMenu() {
        ClickButtonBuilder boxButton = BuilderLibrary.ClickButtonBuilders("silverbox");
        ClickButtonBuilder leftArrow = BuilderLibrary.ClickButtonBuilders("leftarrow");
        ClickButtonBuilder leftArrowClient = BuilderLibrary.ClickButtonBuilders("leftarrowdisabled");
        BasicTextBuilder label = BuilderLibrary.BasicTextBuilders("label");
        ToggleButtonBuilder select = BuilderLibrary.ToggleButtonBuilders("silvercheck");
        ToggleButtonBuilder selectClient = BuilderLibrary.ToggleButtonBuilders("silverdisabled");

        boolean isServer = HgGame.Network().isLocalOrServer();

        menus.addObjects("Lobby",
                boxButton.copy().position(660, -400).text("Quit Lobby").onClick(this::quitLobby).build(),
                label.copy().position(0, 480).text("In Lobby - " + (isServer ? "as Server" : "as Client")).makeGUI().build(),
                isServer ? boxButton.copy().position(660, -200).text("Start Match").onClick(this::tryStartMatch).build() : null, // Not added if client
                hardcoreSelect = (isServer ? select : selectClient).copy().position(-880, -200).build(),
                label.copy().position(-780, -200).text("Hardcore Mode").textPos(HPos.Left, VPos.Center).makeGUI().build(),
                (isServer ? leftArrow : leftArrowClient).copy().position(-880, -300).angle(180).onClick(isServer ? this::cycleMap : null).build(),
                mapLabel = label.copy().position(-780, -300).text("<not_init>").textPos(HPos.Left, VPos.Center).makeGUI().build()
        );

        if (isServer) {
            hardcoreSelect.setActivateCallback(this::hardcoreToggle);
            hardcoreSelect.setInactivateCallback(this::hardcoreToggle);
        }
        else {
            hardcoreSelect.setClickEnabled(false);
        }

        menus.scheduleStateSwitch("Lobby");
        updateMap();
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void update() {
        menus.onUpdate();
        if (!HgGame.Network().isLocalOrServer()) {
            GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
            if (director != null) {
                if (director.getSettings().hardcore ^ hardcoreSelect.isActive()) {
                    hardcoreSelect.toggle();
                }
                if (director.getSettings().map != mapSelection) {
                    mapSelection = director.getSettings().map;
                    updateMap();
                }
            }
        }
    }

    // Callbacks

    private void cycleMap() {
        switch (mapSelection) {
            case MapType.Grinder -> mapSelection = MapType.Duel;
            default -> mapSelection = MapType.Grinder;
        }

        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) {
            var sets = director.getSettings();
            sets.map = mapSelection;
            director.updateSettings(sets);
        }

        updateMap();
    }

    private void updateMap() {
        String which = null;
        switch (mapSelection) {
            case MapType.Grinder -> which = "Grinder";
            case MapType.Duel -> which = "Duel";
            default -> which = "Unknown";
        }
        mapLabel.setText("Map: " + which);
    }

    private void hardcoreToggle() {
        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) {
            var sets = director.getSettings();
            sets.hardcore = hardcoreSelect.isActive();
            director.updateSettings(sets);
        }
    }

    private void tryStartMatch() {
        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) director.startMatch();
    }

    private void quitLobby() {
        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) director.signalStop();
    }
}
