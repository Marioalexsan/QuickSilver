package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.HashMap;

/**
 * Offers basic load / unload functionality for various things.
 * This class requires external synchronization if used from multiple threads.
 * Music files aren't loadable. Refer to AudioEngine for how to play music.
 */
public class AssetEngine {
    private final HashMap<String, Texture> textureLibrary = new HashMap<>();
    private final HashMap<String, Sound> soundLibrary = new HashMap<>();
    private final HashMap<String, BitmapFont> fontLibrary = new HashMap<>();

    /** Loads a texture from disk. Repeated calls for the same path will return the same object. */
    public Texture loadTexture(String path) {
        Texture tex = textureLibrary.get(path);

        if (tex == null) {
            try {
                tex = new Texture(new FileHandle(path));
                tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                textureLibrary.put(path, tex);
            } catch (Exception e) {
                System.out.println("Couldn't load image at " + path);
                tex = null;
            }
        }

        return tex;
    }

    /** Unloads a texture. */
    public void unloadTexture(String sPath) {
        Texture target = textureLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Loads a sound from disk. Repeated calls for the same path will return the same object. */
    public Sound loadSound(String path) {
        Sound audio = soundLibrary.get(path);

        if (audio == null) {
            try {
                audio = Gdx.audio.newSound(new FileHandle(path));
                soundLibrary.put(path, audio);
            } catch (Exception e) {
                System.out.println("Couldn't load sound at " + path);
                audio = null;
            }
        }

        return audio;
    }

    /** Unloads a sound. */
    public void unloadSound(String sPath) {
        Sound target = soundLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Loads a font based on the data file provided. Repeated calls for the same path will return the same object.
     * Use https://www.angelcode.com/products/bmfont/ for generating fonts. */
    public BitmapFont loadFont(String path) {
        BitmapFont font = fontLibrary.get(path);

        if (font == null) {
            try {
                font = new BitmapFont(new FileHandle(path));
                font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                fontLibrary.put(path, font);
            } catch (Exception e) {
                System.out.println("Couldn't load font at " + path);
                font = null;
            }
        }

        return font;
    }

    /** Unloads a sound. Does nothing if the sound isn't loaded. */
    public void unloadFont(String sPath) {
        BitmapFont target = fontLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Unloads everything previously loaded. */
    public void unloadAll() {
        // Textures
        for (var texture : textureLibrary.entrySet()) texture.getValue().dispose();
        textureLibrary.clear();

        // Sounds
        for (var sound : soundLibrary.entrySet()) sound.getValue().dispose();
        soundLibrary.clear();

        // Fonts
        for (var font : fontLibrary.entrySet()) font.getValue().dispose();
        fontLibrary.clear();
    }
}
