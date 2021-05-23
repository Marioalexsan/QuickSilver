package hg.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.networking.PlayerView;
import hg.ui.BasicTextInput;
import hg.ui.UIElement;
import hg.utils.DebugLevels;
import hg.enums.HPos;
import hg.utils.MathTools;
import hg.enums.VPos;

import java.util.LinkedList;

/** ChatSystem manages the in-game chat structure.
 * It is also used as a form of primitive logging for non-crashing inconsistencies. */
public class ChatSystem extends UIElement {

    private final String font = "Assets/Fonts/CourierNew24.fnt";

    private static class Message {
        public int displayTimeLeft;
        public BasicText drawable;

        public Message(BitmapFont fontToUse, String message, int displayTime) {
            this.displayTimeLeft = displayTime;
            drawable = new BasicText(fontToUse, message);
            drawable.setConstraints(HPos.Left, VPos.Center, 0f);
            drawable.setLayer(DrawLayer.GUIDefault);
            drawable.setCameraUse(false);
            drawable.registerToEngine();
        }
    }

    private final LinkedList<Message> messages = new LinkedList<>();
    private final BasicTextInput chatInput;
    private int debugMessageLevel = DebugLevels.DEFAULT;

    public ChatSystem() {
        chatInput = new BasicTextInput(HgGame.Assets().loadFont(font), 250, 500, 36);
        chatInput.setEmptyText("--- Chat ---");
    }

    public void setDebugMessageLevel(int debugLevel) {
        debugMessageLevel = MathTools.Clamp(debugLevel, DebugLevels.WORST, DebugLevels.ALL);
    }

    public void addDebugMessage(String message, int debugLevel) {
        if (debugLevel <= debugMessageLevel) {
            switch (debugLevel) {
                case DebugLevels.Error -> addMessage("[Error] " + message);
                case DebugLevels.Warn -> addMessage("[Warn] " + message);
                case DebugLevels.Info -> addMessage("[Info] " + message);
                case DebugLevels.Fatal -> addMessage("[FATAL] " + message);
                default -> addMessage("[Other] " + message);
            }
        }
    }

    public void addMessageFromView(String message, PlayerView view) {
        addMessage(view.name + ": " + message);
    }

    public void addMessage(String message) {
        int messageLifetime = 360;
        Message msg = new Message(HgGame.Assets().loadFont(font), message, messageLifetime);
        msg.drawable.setEnabled(true);
        messages.addFirst(msg);

        int maxMessagesToKeep = 14;
        if (messages.size() > maxMessagesToKeep) removeLastMessage();

        updateElements();
    }

    public void removeLastMessage() {
        messages.removeLast().drawable.unregisterFromEngine();
    }

    public void clear() {
        while (messages.size() > 0) removeLastMessage();
        chatInput.setText("");
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        for (var msg : messages) msg.drawable.setEnabled(enabled);
        chatInput.setEnabled(enabled);

    }

    @Override
    public void onUpdate() {
        if (!enabled) return;
        InputEngine inputEngine = HgGame.Input();
        GameManager manager = HgGame.Manager();

        String playerName;
        if (manager.localView != null && manager.localView.name != null) {
            playerName = manager.localView.name;
        }
        else {
            playerName = "Anonymous";
        }
        chatInput.setEmptyText("--- Chat as " + playerName + " ---");

        boolean submit = inputEngine.isActionTapped(MappedAction.ChatSubmit);

        updateElements();

        chatInput.onUpdate();

        for (var msg : messages)
            if (msg.displayTimeLeft > 0) msg.displayTimeLeft--;

        if (submit) {
            if (!chatInput.isFocused()) chatInput.enter();
            else {
                String potentialMessage = chatInput.getText().trim();

                if (!potentialMessage.equals("")) {
                    if (!potentialMessage.startsWith("/"))
                        addMessageFromView(potentialMessage, manager.localView);
                    HgGame.Manager().onChatMessageEntered(potentialMessage);
                }
                chatInput.setText("");
                chatInput.enter(false);
            }
        }
    }

    @Override
    public void destroy() {
        for (var msg : messages) msg.drawable.unregisterFromEngine();
        chatInput.destroy();
    }

    private void updateElements() {
        chatInput.getPosition().set(position);

        int height = 0;
        for (var msg: messages) {
            int fadeoutStartAt = 120;
            if (chatInput.isFocused()) msg.drawable.setAlpha(1f);
            else msg.drawable.setAlpha(Math.min(1f, msg.displayTimeLeft / (float) fadeoutStartAt));
            int messageSpacing = 24;
            msg.drawable.getPosition().set(position.x, position.y + (++height * messageSpacing));
        }
    }
}
