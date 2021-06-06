package hg.drawables;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import hg.game.HgGame;
import hg.utils.Angle;
import hg.utils.MathTools;

import java.util.HashMap;

/** Animation can play sprite animations, and switch between them easily. It can also execute animation instructions. */
public class Animation extends BasicSprite {
    public enum PlayMode {
        PlayOnce,
        Loop,
        Static,
        PlayAndFreeze,
    }

    protected int updatesPerFrame;
    protected int framesPerRow;
    protected int finalFrame;

    protected PlayMode playMode;

    protected float accumulatedFrames = 0f;
    protected int currentFrame = 0;

    protected boolean started = false;
    protected boolean finished = false;

    protected final HashMap<String, Data> knownAnimations = new HashMap<>();
    protected String currentAnimation = "";
    protected String defaultAnimation = "";

    protected void setAnimation(Texture texture, int width, int height, int framesPerRow, int frameCount, int updatesPerFrame) {
        frame.setTexture(texture);
        frame.setRegion(0, 0, width, height);
        this.framesPerRow = framesPerRow;
        this.updatesPerFrame = updatesPerFrame;
        this.finalFrame = frameCount - 1;
        reset();
    }

    public TextureRegion getTextureRegion() {
        return new TextureRegion(frame);
    }

    public void setFrame(int frame) {
        currentFrame = MathTools.Clamp(frame, 0, finalFrame);
    }

    public void setMode(PlayMode mode) {
        this.playMode = mode;
    }

    protected void updateTexture(int frames) {
        if (playMode == PlayMode.Static || finished) return;
        started = true;

        accumulatedFrames += frames;
        int frameAdvance = Math.max((int)(accumulatedFrames / updatesPerFrame), 0);
        accumulatedFrames -= frameAdvance * updatesPerFrame;
        currentFrame += frameAdvance;

        if (currentFrame > finalFrame) {
            if (playMode == PlayMode.Loop) {
                currentFrame -= finalFrame + 1;
            }
            else {
                currentFrame = finalFrame;
                finished = true;
            }
        }

        if (frameAdvance > 0) {
            // Update region
            int row = currentFrame / framesPerRow;
            int collumn = currentFrame % framesPerRow;
            int width = frame.getRegionWidth();
            int height = frame.getRegionHeight();
            frame.setRegion(collumn * width, row * height, width, height);
        }
    }

    public void reset() {
        currentFrame = 0;
        accumulatedFrames = 0f;
        finished = false;
        started = false;
    }

    protected void parseUpdateActions(int startFrame, int endFrame) {
        Data info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            switch (action.trigger.type) {
                case FrameX -> {
                    int frame;
                    try {
                        frame = Integer.parseInt(action.trigger.sArgs);
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
        Data info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            if (action.trigger.type == Trigger.Type.Start) {
                activateEffect(action.effect);
                action.triggered = true;
            }
        }
    }

    protected void parseEndActions() {
        Data info = knownAnimations.get(currentAnimation);
        if (info == null || info.actions == null) return;

        for (var action : info.actions) {
            if (action.triggered) continue;

            if (action.trigger.type == Trigger.Type.End) {
                activateEffect(action.effect);
                action.triggered = true;
            }
        }
    }

    protected void activateEffect(Effect effect) {
        switch (effect.type) {
            case PlaySound -> {
                float volume = effect.afArgs.length >= 1 ? effect.afArgs[0] : 1f;
                Vector2 playPos = effect.afArgs.length >= 2 ? null : position;
                Sound sound = HgGame.Assets().loadSound(effect.sArgs);

                HgGame.Audio().playSound(sound, volume, playPos);
            }
            case FrameDelay -> accumulatedFrames -= effect.afArgs[0];
        }
    }

    public void addKnownAnimation(String animation, Data info) {
        knownAnimations.put(animation, info);
    }

    public void setDefaultAnimation(String defaultAnimation) {
        this.defaultAnimation = defaultAnimation;
    }

    public void switchToDefault() {
        switchAnimation(defaultAnimation);
    }

    public void switchAnimation(String animation) {
        Data info = knownAnimations.get(animation);
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

    public void update() {
        update(1);
    }

    public void update(int frames) {
        if (currentAnimation.equals("")) switchToDefault();

        parseStartActions();

        if (!started) {
            parseStartActions();
        }

        int lastFrame = currentFrame;
        updateTexture(frames);

        if (currentFrame < lastFrame) {
            resetActions(true, false, false);
        }

        if (currentFrame > lastFrame) {
            parseUpdateActions(lastFrame + 1, currentFrame);
        }
        else if (finished && playMode != PlayMode.PlayAndFreeze) {
            String lastAnimation = currentAnimation;
            parseEndActions();
            if (lastAnimation.equals(currentAnimation)) {
                switchToDefault();
            }
            update(); // since current update check didn't advance frame as expected, due to switch
        }
    }

    public void resetActions(boolean updates, boolean start, boolean end) {
        Data info = knownAnimations.get(currentAnimation);
        if (info == null) return;

        for (var action : info.actions) {
            boolean doReset = updates && action.trigger.type != Trigger.Type.Start && action.trigger.type != Trigger.Type.End ||
                            start && action.trigger.type == Trigger.Type.Start ||
                            end && action.trigger.type == Trigger.Type.End;
            if (doReset) action.triggered = false;
        }
    }

    /** AnimationData is a structure defining an animation's information */
    public static class Data {
        public final String texture;
        public final int width;
        public final int height;
        public final int framesPerRow;
        public final int finalFrame;
        public final int updatesPerFrame;
        public final PlayMode playMode;
        public final ActInstruction[] actions;

        public final Vector2 cenOffset = new Vector2();
        public final Angle angOffset = new Angle();
        public final Angle textureAngle = new Angle();


        public Data(String texture, int width, int height, int framesPerRow, int frameCount, int updatesPerFrame, PlayMode playMode, ActInstruction[] actions) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.framesPerRow = framesPerRow;
            this.finalFrame = frameCount - 1;
            this.updatesPerFrame = updatesPerFrame;
            this.playMode = playMode;
            this.actions = actions != null ? actions.clone() : new ActInstruction[0];
        }

        public Data(Data obj) {
            this.texture = obj.texture;
            this.width = obj.width;
            this.height = obj.height;
            this.framesPerRow = obj.framesPerRow;
            this.finalFrame = obj.finalFrame - 1;
            this.updatesPerFrame = obj.updatesPerFrame;
            this.playMode = obj.playMode;
            this.actions = obj.actions != null ? obj.actions.clone() : new ActInstruction[0];

            this.cenOffset.set(obj.cenOffset);
            this.angOffset.set(obj.angOffset);
            this.textureAngle.set(obj.textureAngle);
        }
    }

    /** ActCriteria defines criteria for triggering actions during animations */
    public static class Trigger {
        public enum Type {
            Start,
            FrameX,
            End
        }

        public final Type type;
        public final String sArgs;
        public final float[] afArgs;

        public Trigger(Type type) {
            this.type = type;
            this.sArgs = "";
            this.afArgs = new float[0];
        }

        public Trigger(Type type, String sArgs) {
            this.type = type;
            this.sArgs = sArgs;
            this.afArgs = new float[0];
        }

        public Trigger(Type type, float... afArgs) {
            this.type = type;
            this.sArgs = "";
            this.afArgs = afArgs.clone();
        }

        public Trigger(Type type, String sArgs, float... afArgs) {
            this.type = type;
            this.sArgs = sArgs;
            this.afArgs = afArgs.clone();
        }
    }

    public static class Effect {
        public enum Type {
            PlaySound,
            FrameDelay
        }

        public final Type type;
        public final String sArgs;
        public final float[] afArgs;

        public Effect(Type type) {
            this.type = type;
            this.sArgs = "";
            this.afArgs = new float[0];
        }

        public Effect(Type type, String sArgs) {
            this.type = type;
            this.sArgs = sArgs;
            this.afArgs = new float[0];
        }

        public Effect(Type type, float... afArgs) {
            this.type = type;
            this.sArgs = "";
            this.afArgs = afArgs.clone();
        }

        public Effect(Type type, String sArgs, float... afArgs) {
            this.type = type;
            this.sArgs = sArgs;
            this.afArgs = afArgs.clone();
        }
    }

    public static class ActInstruction {
        public final Trigger trigger;
        public final Effect effect;
        public boolean triggered = false;

        public ActInstruction(Trigger trigger, Effect effect) {
            this.trigger = trigger;
            this.effect = effect;
        }
    }
}
