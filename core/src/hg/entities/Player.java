package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.animation.*;
import hg.drawables.AnimatedSprite;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.engine.AssetEngine;
import hg.engine.MappedAction;
import hg.game.HgGame;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.interfaces.IWeapon;
import hg.libraries.ActorLibrary;
import hg.libraries.AnimationLibrary;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.SphereCollider;
import hg.playerlogic.EmptyAI;
import hg.interfaces.IPlayerLogic;
import hg.utils.HgMath;
import hg.weapons.AssaultRifle;
import hg.weapons.Revolver;

public class Player extends Entity {

    private IPlayerLogic playerLogic;
    private final Vector2 smoothSpeed = new Vector2();

    public int DEBUG_killCount = 0;

    protected Animation drawable = new Animation();
    protected SphereCollider collider = new SphereCollider(50);

    protected int deathCounter = 0;

    protected AssaultRifle rifle = new AssaultRifle(this);
    protected Revolver revolver = new Revolver(this);

    protected IWeapon currentWeapon = revolver;

    public Player(IPlayerLogic playerLogic) {
        AssetEngine assets = HgGame.Assets(); // Extra dependency

        setLogic(playerLogic);

        baseStats = new BaseStats(this);
        collider.group = ColliderGroup.Player;
        collider.baseStats = baseStats;
        collider.owner = this;


        drawable.addKnownAnimation("Death", AnimationLibrary.GetAnimationInfo("Player_Death"));

        drawable.addKnownAnimation("Rifle_Idle", AnimationLibrary.GetAnimationInfo("Player_Rifle_Idle"));
        drawable.addKnownAnimation("Rifle_Reload", AnimationLibrary.GetAnimationInfo("Player_Rifle_Reload"));
        drawable.addKnownAnimation("Rifle_Shoot", AnimationLibrary.GetAnimationInfo("Player_Rifle_Shoot"));
        drawable.setDefaultAnimation("Revolver_Idle");

        drawable.addKnownAnimation("Revolver_Idle", AnimationLibrary.GetAnimationInfo("Player_Revolver_Idle"));
        drawable.addKnownAnimation("Revolver_Reload", AnimationLibrary.GetAnimationInfo("Player_Revolver_Reload"));
        drawable.addKnownAnimation("Revolver_Shoot", AnimationLibrary.GetAnimationInfo("Player_Revolver_Shoot"));

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

        if (baseStats.isDead) {
            deathCounter++;
            if (deathCounter >= 180) {
                revive();
                deathCounter = 0;
            }
        }

        drawable.update();

        playerLogic.localUpdate();
        var actions = playerLogic.obtainActions();
        var aim = playerLogic.obtainAimPosition();

        // Resolve movement

        Vector2 moveDirection = new Vector2(0, 0);
        Vector2 advancedMove = playerLogic.obtainAdvancedMove();

        if (!baseStats.isDead) {
            if (advancedMove != null) {
                moveDirection = advancedMove;
            }
            else if (actions != null) {
                if (actions.contains(MappedAction.MoveUp)) moveDirection.y += 1f;
                if (actions.contains(MappedAction.MoveDown)) moveDirection.y -= 1f;
                if (actions.contains(MappedAction.MoveLeft)) moveDirection.x -= 1f;
                if (actions.contains(MappedAction.MoveRight)) moveDirection.x += 1f;
            }
        }

        float decayFactor = baseStats.isDead ? 0.07f : 0.22f;
        float totalMoveSpeed = baseStats.baseMoveSpeed * (baseStats.heavyArmor > 0 ? 0.83f : 1f);

        Vector2 targetSpeed = new Vector2(moveDirection).nor().scl(totalMoveSpeed);
        smoothSpeed.add(targetSpeed.sub(smoothSpeed).scl(decayFactor));
        move(smoothSpeed);

        // Resolve aim direction

        if (!baseStats.isDead) {
            if (aim != null)
                angle.set(new Vector2(aim).angleDeg());

            if (currentWeapon != null) {
                if (actions != null) {
                    if (actions.contains(MappedAction.QuickSwitchWeapon)) {
                        currentWeapon.onUnequip();
                        if (currentWeapon == revolver) currentWeapon = rifle;
                        else currentWeapon = revolver;
                        currentWeapon.onEquip();
                    }
                    if (actions.contains(MappedAction.Reload)) currentWeapon.onReload();
                    if (actions.contains(MappedAction.PrimaryFire)) currentWeapon.onPrimaryFire();
                }

                currentWeapon.onUpdate();
            }
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

        float damage = attacker.baseDamage;

        // Apply armor plates
        if (baseStats.armorPlates > 0) {
            baseStats.armorPlates--;
            damage = Math.max(0f, damage - 5f);
        }

        // Apply kevlar vest and heavy armor
        float reduction = 0f;

        if (baseStats.hasKevlarVest) reduction += 0.2f;
        if (baseStats.heavyArmor > 0) {
            reduction += 0.5f;
            baseStats.heavyArmor = HgMath.ClampValue(baseStats.heavyArmor - damage * 0.5f, 0f, baseStats.maxHeavyArmor);
        }
        damage *= HgMath.ClampValue(1f - reduction, 0f, 1f);

        baseStats.health = HgMath.ClampValue(baseStats.health - damage, 0f, baseStats.maxHealth);

        if (baseStats.health <= 0.0) onDeath(attacker.owner);
        else HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/PlayerHurt.ogg"), 1f, position);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/GenericHurt.ogg"), 1f);
    }

    @Override
    public void onDeath(Entity killer) {
        if (baseStats != null && !baseStats.isDead) {
            baseStats.isDead = true;
            killer.onKill(this);
            drawable.setLayer(DrawLayer.FloorAir);
            drawable.switchAnimation("Death");
            collider.setEnabled(false);
            if (currentWeapon != null) currentWeapon.onOwnerDeath();
        }
    }

    public void revive() {
        baseStats.health = baseStats.maxHealth;
        baseStats.armorPlates = 0;
        baseStats.heavyArmor = 0;
        baseStats.hasKevlarVest = false;
        baseStats.isDead = false;

        drawable.switchToDefault();
        drawable.setLayer(DrawLayer.Default);

        collider.setEnabled(true);
    }

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
    }

    @Override
    public Drawable getDrawableIfAny() {
        return drawable;
    }
}
