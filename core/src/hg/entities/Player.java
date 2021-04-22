package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.animation.*;
import hg.drawables.AnimatedSprite;
import hg.engine.AssetLoader;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.libraries.ActorLibrary;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.SphereCollider;
import hg.playerlogic.EmptyAI;
import hg.interfaces.IPlayerLogic;

public class Player extends Entity {

    private IPlayerLogic playerLogic;
    private final Vector2 smoothSpeed = new Vector2();

    private int DEBUG_shootCooldown = 10;
    public int DEBUG_killCount = 0;

    protected Animation drawable = new Animation();
    protected SphereCollider collider = new SphereCollider(50);

    public Player(IPlayerLogic playerLogic) {
        AssetLoader assets = HgGame.Assets(); // Extra dependency

        setLogic(playerLogic);

        baseStats = new BaseStats(this);
        collider.group = ColliderGroup.Player;
        collider.baseStats = baseStats;
        collider.owner = this;

        AnimationInfo anim1 = new AnimationInfo("Assets/Textures/Player/Rifle_Idle.png", 96, 105, 1, 1, 0, AnimatedSprite.PlayMode.Static, null);
        anim1.cenOffset.set(new Vector2(48, 31));
        anim1.textureAngle.set(-90f);
        drawable.addKnownAnimation("Player_Idle", anim1);
        drawable.setDefaultAnimation("Player_Idle");

        AnimationInfo anim2 = new AnimationInfo("Assets/Textures/Player/Rifle_Reload.png", 98, 105, 20, 20, 6, AnimatedSprite.PlayMode.PlayOnce, new ActInstruction[] {
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "7"), new ActEffect(ActEffect.Type.PlaySound, "Assets/shotMono.ogg")),
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtFrameX, "14"), new ActEffect(ActEffect.Type.PlaySound, "Assets/shotMono.ogg")),
                new ActInstruction(new ActCriteria(ActCriteria.Type.TriggerAtEnd), new ActEffect(ActEffect.Type.PlaySound, "Assets/shotMono.ogg")),
        });
        anim2.cenOffset.set(new Vector2(48, 31));
        anim2.textureAngle.set(-90f);
        drawable.addKnownAnimation("Player_Rifle_Reload", anim2);

        collider.setPosition(position);
        collider.setAngle(angle);

        drawable.setPosition(position);
        drawable.setAngle(angle);

        collider.registerToEngine(); // Allocation!
        drawable.registerToEngine(); // Allocation!
    }

    public void setLogic(IPlayerLogic newLogic) {
        if (playerLogic != null) playerLogic.setControlledPlayer(null);
        if (newLogic == null) newLogic = new EmptyAI();
        playerLogic = newLogic;
        playerLogic.setControlledPlayer(this);
    }

    @Override
    public void clientUpdate() {
        serverUpdate(); // To be changed later, when / if multiplayer is added
    }

    @Override
    public void serverUpdate() {
        if (playerLogic == null) {
            playerLogic = new EmptyAI();
        }

        drawable.update();

        playerLogic.localUpdate();
        var actions = playerLogic.obtainActions();
        var aim = playerLogic.obtainAimPosition();

        // Resolve movement

        Vector2 moveDirection;
        Vector2 advancedMove = playerLogic.obtainAdvancedMove();

        if (advancedMove == null) {
            moveDirection = new Vector2(0, 0);
            if (actions != null) {
                if (actions.contains(MappedAction.MoveUp)) moveDirection.y += 1f;
                if (actions.contains(MappedAction.MoveDown)) moveDirection.y -= 1f;
                if (actions.contains(MappedAction.MoveLeft)) moveDirection.x -= 1f;
                if (actions.contains(MappedAction.MoveRight)) moveDirection.x += 1f;
            }
        }
        else {
            moveDirection = advancedMove;
        }

        Vector2 targetSpeed = new Vector2(moveDirection).nor().scl(baseStats.baseMoveSpeed);
        smoothSpeed.add(targetSpeed.sub(smoothSpeed).scl(0.22f));

        move(smoothSpeed);

        // Resolve aim direction

        if (aim != null) angle.set(new Vector2(aim).angleDeg());

        if (actions != null && actions.contains(MappedAction.Reload) && !drawable.getCurrentAnimation().equals("Player_Rifle_Reload")) {
            drawable.switchAnimation("Player_Rifle_Reload");
        }
        if (DEBUG_shootCooldown > 0) DEBUG_shootCooldown--;
        if (actions!= null && DEBUG_shootCooldown == 0 && actions.contains(MappedAction.SecondaryFire)) {
            var boolet = HgGame.Entities().addActor(ActorLibrary.Types.GenericBullet, position, angle.getDeg());
            boolet.getPosition().add(angle.normalVector().scl(75).add(angle.normalVector().rotate90(-1).scl(25)));
            boolet.getColliderIfAny().attackStats = new AttackStats(this, 20f, ColliderGroup.Player);
            DEBUG_shootCooldown = 10;
        }
    }

    @Override
    public void onGenericCollision(Collider col) {

    }

    @Override
    public void onAttackHit(BaseStats defender) {

    }

    @Override
    public void onKill(Entity other) {
        DEBUG_killCount++;
    }

    @Override
    public void onHitByAttack(AttackStats attacker) {
        if (baseStats.invulnerabilityFrames > 0 || attacker.baseDamage <= 0.0) return;

        baseStats.health -= attacker.baseDamage;

        if (baseStats.health <= 0.0) onDeath(attacker.owner);
        else HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/PlayerHurt.ogg"), 1f, position);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/GenericHurt.ogg"), 1f);
    }

    @Override
    public void onDeath(Entity killer) {
        if (baseStats != null && !baseStats.isDead) {
            baseStats.isDead = true;
            killer.onKill(this);
        }
    }

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
    }
}
