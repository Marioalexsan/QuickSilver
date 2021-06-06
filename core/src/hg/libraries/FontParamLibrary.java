package hg.libraries;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontParamLibrary {
    private static final String LatinSet;
    private static final String Numeric;

    static {
        StringBuilder builder;

        // Latin
        builder = new StringBuilder();
        for (char c = 32; c <= 176; c++)
            builder.append(c);
        LatinSet = builder.toString();

        // Numeric
        builder = new StringBuilder();
        for (char c = '0'; c <= '9'; c++)
            builder.append(c);
        Numeric = builder.toString();
    }


    public static FreeTypeFontGenerator.FreeTypeFontParameter GetParams(String which) {
        var params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.magFilter = Texture.TextureFilter.Linear;
        params.minFilter = Texture.TextureFilter.Linear;
        params.characters = LatinSet;

        params.color = Color.WHITE;
        params.borderColor = Color.BLACK;
        params.borderWidth = 2;

        switch (which) {
            case "Outline24" -> {
                params.size = 24;
            }
            case "Outline36" -> {
                params.size = 36;
            }
            case "Outline48" -> {
                params.size = 48;
            }
            case "Outline72" -> {
                params.size = 72;
            }
            case "Outline144" -> {
                params.size = 144;
            }
            case "BarValueDefault" -> {
                //params.characters = Numeric;
                params.size = 32;
                params.borderColor.set(0f, 0f, 0f, 0.8f);
            }
        }
        return params;
    }
}
