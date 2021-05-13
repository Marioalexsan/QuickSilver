package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.animation.*;
import hg.directors.DirectorTypes;
import hg.directors.MatchDirector;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.engine.MappedAction;
import hg.engine.NetworkEngine;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Gamemode;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.states.PlayerState;
import hg.gamelogic.states.State;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.interfaces.IWeapon;
import hg.libraries.AnimationLibrary;
import hg.networking.packets.NetInstruction;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.SphereCollider;
import hg.gamelogic.playerlogic.EmptyAI;
import hg.gamelogic.playerlogic.PlayerLogic;
import hg.types.TargetType;
import hg.types.WeaponType;
import hg.utils.DebugLevels;
import hg.utils.MathUtils;
import hg.weapons.AssaultRifle;
import hg.weapons.Revolver;

import java.util.HashMap;

public class PlayerEntity extends Entity {
    private PlayerLogic playerLogic;
    private final Vector2 smoothSpeed = new Vector2();

    protected Animation drawable = new Animation();
    protected SphereCollider collider = new SphereCollider(50);

    protected HashMap<Integer, IWeapon> weapons = new HashMap<>();

    protected int lastWeapon;
    protected int currentWeapon;

    public PlayerEntity(PlayerLogic playerLogic) {
        setLogic(playerLogic);

        weapons.put(WeaponType.Revolver, new Revolver(this));
        weapons.put(WeaponType.AssaultRifle, new AssaultRifle(this));

        lastWeapon = WeaponType.AssaultRifle;
        currentWeapon = WeaponType.Revolver;

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

    public void setLogic(PlayerLogic newLogic) {
        if (playerLogic != null) playerLogic.setControlledPlayer(null);
        if (newLogic == null) newLogic = new EmptyAI();
        playerLogic = newLogic;
        playerLogic.setControlledPlayer(this);
    }

    public PlayerLogic getLogic() {
        return playerLogic;
    }

    @Override
    public void update() {
        boolean isServer = HgGame.Network().isLocalOrServer();

        drawable.update();

        if (isServer) {
            baseStats.update();
        }

        playerLogic.update();
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

            IWeapon heldWeapon = weapons.get(currentWeapon);

            if (heldWeapon != null) {
                if (actions != null) {
                    if (actions.contains(MappedAction.QuickSwitchWeapon) && currentWeapon != lastWeapon) {
                        heldWeapon.onUnequip();
                        int swap = currentWeapon;
                        currentWeapon = lastWeapon;
                        lastWeapon = swap;
                        heldWeapon = weapons.get(currentWeapon);
                        heldWeapon.onEquip();
                    }
                    if (actions.contains(MappedAction.Reload)) heldWeapon.onReload();
                    if (actions.contains(MappedAction.PrimaryFire)) heldWeapon.onPrimaryFire();
                }

                heldWeapon.onUpdate();
            }
        }

        if (playerLogic instanceof LocalPlayerLogic) {
            ((LocalPlayerLogic) playerLogic).sendActions();
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
    }

    @Override
    public void onHitByAttack(AttackStats attacker) {
        if (baseStats.invulnerabilityFrames > 0 || attacker.baseDamage <= 0.0) return;

        NetworkEngine network = HgGame.Network();

        if (network.isLocalOrServer()) {
            takeDamage(attacker.baseDamage);

            if (baseStats.health <= 0.0) onDeath(attacker.owner);
        }

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/GenericHurt.ogg"), 1f);
    }

    public void takeDamage(float damage) {
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
            baseStats.heavyArmor = MathUtils.ClampValue(baseStats.heavyArmor - damage * 0.5f, 0f, baseStats.maxHeavyArmor);
        }
        damage *= MathUtils.ClampValue(1f - reduction, 0f, 1f);

        baseStats.health = MathUtils.ClampValue(baseStats.health - damage, 0f, baseStats.maxHealth);

        if (baseStats.health >= 0.0)
            HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/PlayerHurt.ogg"), 1f, position);

        NetInstruction msg = new NetInstruction(TargetType.Actors, ID, 0).setFloats(damage);
        HgGame.Network().sendToAllClients(msg, true);
    }

    @Override
    public void onDeath(Entity killer) {
        NetworkEngine network = HgGame.Network();
        if (baseStats != null && !baseStats.isDead) {
            drawable.setLayer(DrawLayer.FloorAir);
            drawable.switchAnimation("Death");
            collider.setEnabled(false);

            baseStats.isDead = true;
            if (killer != null) {
                killer.onKill(this);

                MatchDirector match = (MatchDirector) HgGame.Manager().getDirector(DirectorTypes.MatchDirector);
                if (match != null) {
                    Gamemode mode = match.getGamemode();
                    if (mode != null) mode.onKillCallback(killer, this);
                }
            }

            IWeapon heldWeapon = weapons.get(currentWeapon);
            if (heldWeapon != null) heldWeapon.onOwnerDeath();

            if (network.isLocalOrServer()) {
                NetInstruction msg = new NetInstruction(TargetType.Actors, ID, 1).setInts(killer.ID);
                HgGame.Network().sendToAllClients(msg, true);
            }
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

        if (HgGame.Network().isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, ID, 2);
            HgGame.Network().sendToAllClients(msg, true);
        }
    }

    @Override
    public void onInstructionFromServer(NetInstruction msg) {
        GameManager manager = HgGame.Manager();

        switch (msg.insType) {
            case 0 -> takeDamage(msg.floatParams[0]);
            case 1 -> {
                Entity killer = manager.getActor(msg.intParams[0]);
                if (killer != null) onDeath(killer);
            }
            case 2 -> revive();
            default -> manager.getChatSystem().addDebugMessage("Update for unallowed type " + msg.insType, DebugLevels.Warn);
        }
    }

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
        playerLogic.setControlledPlayer(null);
    }

    @Override
    public Drawable getDrawableIfAny() {
        return drawable;
    }

    @Override
    public State tryGenerateState() {
        PlayerState stuff = new PlayerState();
        stuff.posX = position.x;
        stuff.posY = position.y;
        stuff.angle = angle.getDeg();
        stuff.smoothSpeedX = smoothSpeed.x;
        stuff.smoothSpeedY = smoothSpeed.y;
        stuff.baseStats = baseStats;

        stuff.currentWeapon = currentWeapon;

        stuff.weaponStates = new HashMap<>();
        for (var weapon: weapons.entrySet()) {
            stuff.weaponStates.put(weapon.getKey(), weapon.getValue().tryGetState());
        }
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof PlayerState) {
            PlayerState stuff = (PlayerState) state;
            position.set(stuff.posX, stuff.posY);
            angle.set(stuff.angle);
            smoothSpeed.set(stuff.smoothSpeedX, stuff.smoothSpeedY);
            baseStats.copyFrom(stuff.baseStats);
            currentWeapon = stuff.currentWeapon;

            for (var weaponState: stuff.weaponStates.entrySet()) {
                IWeapon weapon = weapons.get(weaponState.getKey());
                if (weapon != null) weapon.tryApplyState(weaponState.getValue());
            }
        }
    }
}
