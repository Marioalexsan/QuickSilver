package hg.entities;

import com.badlogic.gdx.math.Vector2;
import hg.drawables.Animation;
import hg.drawables.gfxeffects.AfterImage;
import hg.libraries.WeaponLibrary;
import hg.enums.DirectorType;
import hg.directors.GameSession;
import hg.drawables.DrawLayer;
import hg.drawables.Drawable;
import hg.engine.MappedAction;
import hg.engine.NetworkEngine;
import hg.game.GameManager;
import hg.game.HgGame;
import hg.gamelogic.gamemodes.Gamemode;
import hg.gamelogic.playerlogic.LocalPlayerLogic;
import hg.gamelogic.ObjectState;
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
import hg.enums.TargetType;
import hg.enums.WeaponType;
import hg.utils.DebugLevels;
import hg.utils.MathTools;
import hg.weapons.Revolver;

import java.util.ArrayList;
import java.util.HashMap;

/** PlayerEntity is the player character. It can either be controlled by an AI, a human behind a keyboard, or over the network! */
public class PlayerEntity extends Entity {
    public static class State extends ObjectState.PositionState {
        public float smoothSpeedX;
        public float smoothSpeedY;
        public float boostSpeedX;
        public float boostSpeedY;
        public int boostCooldown;
        public BaseStats baseStats; // Does not send Entity

        public int currentWeapon;
        public HashMap<Integer, ObjectState> weaponStates;
    }

    private PlayerLogic playerLogic;

    protected Animation drawable = new Animation();
    protected SphereCollider collider = new SphereCollider(50);

    protected final Vector2 smoothSpeed = new Vector2();
    protected final Vector2 boostSpeed = new Vector2();
    protected int boostCooldown = 0;

    protected HashMap<Integer, IWeapon> weapons = new HashMap<>();

    protected int lastWeapon;
    protected int currentWeapon;

    protected int localOnly_boostImageCycle = 0;

    private static final float BoostStaminaCost = 45f;
    private static final int BoostCooldownToSet = 30;
    private static final float BoostStrength = 30f;
    private static final int BoostInvulnerability = 16;

    public PlayerEntity(PlayerLogic playerLogic) {
        setLogic(playerLogic);

        baseStats = new BaseStats(this);
        collider.group = ColliderGroup.Player;
        collider.baseStats = baseStats;
        collider.owner = this;

        GameSession director = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
        if (director != null) {
            var sets = director.getSettings();
            if (sets.hardcore) {
                baseStats.maxHealth = 65f;
                baseStats.baseMoveSpeed = 18f;
                baseStats.maxArmorPlates = 6;
                baseStats.maxHeavyArmor = 70f;
            }
        }

        revive();

        drawable.addKnownAnimation("Death", AnimationLibrary.GetAnimationInfo("Player_Death"));

        drawable.addKnownAnimation("Rifle_Idle", AnimationLibrary.GetAnimationInfo("Player_Rifle_Idle"));
        drawable.addKnownAnimation("Rifle_Reload", AnimationLibrary.GetAnimationInfo("Player_Rifle_Reload"));
        drawable.addKnownAnimation("Rifle_Shoot", AnimationLibrary.GetAnimationInfo("Player_Rifle_Shoot"));
        drawable.setDefaultAnimation("Revolver_Idle");

        drawable.addKnownAnimation("Revolver_Idle", AnimationLibrary.GetAnimationInfo("Player_Revolver_Idle"));
        drawable.addKnownAnimation("Revolver_Reload", AnimationLibrary.GetAnimationInfo("Player_Revolver_Reload"));
        drawable.addKnownAnimation("Revolver_Shoot", AnimationLibrary.GetAnimationInfo("Player_Revolver_Shoot"));
        drawable.addKnownAnimation("Shotgun_Idle", AnimationLibrary.GetAnimationInfo("Player_Shotgun_Idle"));
        drawable.addKnownAnimation("Shotgun_Reload", AnimationLibrary.GetAnimationInfo("Player_Shotgun_Reload"));
        drawable.addKnownAnimation("Shotgun_PowerShoot", AnimationLibrary.GetAnimationInfo("Player_Shotgun_PowerShoot"));
        drawable.addKnownAnimation("Shotgun_Shoot", AnimationLibrary.GetAnimationInfo("Player_Shotgun_Shoot"));

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

    public HashMap<Integer, IWeapon> viewWeapons() {
        return new HashMap<>(weapons);
    }

    public int viewSelectedWeapon() {
        return currentWeapon;
    }

    @Override
    public void update() {
        boolean isServer = HgGame.Network().isLocalOrServer();

        drawable.update();

        baseStats.update();

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
        float totalMoveSpeed = baseStats.baseMoveSpeed * (baseStats.heavyArmor > 0 ? 0.9f : 1f);

        Vector2 targetSpeed = new Vector2(moveDirection).nor().scl(totalMoveSpeed);
        smoothSpeed.add(targetSpeed.sub(smoothSpeed).scl(decayFactor));
        move(smoothSpeed);

        if (!boostSpeed.isZero()) {
            localOnly_boostImageCycle = (localOnly_boostImageCycle + 1) % 2;
            if (localOnly_boostImageCycle == 0) {
                AfterImage hax = new AfterImage(drawable, 32);
                if (baseStats.invulnerabilityFrames == 0) {
                    hax.getColor().mul(0.66f, 0.66f, 0.66f, 0.45f);
                }
                hax.getColor().mul(1f, 1f, 1f, 0.5f + 0.5f * boostSpeed.len() / BoostStrength);
                HgGame.Manager().addGFXEffectToManage(hax);
            }

            move(boostSpeed);
            boostSpeed.scl(0.9f);
            if (boostSpeed.isZero(3f)) boostSpeed.set(0, 0);
        }

        if (boostCooldown > 0) boostCooldown--;

        // Resolve aim direction

        if (!baseStats.isDead) {
            if (aim != null)
                angle.set(new Vector2(aim).angleDeg());

            IWeapon heldWeapon = weapons.get(currentWeapon);

            if (actions != null) {
                if (heldWeapon != null) {
                    if (actions.contains(MappedAction.QuickSwitchWeapon)) tryWeaponSwitch(lastWeapon);
                    else if (actions.contains(MappedAction.WeaponOne)) tryWeaponSwitch(WeaponType.Revolver);
                    else if (actions.contains(MappedAction.WeaponTwo)) tryWeaponSwitch(WeaponType.AssaultRifle);
                    else if (actions.contains(MappedAction.WeaponThree)) tryWeaponSwitch(WeaponType.DBShotgun);
                    else if (actions.contains(MappedAction.Reload)) heldWeapon.onReload();
                    else if (actions.contains(MappedAction.PrimaryFire)) heldWeapon.onPrimaryFire();
                    else if (actions.contains(MappedAction.SecondaryFire)) heldWeapon.onSecondaryFire();

                    heldWeapon.onUpdate();
                }

                if (actions.contains(MappedAction.Boost)) tryBoost(moveDirection);

            }
        }

        if (playerLogic instanceof LocalPlayerLogic) {
            ((LocalPlayerLogic) playerLogic).sendActions();
        }
    }

    private void tryWeaponSwitch(int newWeapon) {
        if (newWeapon == currentWeapon || weapons.get(newWeapon) == null) return;
        lastWeapon = currentWeapon;
        currentWeapon = newWeapon;
        weapons.get(lastWeapon).onUnequip();
        weapons.get(currentWeapon).onEquip();
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

        if (baseStats.hasKevlarVest) reduction += 0.3f;
        if (baseStats.heavyArmor > 0) {
            reduction += 0.5f;
            baseStats.heavyArmor = MathTools.Clamp(baseStats.heavyArmor - damage * 0.25f, 0f, baseStats.maxHeavyArmor);
        }
        damage *= MathTools.Clamp(1f - reduction, 0f, 1f);

        baseStats.health = MathTools.Clamp(baseStats.health - damage, 0f, baseStats.maxHealth);

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

                GameSession match = (GameSession) HgGame.Manager().getDirector(DirectorType.GameSession);
                if (match != null) {
                    Gamemode mode = match.getGamemode();
                    if (mode != null) mode.onKillCallback(killer, this);
                }
            }

            IWeapon heldWeapon = weapons.get(currentWeapon);
            if (heldWeapon != null) heldWeapon.onOwnerDeath();

            if (network.isLocalOrServer()) {
                NetInstruction msg = new NetInstruction(TargetType.Actors, ID, 1).setInts(killer != null ? killer.ID : -1);
                HgGame.Network().sendToAllClients(msg, true);
            }
        }
    }

    public void revive() {
        baseStats.health = baseStats.maxHealth;
        baseStats.armorPlates = 0;
        baseStats.heavyArmor = 0f;
        baseStats.hasKevlarVest = false;
        baseStats.isDead = false;

        drawable.switchToDefault();
        drawable.setLayer(DrawLayer.Default);

        collider.setEnabled(true);

        for (var weapon: new ArrayList<>(weapons.keySet())) removeWeapon(weapon);
        weapons.put(WeaponType.Revolver, new Revolver(this));

        currentWeapon = WeaponType.Revolver;
        lastWeapon = WeaponType.Revolver;

        weapons.get(currentWeapon).onEquip();

        NetworkEngine network = HgGame.Network();

        if (network.isLocalOrServer()) {
            NetInstruction msg = new NetInstruction(TargetType.Actors, ID, 2);
            network.sendToAllClients(msg, true);
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
            case 3 -> onWeaponPickup(msg.intParams[0]);
            case 4 -> onAmmoPickup(msg.intParams[0]);
            case 5 -> heal(msg.floatParams[0]);
            case 6 -> obtainArmorPlates(msg.intParams[0]);
            case 7 -> obtainVest();
            case 8 -> obtainHeavyArmor(msg.floatParams[0]);
            default -> HgGame.Chat().addDebugMessage("Update for unallowed type " + msg.insType, DebugLevels.Warn);
        }
    }

    public void heal(float amount) {
        GameManager manager = HgGame.Manager();
        if (this == manager.localView.playerEntity) HgGame.SetNotice("Healed " + (int) amount + " HP!", 35);
        baseStats.health = Math.min(baseStats.health + amount, baseStats.maxHealth);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);
    }

    public void obtainArmorPlates(int count) {
        GameManager manager = HgGame.Manager();
        if (this == manager.localView.playerEntity)
            HgGame.SetNotice("Obtained " + (count == 1 ? "an" : count) + " Armor Plate" + (count == 1 ? "" : "s") + "!", 35);
        baseStats.armorPlates = Math.min(baseStats.armorPlates + count, baseStats.maxArmorPlates);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);
    }

    public void obtainVest() {
        GameManager manager = HgGame.Manager();
        if (this == manager.localView.playerEntity)
            HgGame.SetNotice("Obtained Kevlar Vest!", 35);
        baseStats.hasKevlarVest = true;

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);
    }

    public void obtainHeavyArmor(float amount) {
        GameManager manager = HgGame.Manager();
        if (this == manager.localView.playerEntity)
            HgGame.SetNotice("Obtained Heavy Armor!", 35);
        baseStats.heavyArmor = Math.min(baseStats.heavyArmor + amount, baseStats.maxHeavyArmor);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);
    }

    public void removeWeapon(int weapon) {
        weapons.remove(weapon);

        if (weapon == currentWeapon) currentWeapon = WeaponType.Revolver;
        if (weapon == lastWeapon) lastWeapon = WeaponType.Revolver;
    }

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
        playerLogic.setControlledPlayer(null);
    }

    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public Drawable[] getDrawableArray() {
        return new Drawable[] { drawable };
    }

    public void onWeaponPickup(int type) {
        GameManager manager = HgGame.Manager();

        IWeapon existing = weapons.get(type);
        boolean doNotice = this == manager.localView.playerEntity;

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);

        if (existing != null) {
            existing.onWeaponPickup();
            if (doNotice) HgGame.SetNotice("Took ammo from the Weapon!", 35);
            return;
        }

        IWeapon newWeapon = WeaponLibrary.GetWeapon(type);
        if (newWeapon != null) {
            newWeapon.setOwner(this);
            weapons.put(type, newWeapon);
            if (doNotice) HgGame.SetNotice(WeaponLibrary.GetWeaponPickupHint(type), 35);
        }
    }

    public void onAmmoPickup(int type) {
        GameManager manager = HgGame.Manager();
        boolean doNotice = this == manager.localView.playerEntity;

        if (type == WeaponType.AllWeapons) {
            for (var weapon: weapons.values()) weapon.onAmmoPackPickup();
        }

        IWeapon existing = weapons.get(type);
        if (existing != null) {
            existing.onAmmoPackPickup();
        }
        if (doNotice) HgGame.SetNotice("Picked up some ammo!", 35);

        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/magin.ogg"), 1f, position);
    }

    public void tryBoost(Vector2 moveDirection) {
        if (moveDirection.isZero() || boostCooldown > 0 || baseStats.stamina < BoostStaminaCost) return;
        HgGame.Audio().playSound(HgGame.Assets().loadSound("Assets/Audio/boost.ogg"), 1f, position);
        baseStats.stamina -= BoostStaminaCost;
        boostSpeed.set(new Vector2(moveDirection).nor().scl(BoostStrength));
        boostCooldown = BoostCooldownToSet;
        baseStats.invulnerabilityFrames = BoostInvulnerability;
        baseStats.staminaRegenCooldown = baseStats.staminaRegenCooldownToSet;
    }

    @Override
    public ObjectState tryGenerateState() {
        State stuff = new State();
        stuff.copyPosition(this);
        stuff.smoothSpeedX = smoothSpeed.x;
        stuff.smoothSpeedY = smoothSpeed.y;
        stuff.boostSpeedX = boostSpeed.x;
        stuff.boostSpeedY = boostSpeed.y;
        stuff.baseStats = baseStats;
        stuff.boostCooldown = boostCooldown;

        stuff.currentWeapon = currentWeapon;

        stuff.weaponStates = new HashMap<>();
        for (var weapon: weapons.entrySet()) {
            stuff.weaponStates.put(weapon.getKey(), weapon.getValue().tryGetState());
        }
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            stuff.applyPosition(this);
            smoothSpeed.set(stuff.smoothSpeedX, stuff.smoothSpeedY);
            boostSpeed.set(stuff.boostSpeedX, stuff.boostSpeedY);
            boostCooldown = stuff.boostCooldown;
            baseStats.copyFrom(stuff.baseStats);
            currentWeapon = stuff.currentWeapon;

            // Update state for weapons that server has
            for (var weaponState: stuff.weaponStates.entrySet()) {
                IWeapon weapon = weapons.get(weaponState.getKey());
                if (weapon == null) {
                    int type = weaponState.getKey();
                    weapons.put(type, WeaponLibrary.GetWeapon(type));
                    weapon = weapons.get(type);
                }
                weapon.tryApplyState(weaponState.getValue());
            }

            // Remove any weapons that client has, but server doesn't
            for (var weapon: new ArrayList<>(weapons.keySet())) {
                if (!stuff.weaponStates.containsKey(weapon))
                    removeWeapon(weapon);
            }
        }
    }
}
