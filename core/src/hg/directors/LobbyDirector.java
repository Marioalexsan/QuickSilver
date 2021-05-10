package hg.directors;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.game.HgGame;
import hg.networking.NetworkStatus;
import hg.ui.BasicUIStates;
import hg.ui.ClickButton;

public class LobbyDirector extends Director {
    private final BasicUIStates menus = new BasicUIStates();

    public LobbyDirector() {
        // This only has one state for now

        boolean isServer = HgGame.Network().isLocalOrServer();

        menus.addState("Lobby");

        if (isServer) {
            ClickButton generic_startGame = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Start Game");
            generic_startGame.setPosition(660, -200);
            generic_startGame.setCallback(() -> {
                MatchDirector director = (MatchDirector) HgGame.Manager().getDirector(DirectorTypes.MatchDirector);
                if (director != null) director.startMatch();
                toBeDestroyed = true;
            });
            menus.addStateElement("Lobby", "StartGame", generic_startGame);
        }

        ClickButton generic_quitLobby = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Quit Lobby");
        generic_quitLobby.setPosition(660, -400);
        generic_quitLobby.setCallback(() -> {
            MatchDirector director = (MatchDirector) HgGame.Manager().getDirector(DirectorTypes.MatchDirector);
            if (director != null) director.receiveStop();
            toBeDestroyed = true;
        });
        menus.addStateElement("Lobby", "QuitLobby", generic_quitLobby);

        BasicText generic_thisIsLobby = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"), "In Lobby" + (isServer ? " - as Server" : " - as Client"));
        generic_thisIsLobby.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        generic_thisIsLobby.setPosition(new Vector2(0, 480));
        generic_thisIsLobby.setCameraUse(false);
        generic_thisIsLobby.registerToEngine();
        menus.addStateElement("Lobby", "LobbyTitleText", generic_thisIsLobby);

        menus.scheduleSwitchState("Lobby");
    }

    @Override
    public void destroy() {
        menus.destroy();
    }

    @Override
    public void update() {
        menus.onUpdate();
    }
}
