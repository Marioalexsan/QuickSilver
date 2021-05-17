package hg.directors;

import hg.game.HgGame;
import hg.libraries.EnvironmentLibrary;
import hg.enums.types.DirectorType;

/** InitDirector inititates the game and launches MainMenuDirector, then self-destructs
 * You likely don't need this to exist after it did its job
 */
public class GameInit extends Director {
    @Override
    public void destroy() { }

    @Override
    public void update() {
        HgGame.Manager().tryAddDirector(DirectorType.MainMenu);

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

        for (var texture: texturesToPreload) HgGame.Assets().loadTexture(texture);
        for (var audio: soundsToPreload) HgGame.Assets().loadSound(audio);

        // In the future Init may linger for more time
        // or wait for a Job to complete instead
        // However, for now we just destroy it in its first update frame
        toBeDestroyed = true;
    }
}
