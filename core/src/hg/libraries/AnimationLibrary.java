package hg.libraries;

import com.badlogic.gdx.math.Vector2;
import hg.animation.ActCriteria;
import hg.animation.ActEffect;
import hg.animation.ActInstruction;
import hg.animation.AnimationInfo;
import hg.drawables.AnimatedSprite;

import java.util.HashMap;

// TODO Make an animation builder

public class AnimationLibrary {
    private static final HashMap<String, AnimationInfo> prototypes = new HashMap<>();

    public static AnimationInfo GetAnimationInfo(String info) {
        AnimationInfo obj = prototypes.get(info);
        return obj != null ? new AnimationInfo(obj) : null;
    }

    static {
        AnimationInfo anim1 = new AnimationInfo("Assets/Sprites/Player/Rifle_Idle.png", 96, 105, 1, 1, 0, AnimatedSprite.PlayMode.Static, null);
        anim1.cenOffset.set(new Vector2(48, 31));
        anim1.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Idle", anim1);

        AnimationInfo anim2 = new AnimationInfo("Assets/Sprites/Player/Rifle_Reload.png", 98, 105, 20, 20, 6, AnimatedSprite.PlayMode.PlayOnce, new ActInstruction[] {
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "3"), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/magout.ogg")),
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "14"), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/magin.ogg")),
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "17"), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/receiverpull.ogg")),
        });
        anim2.cenOffset.set(new Vector2(48, 31));
        anim2.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Reload", anim2);

        AnimationInfo anim3 = new AnimationInfo("Assets/Sprites/Player/Dead.png", 150, 250, 8, 8, 3, AnimatedSprite.PlayMode.PlayAndFreeze, null);
        anim3.cenOffset.set(new Vector2(75, 119));
        anim3.textureAngle.set(-90f);
        prototypes.put("Player_Death", anim3);

        AnimationInfo anim4 = new AnimationInfo("Assets/Sprites/Player/Rifle_Shoot.png", 96, 106, 4, 4, 2, AnimatedSprite.PlayMode.PlayOnce, new ActInstruction[] {
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtStart), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/shot.ogg"))
        });
        anim4.cenOffset.set(new Vector2(48, 31));
        anim4.textureAngle.set(-90f);
        prototypes.put("Player_Rifle_Shoot", anim4);

        AnimationInfo anim5 = new AnimationInfo("Assets/Sprites/Player/Revolver_Idle.png", 95, 100, 1, 1, 0, AnimatedSprite.PlayMode.Static, null);
        anim5.cenOffset.set(new Vector2(48, 31));
        anim5.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Idle", anim5);

        AnimationInfo anim6 = new AnimationInfo("Assets/Sprites/Player/Revolver_Shoot.png", 95, 100, 7, 7, 4, AnimatedSprite.PlayMode.PlayOnce, new ActInstruction[] {
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtStart), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/shot.ogg"))
        });
        anim6.cenOffset.set(new Vector2(48, 31));
        anim6.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Shoot", anim6);

        AnimationInfo anim7 = new AnimationInfo("Assets/Sprites/Player/Revolver_Reload.png", 95, 100, 20, 20, 6, AnimatedSprite.PlayMode.PlayOnce, new ActInstruction[] {
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "7"), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/magin.ogg")),
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "14"), new ActEffect(ActEffect.Type.PlaySound, "Assets/Audio/magout.ogg")),
        });
        anim7.cenOffset.set(new Vector2(48, 31));
        anim7.textureAngle.set(-90f);
        prototypes.put("Player_Revolver_Reload", anim7);
    }
}
