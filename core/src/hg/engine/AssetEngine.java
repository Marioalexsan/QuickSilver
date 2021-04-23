package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.HashMap;

/**
 * Offers basic load / unload functionality. You should probably not use this from a dedicated load thread.
 * Loading is supported for textures, sounds files, and fonts.
 * Music files aren't loadable by AssetEngine. Refer to AudioEngine for how to play music.
 */
public class AssetEngine {
    // Libraries
    private final HashMap<String, Texture> textureLibrary = new HashMap<>();
    private final HashMap<String, Sound> soundLibrary = new HashMap<>();
    private final HashMap<String, BitmapFont> fontLibrary = new HashMap<>();

    /** Loads a texture from disk. Repeated calls for the same path will return the same object. */
    public Texture loadTexture(String sPath) {
        if (textureLibrary.containsKey(sPath))
            return textureLibrary.get(sPath);

        try {
            Texture xTex = new Texture(new FileHandle(sPath));
            xTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureLibrary.put(sPath, xTex);
            return xTex;
        } catch (Exception e) {
            System.out.println("Couldn't load image at " + sPath);
            return null;
        }
    }

    /** Unloads a texture. */
    public void unloadTexture(String sPath) {
        Texture target = textureLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Loads a sound from disk. Repeated calls for the same path will return the same object. */
    public Sound loadSound(String sPath) {
        if (soundLibrary.containsKey(sPath))
            return soundLibrary.get(sPath);

        try {
            Sound xSound = Gdx.audio.newSound(new FileHandle(sPath));
            soundLibrary.put(sPath, xSound);
            return xSound;
        } catch (Exception e) {
            System.out.println("Couldn't load sound at " + sPath);
            return null;
        }
    }

    /** Unloads a sound. */
    public void unloadSound(String sPath) {
        Sound target = soundLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Loads a font based on the data file provided. Repeated calls for the same path will return the same object.
     * Use https://www.angelcode.com/products/bmfont/ for generating fonts. */
    public BitmapFont loadFont(String sPath) {
        if (fontLibrary.containsKey(sPath)) return fontLibrary.get(sPath);

        try {
            BitmapFont xSound = new BitmapFont(new FileHandle(sPath));
            fontLibrary.put(sPath, xSound);
            return xSound;
        } catch (Exception e) {
            System.out.println("Couldn't load font at " + sPath);
            return null;
        }
    }

    /** Unloads a sound. Does nothing if the sound isn't loaded. */
    public void unloadFont(String sPath) {
        BitmapFont target = fontLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Unloads everything previously loaded. */
    public void unloadAll() {
        // Textures
        for (var texture : textureLibrary.entrySet()) {
            texture.getValue().dispose();
        }
        textureLibrary.clear();

        // Sounds
        for (var sound : soundLibrary.entrySet()) {
            sound.getValue().dispose();
        }
        soundLibrary.clear();

        // Fonts
        for (var font : fontLibrary.entrySet()) {
            font.getValue().dispose();
        }
        fontLibrary.clear();
    }
}
