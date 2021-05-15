package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import hg.drawables.Drawable;
import hg.game.HgGame;
import hg.enums.HPos;
import hg.utils.HgGraphicsUtils;
import hg.utils.HgMathUtils;
import hg.enums.VPos;

import java.util.*;

public class GraphicsEngine {
    private final ArrayList<Rectangle> resolutionSelection = new ArrayList<>();

    private final SpriteBatch batch;

    private Vector2 currentResolution = new Vector2(0, 0);
    private static final OrthographicCamera camera = new OrthographicCamera(HgGame.WorldWidth, HgGame.WorldHeight);
    private static final Vector2 cameraOffset = new Vector2();

    private final HashSet<Drawable> drawableLibrary = new HashSet<>();

    public GraphicsEngine() {
        batch = new SpriteBatch();
        Gdx.graphics.setResizable(false);
        setVideoMode(1600, 900, false);

        resolutionSelection.add(new Rectangle(0, 0, 2560, 1440));
        resolutionSelection.add(new Rectangle(0, 0, 1920, 1080));
        resolutionSelection.add(new Rectangle(0, 0, 1680, 1050));
        resolutionSelection.add(new Rectangle(0, 0, 1600, 900));
        resolutionSelection.add(new Rectangle(0, 0, 1440, 900));
        resolutionSelection.add(new Rectangle(0, 0, 1366, 768));
        resolutionSelection.add(new Rectangle(0, 0, 1280, 720));
        resolutionSelection.add(new Rectangle(0, 0, 800, 600));
    }

    public void setCameraCenter(Vector2 center) {
        camera.position.set(center.x, center.y, 0);
        camera.update();
    }

    public void setCameraOffset(Vector2 offset) {
        cameraOffset.set(offset);
    }

    public void setCameraZoom(double zoom) {
        camera.zoom = (float) HgMathUtils.ClampValue(zoom, 0.25, 4.0);
        camera.update();
    }

    public Vector2 getCameraCenter() {
        return new Vector2(camera.position.x, camera.position.y);
    }

    public float getCameraZoom() {
        return camera.zoom;
    }

    public Vector2 getCameraOffset() {
        return new Vector2(cameraOffset);
    }

    public Graphics.DisplayMode[] getAllowedVideoModes() {
        return Gdx.graphics.getDisplayModes();
    }

    public boolean setVideoMode(int width, int height, boolean fullscreen) {
        if (width < 640 || height < 360) {
            return false;
        }

        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();

        boolean foundCompatible = false;
        Graphics.DisplayMode bestMatch = null;
        for (var mode : displayModes) {
            if ( mode.width <= width && mode.height <= height && (bestMatch == null || mode.width >= bestMatch.width && mode.height >= bestMatch.height)) {
                bestMatch = mode;
                if (bestMatch.width == width && bestMatch.height == height) {
                    foundCompatible = true;
                    break;
                }
            }
        }
        if (bestMatch == null) {
            return false;
        }

        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(bestMatch);
        }
        else {
            Gdx.graphics.setWindowedMode(width, height);
        }

        currentResolution = new Vector2(width, height);
        return foundCompatible;
    }

    public ArrayList<Rectangle> getSupportedResolutions() {
        Graphics.DisplayMode[] allModes = Gdx.graphics.getDisplayModes();
        ArrayList<Rectangle> compatibleResolutions = new ArrayList<>();

        Graphics.DisplayMode bestMatch = null;
        for (var resolution : resolutionSelection) {
            for (var mode : allModes) {
                if (mode.width == resolution.width && mode.height == resolution.height) {
                    compatibleResolutions.add(new Rectangle(resolution));
                    break;
                }
            }
        }
        return compatibleResolutions;
    }

    public Vector2 getCurrentResolution() {
        return new Vector2(currentResolution);
    }

    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    public void registerDrawable(Drawable object) {
        drawableLibrary.add(object);
    }

    public void unregisterDrawable(Drawable object) {
        drawableLibrary.remove(object);
    }

    public void render() {
        // Prepare drawing field
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        batch.setProjectionMatrix(camera.combined);

        // Prepare sorted drawables
        ArrayList<Drawable> heightSortedDrawables = new ArrayList<>(drawableLibrary);
        heightSortedDrawables.sort(HgGraphicsUtils.DrawableLayerComparator);

        // Draw everything that is enabled
        batch.begin();
        for (var drawable : heightSortedDrawables) {
            if (drawable.isActive()) drawable.draw(batch);
        }
        batch.end();
    }

    public void cleanup() {
        batch.dispose();
    }

    // Following static helper functions are used by Drawables for reducing implementation junk

    /** Renders the given texture region with the given parameters to the sprite batch */
    public static void RenderTextureRegion(SpriteBatch batch, Affine2 transform, Color color, boolean useCamera, TextureRegion textureRegion, boolean mirror, boolean flip) {
        batch.setColor(color);
        Affine2 targetTransform = new Affine2(transform);
        if (!useCamera) {
            // Move the texture into camera's view so that it's invariant to its positioning
            targetTransform.preScale(camera.zoom, camera.zoom).preTranslate(
                    camera.position.x,
                    camera.position.y
            );
        }
        else {
            // Apply FOV translation
            targetTransform.preTranslate(new Vector2(cameraOffset).scl(-1f));
        }

        if (flip || mirror) {
            targetTransform.translate(mirror ? textureRegion.getRegionWidth() : 0, flip ? textureRegion.getRegionHeight() : 0);
            targetTransform.scale(mirror ? -1f : 1f, flip ? -1f : 1f);
        }

        batch.draw(textureRegion, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), targetTransform);
    }

    // Functionality resembles RenderCopyEx from SDL
    // Transforms the output if the drawable doesn't use the camera
    public static void RenderText(SpriteBatch batch, Affine2 transform, Color color, boolean useCamera, BitmapFont font, String text, HPos hpos, VPos vpos, float wrap) {
        //font.setColor(color);
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
            targetTransform.preScale(camera.zoom, camera.zoom).preTranslate(
                    camera.position.x,
                    camera.position.y
            );
        }
        else {
            // Apply FOV translation
            targetTransform.preTranslate(new Vector2(cameraOffset).scl(-1f));
        }

        Matrix4 old = new Matrix4(batch.getTransformMatrix());
        batch.setTransformMatrix(new Matrix4().set(targetTransform));

        font.draw(batch, layout, 0, 0);

        batch.setTransformMatrix(old);
    }

}
