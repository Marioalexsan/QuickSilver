package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Animation;

import java.util.HashMap;

// TODO Make an animation builder

/** Holds animations used in the game. */
public class AnimationLibrary {
    private static final HashMap<String, Animation.Data> prototypes = new HashMap<>();

    public static Animation.Data GetAnimationInfo(String info) {
        Animation.Data obj = prototypes.get(info);
        return obj != null ? new Animation.Data(obj) : null;
    }

    static {
        Animation.Data anim1 = new Animation.Data("Assets/Sprites/Player/Rifle_Idle.png", 96, 105, 1, 1, 0, Animation.PlayMode.Static, null);
        anim1.cenOffset.set(new Vector2(48, 31));
        anim1.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Idle", anim1);

        Animation.Data anim2 = new Animation.Data("Assets/Sprites/Player/Rifle_Reload.png", 98, 105, 20, 20, 6, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "3"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/magout.ogg")),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "14"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/magin.ogg")),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "17"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/receiverpull.ogg")),
        });
        anim2.cenOffset.set(new Vector2(48, 31));
        anim2.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Reload", anim2);

        Animation.Data anim3 = new Animation.Data("Assets/Sprites/Player/Dead.png", 150, 250, 8, 8, 3, Animation.PlayMode.PlayAndFreeze, null);
        anim3.cenOffset.set(new Vector2(75, 119));
        anim3.textureAngle.set(-90f);
        prototypes.put("Player_Death", anim3);

        Animation.Data anim4 = new Animation.Data("Assets/Sprites/Player/Rifle_Shoot.png", 96, 106, 4, 4, 2, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.Start), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/shot.ogg"))
        });
        anim4.cenOffset.set(new Vector2(48, 31));
        anim4.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Shoot", anim4);

        Animation.Data anim5 = new Animation.Data("Assets/Sprites/Player/Revolver_Idle.png", 95, 100, 1, 1, 0, Animation.PlayMode.Static, null);
        anim5.cenOffset.set(new Vector2(48, 31));
        anim5.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Idle", anim5);

        Animation.Data anim6 = new Animation.Data("Assets/Sprites/Player/Revolver_Shoot.png", 95, 100, 7, 7, 4, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.Start), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/shot.ogg"))
        });
        anim6.cenOffset.set(new Vector2(48, 31));
        anim6.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Shoot", anim6);

        Animation.Data anim7 = new Animation.Data("Assets/Sprites/Player/Revolver_Reload.png", 95, 100, 20, 20, 6, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "7"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/magin.ogg")),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "14"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/magout.ogg")),
        });
        anim7.cenOffset.set(new Vector2(48, 31));
        anim7.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Reload", anim7);

        Animation.Data anim8 = new Animation.Data("Assets/Sprites/Player/Shotgun_PowerShoot.png", 150, 200, 8, 8, 3, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.Start), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/shot.ogg")),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "2"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "3"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 16f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "4"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 3f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "5"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "6"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 1f)),
        });
        anim8.cenOffset.set(new Vector2(75, 68));
        anim8.textureAngle.set(-90f);
        prototypes.put("Player_Shotgun_PowerShoot", anim8);

        Animation.Data anim9 = new Animation.Data("Assets/Sprites/Player/Shotgun_Reload.png", 150, 200, 16, 16, 5, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "13"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "12"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "12"), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/magout.ogg")),
        });
        anim9.cenOffset.set(new Vector2(75, 68));
        anim9.textureAngle.set(-90f);
        prototypes.put("Player_Shotgun_Reload", anim9);

        Animation.Data anim10 = new Animation.Data("Assets/Sprites/Player/Shotgun_Idle.png", 150, 200, 1, 1, 0, Animation.PlayMode.Static, null);
        anim10.cenOffset.set(new Vector2(75, 68));
        anim10.textureAngle.set(-90f);
        prototypes.put("Player_Shotgun_Idle", anim10);

        Animation.Data anim11 = new Animation.Data("Assets/Sprites/Player/Shotgun_PowerShoot.png", 150, 200, 8, 8, 3, Animation.PlayMode.PlayOnce, new Animation.ActInstruction[] {
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.Start), new Animation.Effect(Animation.Effect.Type.PlaySound, "Assets/Audio/shot.ogg")),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "2"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "3"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 6f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "4"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 2f)),
                new Animation.ActInstruction(new Animation.Trigger(Animation.Trigger.Type.FrameX, "5"), new Animation.Effect(Animation.Effect.Type.FrameDelay, 1f)),
        });
        anim11.cenOffset.set(new Vector2(75, 68));
        anim11.textureAngle.set(-90f);
        prototypes.put("Player_Shotgun_Shoot", anim11);
    }
}
