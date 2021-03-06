package hg.drawables;

import hg.drawables.BasicText;
import hg.drawables.Drawable;
import hg.enums.HPos;
import hg.enums.VPos;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.libraries.BuilderLibrary;
import hg.networking.PlayerView;
import hg.utils.GraphicsContext;
import hg.utils.builders.BasicTextBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** DeathmatchScoreboard displays the current round's scores */
public class DeathmatchScoreboard extends Drawable {
    protected final ArrayList<BasicText> scoreTexts = new ArrayList<>();
    protected BasicTextBuilder baseBuilder;
    protected BasicText status;

    public DeathmatchScoreboard() {
        baseBuilder = BuilderLibrary.BasicTextBuilders("smalllabel").textPos(HPos.Right, VPos.Top);
        status = baseBuilder.copy().build();
        status.unregisterFromEngine();
        status.setPCA(position, center, angle);
    }

    public void updateScores(HashMap<Integer, Integer> scores) {
        GameManager manager = HgGame.Manager();

        // Add texts for new score entries
        while (scoreTexts.size() < scores.size()) {
            BasicText text = baseBuilder.makeGUI().build();
            scoreTexts.add(text);
            text.unregisterFromEngine();
        }

        // Removes texts for removed score entries
        while (scoreTexts.size() > scores.size())
            scoreTexts.remove(scoreTexts.size() - 1).unregisterFromEngine();

        ArrayList<Map.Entry<Integer, Integer>> entries = new ArrayList<>(scores.entrySet());
        entries.sort((o1, o2) -> o2.getValue() - o1.getValue());

        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            PlayerView view = manager.getPlayerViewByUniqueID(entry.getKey());
            String name = view != null ? view.name : null;
            scoreTexts.get(i).setText((name != null ? name : "Incognito") + " : " + entry.getValue() + " Kills");
        }

        int offsetY = -30;

        for (var text: scoreTexts) {
            text.getPositionOffset().set(0, offsetY);
            offsetY -= 30;
        }
    }

    public void updateStatus(int gamemodeState, int timeLeftInFrames) {
        String postfix = "";
        switch (gamemodeState) {
            case 0 -> postfix = "Starting";
            case 1 -> {
                int seconds = (timeLeftInFrames / 60) % 60;
                int minutes = (timeLeftInFrames / 3600) % 60;
                postfix = minutes + ":" + (seconds < 10 && seconds >= 0 ? "0" : "") + seconds;
            }
            case 2 -> postfix = "Match End";
        }
        status.setText(postfix + " | Deathmatch");
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        for (var text: scoreTexts)
            text.setEnabled(enabled);
        status.setEnabled(enabled);
    }

    @Override
    public void draw(GraphicsContext env) {
        status.draw(env);
        for (var text: scoreTexts) {
            text.setPCA(position, center, angle);
            text.draw(env);
        }
    }
}
