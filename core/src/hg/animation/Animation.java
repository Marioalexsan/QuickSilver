package hg.animation;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.AnimatedSprite;
import hg.game.HgGame;

import java.util.HashMap;

public class Animation extends AnimatedSprite {
    protected final HashMap<String, AnimationInfo> knownAnimations = new HashMap<>();
    protected String currentAnimation = "";
    protected String defaultAnimation = "";

    protected void parseUpdateActions(int startFrame, int endFrame) {
        AnimationInfo info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            switch (action.criteria.type) {
                case TriggerAtFrameX -> {
                    int frame;
                    try {
                        frame = Integer.parseInt(action.criteria.sArgs);
                    } catch (Exception e) {
                        action.triggered = true; // Can't process this one
                        continue;
                    }
                    if (startFrame <= frame && frame <= endFrame) {
                        activateEffect(action.effect);
                        action.triggered = true;
                    }
                }
            }
        }
    }

    protected void parseStartActions() {
        AnimationInfo info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            if (action.criteria.type == ActCriteria.Type.TriggerAtStart) {
                activateEffect(action.effect);
                action.triggered = true;
            }
        }
    }

    protected void parseEndActions() {
        AnimationInfo info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            if (action.criteria.type == ActCriteria.Type.TriggerAtEnd) {
                activateEffect(action.effect);
                action.triggered = true;
            }
        }
    }

    protected void activateEffect(ActEffect effect) {
        switch (effect.type) {
            case PlaySound -> {
                float volume = effect.afArgs.length >= 1 ? effect.afArgs[0] : 1f;
                Vector2 playPos = effect.afArgs.length >= 2 ? null : position;
                var sound = HgGame.Assets().loadSound(effect.sArgs);

                HgGame.Audio().playSound(sound, volume, playPos);
            }
        }
    }

    public Animation() {}

    public void addKnownAnimation(String animation, AnimationInfo info) {
        knownAnimations.put(animation, info);
    }

    public void setDefaultAnimation(String defaultAnimation) {
        this.defaultAnimation = defaultAnimation;
    }

    public void switchToDefault() {
        switchAnimation(defaultAnimation);
    }

    public void switchAnimation(String animation) {
        AnimationInfo info = knownAnimations.get(animation);
        if (info == null) return;

        currentAnimation = animation;
        playMode = info.playMode;

        cenOffset.set(info.cenOffset);
        angOffset.set(info.angOffset);
        textureAngle.set(info.textureAngle);

        setAnimation(HgGame.Assets().loadTexture(info.texture), info.width, info.height, info.framesPerRow, info.finalFrame + 1, info.updatesPerFrame);
        resetActions(true, true, true);
    }

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    @Override
    public void update() {
        update(1);
    }

    @Override
    public void update(int frames) {
        if (currentAnimation.equals("")) switchToDefault();

        parseStartActions();

        if (!started) {
            parseStartActions();
        }

        int lastFrame = currentFrame;
        super.update(frames);

        if (currentFrame < lastFrame) {
            resetActions(true, false, false);
        }

        if (currentFrame > lastFrame) {
            parseUpdateActions(lastFrame + 1, currentFrame);
        }
        else if (finished) {
            String lastAnimation = currentAnimation;
            parseEndActions();
            if (lastAnimation.equals(currentAnimation)) {
                switchToDefault();
            }
            update(); // since current update check didn't advance frame as expected, due to switch
        }
    }

    public void resetActions(boolean includingUpdates, boolean includingStart, boolean includingEnd) {
        AnimationInfo info = knownAnimations.get(currentAnimation);
        if (info == null) return;

        for (var action : info.actions) {
            if (includingUpdates && action.criteria.type != ActCriteria.Type.TriggerAtStart && action.criteria.type != ActCriteria.Type.TriggerAtEnd) action.triggered = false;
            if (includingStart && action.criteria.type == ActCriteria.Type.TriggerAtStart) action.triggered = false;
            if (includingEnd && action.criteria.type == ActCriteria.Type.TriggerAtEnd) action.triggered = false;
        }
    }
}
