package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import hg.drawables.Drawable;
import hg.game.HgGame;
import hg.utils.GFXTools;
import hg.utils.GraphicsContext;
import hg.utils.MathTools;

import java.util.*;

/** GraphicsEngine renders Drawables to the screen each game update cycle, using a global graphics context (camera, etc.) */
public class GraphicsEngine {
    private final ArrayList<Rectangle> resolutionSelection = new ArrayList<>();

    private final SpriteBatch batch;

    private Vector2 currentResolution = new Vector2(0, 0);
    private final OrthographicCamera camera = new OrthographicCamera(HgGame.WorldWidth, HgGame.WorldHeight);
    private final Vector2 cameraOffset = new Vector2();

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
        camera.zoom = (float) MathTools.Clamp(zoom, 0.25, 4.0);
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
        heightSortedDrawables.sort(GFXTools.DrawableLayerComparator);

        // Draw everything that is enabled
        batch.begin();
        GraphicsContext context = new GraphicsContext(batch, camera, cameraOffset);
        for (var drawable : heightSortedDrawables) {
            if (drawable.isActive()) drawable.draw(context);
        }
        batch.end();
    }

    public void cleanup() {
        batch.dispose();
    }
}
