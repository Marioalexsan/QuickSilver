package hg.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import hg.drawables.Drawable;
import hg.enums.HPos;
import hg.enums.VPos;

import java.util.Comparator;

/** Helper class for graphics methods */
public class GFXTools {

    /** Comparator for sorting drawables by height */
    public static final Comparator<Drawable> DrawableLayerComparator = Comparator.comparingInt(Drawable::getLayer);

    /** Calculates an Affine2 transform from the given position, center, angle, and their respective offsets. */
    public static Affine2 AffineFromPCAO(Vector2 position, Vector2 center, Angle angle,
                                         Vector2 pOffset, Vector2 cOffset, Angle aOffset) {
        return new Affine2()
                .translate(new Vector2(position).add(pOffset))
                .rotate(angle.getDeg() + aOffset.getDeg())
                .translate(new Vector2(center).add(cOffset).scl(-1));
    }

    /** Calculates an Affine2 transform from the given position, center, angle, their respective offsets, and scale. */
    public static Affine2 AffineFromPCAOScaled(Vector2 position, Vector2 center, Angle angle,
                                         Vector2 pOffset, Vector2 cOffset, Angle aOffset, Vector2 scale) {
        return new Affine2()
                .translate(new Vector2(position).add(pOffset))
                .rotate(angle.getDeg() + aOffset.getDeg())
                .scale(scale)
                .translate(new Vector2(center).add(cOffset).scl(-1));
    }

    /** Renders the given texture region with the given parameters to the sprite batch */
    public static void RenderTextureRegion(GraphicsContext env, Affine2 transform, Color color, boolean useCamera, TextureRegion textureRegion, boolean mirror, boolean flip) {
        env.batch.setColor(color);
        Affine2 targetTransform = new Affine2(transform);
        if (!useCamera) {
            // Move the texture into camera's view so that it's invariant to its positioning
            targetTransform.preScale(env.camera.zoom, env.camera.zoom).preTranslate(
                    env.camera.position.x,
                    env.camera.position.y
            );
        }
        else {
            // Apply FOV translation
            targetTransform.preTranslate(new Vector2(env.cameraOffset).scl(-1f));
        }

        if (flip || mirror) {
            targetTransform.translate(mirror ? textureRegion.getRegionWidth() : 0, flip ? textureRegion.getRegionHeight() : 0);
            targetTransform.scale(mirror ? -1f : 1f, flip ? -1f : 1f);
        }

        env.batch.draw(textureRegion, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), targetTransform);
    }

    // Functionality resembles RenderCopyEx from SDL
    // Transforms the output if the drawable doesn't use the camera
    public static void RenderText(GraphicsContext env, Affine2 transform, Color color, boolean useCamera, BitmapFont font, String text, HPos hpos, VPos vpos, float wrap) {
        Affine2 targetTransform = new Affine2(transform);

        float halign = 0f;
        switch (hpos) {
            case Center -> halign = -0.5f;
            case Right -> halign = -1f;
        }

        float valign = 0f;
        switch (vpos) {
            case Center -> valign = 0.5f;
            case Bottom -> valign = 1f;
        }

        GlyphLayout layout = new GlyphLayout(font, text, color, wrap, Align.left, wrap > 0f);
        targetTransform.translate(new Vector2(layout.width * halign, layout.height * valign));

        if (!useCamera) {
            // Move the texture into camera's view so that it's invariant to its positioning
            targetTransform.preScale(env.camera.zoom, env.camera.zoom).preTranslate(
                    env.camera.position.x,
                    env.camera.position.y
            );
        }
        else {
            // Apply FOV translation
            targetTransform.preTranslate(new Vector2(env.cameraOffset).scl(-1f));
        }

        Matrix4 old = new Matrix4(env.batch.getTransformMatrix());
        env.batch.setTransformMatrix(new Matrix4().set(targetTransform));

        font.draw(env.batch, layout, 0, 0);

        env.batch.setTransformMatrix(old);
    }

}
