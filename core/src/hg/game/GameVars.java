package hg.game;

import java.util.HashMap;

// Under construction / existential crisis / etc.
public class GameVars {
    private static final HashMap<String, String> defaultSettings = new HashMap<>();
    private static final HashMap<String, String> folderPaths = new HashMap<>();

    static {
        defaultSettings.put("ResWidth", Integer.toString(1600));
        defaultSettings.put("ResHeight", Integer.toString(900));
        defaultSettings.put("Fullscreen", Boolean.toString(false));
        defaultSettings.put("MouseSens", Float.toString(1.0f));
        defaultSettings.put("FOVFactor", Float.toString(0.6f));
    }

    static {
        folderPaths.put("Assets", "Assets/");
        folderPaths.put("Audio", "Assets/Audio/");
        folderPaths.put("Fonts", "Assets/Fonts/");
        folderPaths.put("GUI", "Assets/GUI/");
        folderPaths.put("Maps", "Assets/Maps/");
        folderPaths.put("Sprites", "Assets/Sprites/");
    }

    public static HashMap<String, String> GetAllDefaultSettings() {
        return new HashMap<>(defaultSettings);
    }

    public static String GetDefaultSetting(String key) {
        String value = defaultSettings.get(key);
        return value != null ? value : "";
    }
}
