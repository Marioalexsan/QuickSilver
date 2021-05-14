package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.types.DirectorType;
import hg.ui.BasicUIStates;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.utils.builders.BasicTextMaker;
import hg.utils.builders.ClickButtonMaker;

public class LobbyMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();

    public LobbyMenu() {
        AssetEngine assets = HgGame.Assets();

        Texture bigbox = assets.loadTexture("Assets/GUI/Button.png");
        BitmapFont couriernew36 = assets.loadFont("Assets/Fonts/CourierNew36.fnt");
        BitmapFont couriernew72 = assets.loadFont("Assets/Fonts/CourierNew72.fnt");

        ClickButtonMaker boxButton = new ClickButtonMaker().display(bigbox).font(couriernew36).clickArea(460, 150);
        BasicTextMaker labels = new BasicTextMaker().font(couriernew72).textPos(HPos.Center, VPos.Center);

        boolean isServer = HgGame.Network().isLocalOrServer();

        if (isServer) {
            menus.addObject("Lobby",
                    new ClickButtonMaker(boxButton).position(660, -200).text("Start Match").onClick(() -> {
                        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
                        if (director != null) director.startMatch();
                    }).build()
            );
        }

        menus.addObjects("Lobby",
                new ClickButtonMaker(boxButton).position(660, -400).text("Quit Lobby").onClick(() -> {
                    GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
                    if (director != null) director.signalStop();
                }).build(),
                new BasicTextMaker(labels).position(0, 480).text("In Lobby - " + (isServer ? "as Server" : "as Client")).makeGUI().build()
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
}
