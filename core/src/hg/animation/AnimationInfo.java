package hg.animation;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.AnimatedSprite;
import hg.utils.Angle;

/** AnimationInfo is a structure defining an animation's information */
public class AnimationInfo {
    public final String texture;
    public final int width;
    public final int height;
    public final int framesPerRow;
    public final int finalFrame;
    public final int updatesPerFrame;
    public final AnimatedSprite.PlayMode playMode;
    public final ActInstruction[] actions;

    public final Vector2 cenOffset = new Vector2();
    public final Angle angOffset = new Angle();
    public final Angle textureAngle = new Angle();


    public AnimationInfo(String texture,int width, int height, int framesPerRow, int frameCount, int updatesPerFrame, AnimatedSprite.PlayMode playMode, ActInstruction[] actions) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.framesPerRow = framesPerRow;
        this.finalFrame = frameCount - 1;
        this.updatesPerFrame = updatesPerFrame;
        this.playMode = playMode;
        this.actions = actions != null ? actions.clone() : new ActInstruction[0];
    }

    public AnimationInfo(AnimationInfo obj) {
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
