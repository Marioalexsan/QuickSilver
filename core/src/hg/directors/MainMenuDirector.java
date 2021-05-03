package hg.directors;

import com.badlogic.gdx.Input;
import hg.game.HgGame;
import hg.ui.ClickButton;

public class MainMenuDirector extends Director {
    private final ClickButton testPlayButton;
    private final ClickButton quitButton;

    public MainMenuDirector() {
        testPlayButton = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Play");
        testPlayButton.setPosition(-660, -400);
        testPlayButton.setCallback(this::playClicked);
        testPlayButton.setEnabled(false);

        quitButton = new ClickButton(HgGame.Assets().loadTexture("Assets/GUI/Button.png"), 460, 150, HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), "Quit");
        quitButton.setPosition(660, -400);
        quitButton.setCallback(this::quitClicked);
        quitButton.setEnabled(false);
    }

    private void start() {
        started = true;
        testPlayButton.setEnabled(true);
        quitButton.setEnabled(true);
    }

    private void playClicked() {
        HgGame.Entities().addDirector(DirectorTypes.MatchDirector);
        toBeDestroyed = true;
    }

    private void quitClicked() {
        HgGame.Entities().addDirector(DirectorTypes.QuitDirector);
        toBeDestroyed = true;
    }

    @Override
    public void destroy() {
        testPlayButton.destroy();
        quitButton.destroy();
    }

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (!started) start();

        if (HgGame.Input().isButtonTapped(Input.Buttons.LEFT)) {
            var pos = HgGame.Input().getMouse();
            testPlayButton.onLMBDown(pos.x, pos.y);
            quitButton.onLMBDown(pos.x, pos.y);
        }
    }
}
