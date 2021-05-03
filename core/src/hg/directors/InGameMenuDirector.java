package hg.directors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicSprite;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.ui.ClickButton;

public class InGameMenuDirector extends Director {
    private final BasicSprite vignette;
    private final BasicText title;
    private final ClickButton quitToMenu;

    public InGameMenuDirector() {
        title = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew48.fnt"), "In Pause Menu");
        title.setPosition(new Vector2(0, 440));
        title.setConstraints(BasicText.HPos.Center, BasicText.VPos.Center, 0f);
        title.setCameraUse(false);
        title.setLayer(DrawLayer.GUIDefault);
        title.setEnabled(false);

        vignette = new BasicSprite(HgGame.Assets().loadTexture("Assets/GUI/PauseVignette.png"));
        vignette.centerToRegion();
        vignette.setCameraUse(false);
        vignette.setLayer(DrawLayer.GUIDefault - 1);
        vignette.setEnabled(false);

        quitToMenu = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Quit to menu");
        quitToMenu.setPosition(0, -200);
        quitToMenu.setCallback(this::quitToMenuClicked);
        quitToMenu.setEnabled(false);

        title.registerToEngine();
        vignette.registerToEngine();
    }

    private void start() {
        started = true;
        title.setEnabled(true);
        quitToMenu.setEnabled(true);
        vignette.setEnabled(true);

        HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.InGameMenu);
    }

    private void quitToMenuClicked() {
        Director match = HgGame.Entities().getDirector(DirectorTypes.MatchDirector);
        if (match != null) ((MatchDirector) match).receiveStop();
        toBeDestroyed = true;
    }

    @Override
    public void destroy() {
        HgGame.Input().removeFocusInput(this);
        title.unregisterFromEngine();
        vignette.unregisterFromEngine();
        quitToMenu.destroy();
    }

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (!started) start();

        boolean hasFocus = HgGame.Input().inputHasFocus(this);

        if (hasFocus && HgGame.Input().isButtonTapped(Input.Buttons.LEFT)) {
            var pos = HgGame.Input().getMouse();
            quitToMenu.onLMBDown(pos.x, pos.y);
        }

        if (hasFocus && HgGame.Input().isActionTapped(MappedAction.Escape)) toBeDestroyed = true;
    }
}
