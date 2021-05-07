package hg.directors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.ui.BasicTextInput;
import hg.ui.BasicUIStates;
import hg.ui.ClickButton;

public class InGameMenu extends Director {
    private final BasicUIStates menus = new BasicUIStates();


    public InGameMenu() {
        // Only has two states
        menus.addState("PauseMenu");
        menus.addState("NonActive");

        BasicText title = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"), "In Pause Menu");
        title.setPosition(new Vector2(0, 440));
        title.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        title.setCameraUse(false);
        title.setLayer(DrawLayer.GUIDefault);
        title.registerToEngine();
        menus.addStateElement("PauseMenu", "MenuTitle", title);

        BasicSprite vignette = new BasicSprite(HgGame.Assets().loadTexture("Assets/GUI/PauseVignette.png"));
        vignette.centerToRegion();
        vignette.setCameraUse(false);
        vignette.setLayer(DrawLayer.GUIDefault - 1);
        vignette.registerToEngine();
        menus.addStateElement("PauseMenu", "DarkenScreen", vignette);

        ClickButton quitToMenu = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Quit to menu");
        quitToMenu.setPosition(0, -200);
        quitToMenu.setCallback(() -> {
            MatchDirector match = (MatchDirector) HgGame.Manager().getDirector(DirectorTypes.MatchDirector);
            if (match != null) match.receiveStop();
            toBeDestroyed = true;
        });
        menus.addStateElement("PauseMenu", "QuitToMenu", quitToMenu);

        menus.scheduleSwitchState("NonActive");
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
                menus.scheduleSwitchState("PauseMenu");
                input.addFocusInput(this, InputEngine.FocusPriorities.PauseMenu);
            }
            else {
                menus.scheduleSwitchState("NonActive");
                input.removeFocusInput(this);
            }
        }

        menus.onUpdate();
    }
}
