package hg.drawables;

import com.badlogic.gdx.graphics.Texture;
import hg.utils.HgMath;

public class AnimatedSprite extends BasicSprite {
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

    public AnimatedSprite() {}

    public AnimatedSprite(Texture texture, int width, int height, int framesPerRow, int frameCount, int updatesPerFrame) {
        setAnimation(texture, width, height, framesPerRow, frameCount, updatesPerFrame);
    }

    public void setAnimation(Texture texture, int width, int height, int framesPerRow, int frameCount, int updatesPerFrame) {
        frame.setTexture(texture);
        frame.setRegion(0, 0, width, height);
        this.framesPerRow = framesPerRow;
        this.updatesPerFrame = updatesPerFrame;
        this.finalFrame = frameCount - 1;
        reset();
    }

    public void setFrame(int frame) {
        currentFrame = HgMath.ClampValue(frame, 0, finalFrame);
    }

    public void setMode(PlayMode mode) {
        this.playMode = mode;
    }

    public void update() { update(1); }

    public void update(int frames) {
        if (playMode == PlayMode.Static || finished) return;
        started = true;

        accumulatedFrames += frames;
        int frameAdvance = (int)(accumulatedFrames / updatesPerFrame);
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
}
