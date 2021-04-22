package hg.animation;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.AnimatedSprite;
import hg.utils.Angle;

public class AnimationInfo {
    public final int updatesPerFrame;
    public final int framesPerRow;
    public final int finalFrame;
    public final int width;
    public final int height;
    public final AnimatedSprite.PlayMode playMode;
    public final String texture;
    public final Vector2 cenOffset = new Vector2();
    public final Angle angOffset = new Angle();
    public final Angle textureAngle = new Angle();

    public ActInstruction[] actions;

    public AnimationInfo(String texture,int width, int height, int framesPerRow, int frameCount, int updatesPerFrame, AnimatedSprite.PlayMode playMode, ActInstruction[] actions) {
        this.texture = texture;
        this.updatesPerFrame = updatesPerFrame;
        this.framesPerRow = framesPerRow;
        this.width = width;
        this.height = height;
        this.finalFrame = frameCount - 1;
        this.playMode = playMode;
        this.actions = actions != null ? actions.clone() : new ActInstruction[0];
    }
}
