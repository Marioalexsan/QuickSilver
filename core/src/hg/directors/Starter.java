package hg.directors;

import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.libraries.EnvironmentLibrary;
import hg.enums.DirectorType;
import hg.libraries.FontParamLibrary;

import java.util.HashMap;

/** InitDirector inititates the game and launches MainMenuDirector, then self-destructs
 * You likely don't need this to exist after it did its job */
public class Starter extends Director {
    @Override
    public void destroy() { }

    @Override
    public void update() {
        AssetEngine assets = HgGame.Assets();

        // TODO Redo preloading
        String[] texturesToPreload = {
                "Assets/Sprites/Player/Rifle_Idle.png",
                "Assets/Sprites/Player/Rifle_Reload.png",
                "Assets/Sprites/Player/Rifle_Shoot.png",
                "Assets/Sprites/Player/Dead.png",
                "Assets/Sprites/Player/Revolver_Idle.png",
                "Assets/Sprites/Player/Revolver_Shoot.png",
                "Assets/Sprites/Player/Revolver_Reload.png",
                EnvironmentLibrary.envPath + "BrickDefault.png",
                EnvironmentLibrary.envPath + "BrickHalf.png",
                EnvironmentLibrary.envPath + "BrickSlashed.png",
                EnvironmentLibrary.envPath + "BrickSlashed.png",
                EnvironmentLibrary.envPath + "BrickLShape.png",
                EnvironmentLibrary.envPath + "BrickTriangle.png",
                EnvironmentLibrary.envPath  + "BrickPillarSmall.png",
                EnvironmentLibrary.envPath + "BrickPillarMedium.png",
                EnvironmentLibrary.envPath + "BrickPillarBig.png",
                EnvironmentLibrary.envPath + "ConcreteFloor.png",
                EnvironmentLibrary.envPath + "BoxMedium.png",
                EnvironmentLibrary.envPath + "BoxSmall.png",
                EnvironmentLibrary.envPath + "MetalBars.png",
        };

        String[] soundsToPreload = {
                "Assets/Audio/gunclick.ogg",
                "Assets/Audio/magin.ogg",
                "Assets/Audio/magout.ogg",
                "Assets/Audio/receiverpull.ogg",
        };

        for (var texture: texturesToPreload) assets.loadTexture(texture);
        for (var audio: soundsToPreload) assets.loadSound(audio);

        // Preload TTFs
        assets.registerTTF("Assets/Fonts/CourierNew.ttf", "CourierNew");

        // Pregenerate fonts
        assets.generateFont("Text24", "CourierNew", FontParamLibrary.GetParams("Outline24"));
        assets.generateFont("Text36", "CourierNew", FontParamLibrary.GetParams("Outline36"));
        assets.generateFont("Text48", "CourierNew", FontParamLibrary.GetParams("Outline48"));
        assets.generateFont("Text72", "CourierNew", FontParamLibrary.GetParams("Outline72"));
        assets.generateFont("Text144", "CourierNew", FontParamLibrary.GetParams("Outline144"));
        assets.generateFont("BarValueDefault", "CourierNew", FontParamLibrary.GetParams("BarValueDefault"));

        // Tell HUDManager to wake the fuck up, assets are served
        HgGame.GUI().initialize();

        // In the future Init may linger for more time
        // or wait for a Job to complete instead
        // However, for now we just destroy it in its first update frame
        HgGame.Manager().tryAddDirector(DirectorType.MainMenu);
        toBeDestroyed = true;
    }
}
