package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.types.DirectorType;
import hg.ui.BasicUIStates;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.utils.builders.BasicTextBuilder;
import hg.utils.builders.ClickButtonBuilder;

public class LobbyMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();

    public LobbyMenu() {
        ClickButtonBuilder boxButton = BuilderLibrary.ClickButtonBuilders("silverbox");
        BasicTextBuilder label = BuilderLibrary.BasicTextBuilders("label");

        boolean isServer = HgGame.Network().isLocalOrServer();

        menus.addObjects("Lobby",
                boxButton.copy().position(660, -400).text("Quit Lobby").onClick(this::quitLobby).build(),
                label.copy().position(0, 480).text("In Lobby - " + (isServer ? "as Server" : "as Client")).makeGUI().build(),
                isServer ? boxButton.copy().position(660, -200).text("Start Match").onClick(this::tryStartMatch).build() : null // Not added if client
        );

        menus.scheduleStateSwitch("Lobby");
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void update() {
        menus.onUpdate();
    }

    // Callbacks

    private void tryStartMatch() {
        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) director.startMatch();
    }

    private void quitLobby() {
        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) director.signalStop();
    }
}
