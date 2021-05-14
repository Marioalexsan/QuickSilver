package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hg.drawables.DrawLayer;
import hg.engine.AssetEngine;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.types.DirectorType;
import hg.ui.BasicUIStates;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.utils.builders.BasicSpriteMaker;
import hg.utils.builders.BasicTextMaker;
import hg.utils.builders.ClickButtonMaker;

public class PauseMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();


    public PauseMenu() {
        AssetEngine assets = HgGame.Assets();

        Texture bigbox = assets.loadTexture("Assets/GUI/Button.png");
        Texture vignette = assets.loadTexture("Assets/GUI/PauseVignette.png");
        BitmapFont couriernew36 = assets.loadFont("Assets/Fonts/CourierNew36.fnt");
        BitmapFont couriernew72 = assets.loadFont("Assets/Fonts/CourierNew72.fnt");

        ClickButtonMaker boxButton = new ClickButtonMaker().display(bigbox).font(couriernew36).clickArea(460, 150);
        BasicTextMaker labels = new BasicTextMaker().font(couriernew72).textPos(HPos.Center, VPos.Center);

        menus.addState("NonActive");
        menus.addObjects("PauseMenu",
                new BasicTextMaker(labels).position(0, 440).text("In Pause Menu").build(),
                new BasicSpriteMaker().texture(vignette).position(0, 0).texture(vignette).cameraUse(false).layer(DrawLayer.GUIDefault - 1).centerToRegion(true).build(),
                new ClickButtonMaker(boxButton).position(0, -200).text("Quit to Menu").onClick(() -> {
                    GameSession match = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
                    if (match != null) match.signalStop();
                    toBeDestroyed = true;
                }).build()
        );

        menus.scheduleStateSwitch("NonActive");
    }

    @Override
    public void destroy() {
        HgGame.Input().removeFocusInput(this);
        menus.destroy();
    }

    @Override
    public void update() {
        InputEngine input = HgGame.Input();

        if (input.getTopFocusPriority() <= InputEngine.FocusPriorities.PauseMenu && input.isActionTapped(MappedAction.Escape)) {
            if (menus.getCurrentState().equals("NonActive")) {
                menus.scheduleStateSwitch("PauseMenu");
                input.addFocusInput(this, InputEngine.FocusPriorities.PauseMenu);
            }
            else {
                menus.scheduleStateSwitch("NonActive");
                input.removeFocusInput(this);
            }
        }

        menus.onUpdate();
    }
}
