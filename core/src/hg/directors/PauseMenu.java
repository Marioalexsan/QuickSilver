package hg.directors;

import com.badlogic.gdx.graphics.Texture;
import hg.drawables.DrawLayer;
import hg.engine.AssetEngine;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.enums.DirectorType;
import hg.ui.CardMenu;
import hg.utils.builders.BasicSpriteBuilder;
import hg.utils.builders.BasicTextBuilder;
import hg.utils.builders.ClickButtonBuilder;

/** PauseMenu is the menu that appears during gameplay if you hit Escape. It is used to quit a game session and go to the main menu.
 * Quitting will disconnect / close a server! */
public class PauseMenu extends Director {
    private final CardMenu menus = new CardMenu();


    public PauseMenu() {
        AssetEngine assets = HgGame.Assets();

        Texture vignette = assets.loadTexture("Assets/GUI/PauseVignette.png");
        ClickButtonBuilder boxButton = BuilderLibrary.ClickButtonBuilders("silverbox");
        BasicTextBuilder label = BuilderLibrary.BasicTextBuilders("label");

        menus.addState("NonActive");
        menus.addObjects("PauseMenu",
                new BasicSpriteBuilder().texture(vignette).position(0, 0).texture(vignette).cameraUse(false).layer(DrawLayer.GUIDefault - 1).centerToRegion(true).build(),
                label.copy().position(0, 440).text("In Pause Menu").makeGUI().build(),
                boxButton.copy().position(0, -200).text("Quit to Menu").onClick(() -> {
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
