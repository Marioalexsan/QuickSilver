package hg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;

/** AssetEngine offers basic load / unload functionality for various things.
 * This class requires external synchronization if used from multiple threads.
 * Music files aren't loadable. Refer to AudioEngine for how to play music. */
public class AssetEngine {
    // Libraries

    private final HashMap<String, Texture> textureLibrary = new HashMap<>();
    private final HashMap<String, Sound> soundLibrary = new HashMap<>();
    private final HashMap<String, BitmapFont> fontLibrary = new HashMap<>();

    private final HashMap<String, FreeTypeFontGenerator> ttfLibrary = new HashMap<>();
    private final HashMap<String, BitmapFont> generatedFontLibrary = new HashMap<>();

    // Methods

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

    /** Loads a bitmap font based on the data file provided. Repeated calls for the same path will return the same object.
     * Use https://www.angelcode.com/products/bmfont/ for generating fonts. */
    public BitmapFont loadBitmapFont(String path) {
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

    /** Unloads a bitmap font. Does nothing if the font isn't loaded. */
    public void unloadBitmapFont(String sPath) {
        BitmapFont target = fontLibrary.remove(sPath);
        if (target != null) target.dispose();
    }

    /** Registers a TTF Font. Does nothing if the font isn't loaded, or an existing font with that ID is registered.
     * You can use this to generate BitmapFonts through another method call! */
    public void registerTTF(String path, String ID) {
        if (ttfLibrary.get(ID) != null) return;

        FreeTypeFontGenerator ttf = null;
        try {
            ttf = new FreeTypeFontGenerator(new FileHandle(path));
            ttfLibrary.put(ID, ttf);
        } catch (Exception e) {
            System.out.println("Couldn't load TrueType font with ID " + ID + " at " + path);
            if (ttf != null) ttf.dispose();
        }
    }

    /** Unregisters the TTF font with that ID. Does nothing if such a font isn't registered. */
    public void unregisterTTF(String ID) {
        FreeTypeFontGenerator ttf = ttfLibrary.remove(ID);
        if (ttf != null) ttf.dispose();
    }

    /** Unloads all TTFs. */
    public void nukeTTFs() {
        for (var font : ttfLibrary.entrySet()) font.getValue().dispose();
        ttfLibrary.clear();
    }

    /** Generates a new BitmapFont using the TTF with given ID, or returns an existing one.
     * Fails if the TTF is invalid.
     * I suggest you use a factory for the TTF properties */
    public BitmapFont generateFont(String bmpID, String ttfID, FreeTypeFontGenerator.FreeTypeFontParameter properties) {
        BitmapFont font = generatedFontLibrary.get(bmpID);
        if (font != null) return font;

        FreeTypeFontGenerator ttf = ttfLibrary.get(ttfID);
        if (ttf == null) return null;

        try {
            font = ttf.generateFont(properties);
            generatedFontLibrary.put(bmpID, font);
        }
        catch (Exception e) {
            if (font != null) font.dispose();
            font = null;
        }
        return font;
    }

    /** Returns a previously generated BitmapFont with the given ID, or null if none exist. */
    public BitmapFont getFont(String bmpID) {
        return generatedFontLibrary.get(bmpID);
    }

    /** Disposes of a font previously generated via TTF. */
    public void destroyFont(String bmpID) {
        BitmapFont font = generatedFontLibrary.remove(bmpID);
        if (font != null) font.dispose();
    }

    /** Unloads everything previously loaded. */
    public void unloadAll() {
        // Textures
        for (var texture : textureLibrary.entrySet()) texture.getValue().dispose();
        textureLibrary.clear();

        // Sounds
        for (var sound : soundLibrary.entrySet()) sound.getValue().dispose();
        soundLibrary.clear();

        // Loaded Bitmap Fonts
        for (var font : fontLibrary.entrySet()) font.getValue().dispose();
        fontLibrary.clear();

        // Loaded TTFs
        nukeTTFs();

        // Generated Bitmap Fonts
        for (var font : generatedFontLibrary.entrySet()) font.getValue().dispose();
        generatedFontLibrary.clear();
    }
}
