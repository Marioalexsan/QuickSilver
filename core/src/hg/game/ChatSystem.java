package hg.game;

import hg.drawables.BasicText;
import hg.drawables.DrawLayer;
import hg.engine.InputEngine;
import hg.engine.MappedAction;
import hg.ui.BasicTextInput;
import hg.ui.UIElement;

import java.util.LinkedList;

public class ChatSystem extends UIElement {
    private static class Message {
        public int displayTimeLeft;
        public BasicText drawable;

        public Message(int displayTime, String message) {
            this.displayTimeLeft = displayTime;
            drawable = new BasicText(HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), message);
            drawable.setConstraints(BasicText.HPos.Left, BasicText.VPos.Center, 0f);
            drawable.setLayer(DrawLayer.GUIDefault);
            drawable.setCameraUse(false);
            drawable.registerToEngine();
        }
    }

    private int maxMessagesToKeep = 4;
    private int messageLifetime = 360;
    private int fadeoutStartAt = 120;
    private int messageSpacing = 36;

    private final LinkedList<Message> messages = new LinkedList<>();
    private final BasicTextInput chatInput;

    public ChatSystem() {
        chatInput = new BasicTextInput(HgGame.Assets().loadFont("Assets/Fonts/CourierNew36.fnt"), 250, 500, 36);
        chatInput.setEmptyText("--- Chat ---");
    }

    public void addMessage(String message) {
        Message msg = new Message(messageLifetime, message);
        msg.drawable.setEnabled(true);
        messages.addFirst(msg);

        if (messages.size() > maxMessagesToKeep) removeLastMessage();

        updateElements();
    }

    public void removeLastMessage() {
        messages.removeLast().drawable.unregisterFromEngine();
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
                    if (potentialMessage.startsWith("/"))
                        if (potentialMessage.length() > 1) HgGame.Manager().onChatMessageEntered(potentialMessage);
                    else addMessage(potentialMessage);
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
        chatInput.setPosition(position);

        int height = 0;
        for (var msg: messages) {
            msg.drawable.setAlpha(Math.min(1f, msg.displayTimeLeft / (float) fadeoutStartAt));
            msg.drawable.getPosition().set(position.x, position.y + (++height * messageSpacing));
        }
    }

    public void clear() {
        while (messages.size() > 0) removeLastMessage();
        chatInput.setText("");
    }
}
