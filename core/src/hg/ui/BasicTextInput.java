package hg.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.BasicText;
import hg.drawables.ColliderDrawable;
import hg.engine.InputEngine;
import hg.game.HgGame;
import hg.physics.BoxCollider;
import hg.physics.CollisionAlgorithms;

public class BasicTextInput extends UIElement {
    private final int BlinkInFrames = 30;

    protected BasicText textDisplay;
    protected BoxCollider activationZone;

    protected final StringBuffer trueText = new StringBuffer();
    protected boolean blinker = false;
    protected int nextBlink = BlinkInFrames;

    protected int delHeldFor = 0;
    protected int fastDelWaitTime = 40;
    protected int fastDelDelay = 7;

    protected boolean hasFocus = false;
    protected int maxLength = 12345678;

    protected String emptyText = "Enter something...";

    public BasicTextInput(BitmapFont font, int maxLength, int activationWidth, int activationHeight) {
        this.maxLength = maxLength;

        textDisplay = new BasicText(font, "");
        textDisplay.setConstraints(BasicText.HPos.Left, BasicText.VPos.Center, 0f);
        textDisplay.setCameraUse(false);
        textDisplay.registerToEngine();

        activationZone = new BoxCollider(activationWidth, activationHeight);

        textDisplay.setPCA(position, center, angle);
        activationZone.setPCA(position, center, angle);
        activationZone.setCenterOffset(new Vector2(-activationWidth / 2f, 0));
    }

    public String getText() {
        return trueText.toString();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        activationZone.setEnabled(enabled);
        textDisplay.setEnabled(enabled);
    }

    public void setEmptyText(String text) {
        emptyText = text;
    }

    public void setMaxLength(int length) {
        maxLength = length;
    }

    @Override
    public void onLMBDown(float x, float y) {
        if (enabled) {
            boolean gotHit = CollisionAlgorithms.PointHit(new Vector2(x, y), activationZone);

            if (hasFocus != gotHit) {
                if (gotHit) {
                    HgGame.Input().addFocusInput(this, InputEngine.FocusPriorities.TextField);
                    HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/gunclick.ogg"), 1f);
                }
                else HgGame.Input().removeFocusInput(this);

                textDisplay.setText(trueText.toString());
            }
            hasFocus = gotHit;
        }
    }

    @Override
    public void onUpdate() {
        if (hasFocus) {
            String typed = HgGame.Input().getTextTyped();

            // Remove bad characters that clog up input
            typed = typed.replaceAll("\b", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "");

            trueText.append(typed);

            if (trueText.length() > maxLength) {
                trueText.delete(maxLength, trueText.length());
            }

            if (--nextBlink <= 0) {
                nextBlink = BlinkInFrames;
                blinker = !blinker;
            }

            boolean deleting = HgGame.Input().isKeyHeld(Input.Keys.BACKSPACE);

            if (deleting) {
                if (delHeldFor == 0 || delHeldFor > fastDelWaitTime) {
                    int len = trueText.length();
                    if (len > 0) trueText.delete(len - 1, len);
                }
                if (delHeldFor > 60) delHeldFor -= fastDelDelay;
                delHeldFor++;
            }
            else {
                delHeldFor = 0;
            }
        }

        if (hasFocus) {
            textDisplay.setText(trueText + (blinker ? "|" : ""));
        }
        else if (trueText.length() == 0) {
            textDisplay.setText(emptyText);
        }
    }

    @Override
    public void destroy() {
        if (hasFocus) HgGame.Input().removeFocusInput(this);
        textDisplay.unregisterFromEngine();
    }
}
