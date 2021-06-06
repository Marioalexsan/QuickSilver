package hg.gamelogic;

import hg.entities.Entity;
import hg.interfaces.IUpdateable;
import hg.utils.MathTools;

/** Structure that holds information about entities. */
public class BaseStats implements IUpdateable {
    public transient Entity owner;

    public boolean isDead = false; // 0 HP != dead or destroyed
    public int invulnerabilityFrames = 0;

    public float maxHealth = 100f;
    public float health = 100f;

    public float maxHeavyArmor = 100f;
    public float heavyArmor = 0f;

    public int maxArmorPlates = 10;
    public int armorPlates = 0;

    public float maxStamina = 100f;
    public float stamina = 100f;
    public int staminaRegenCooldownToSet = 120;
    public int staminaRegenCooldown = 0;

    public boolean hasKevlarVest = false;

    public float baseMoveSpeed = 10f;

    public int deathCounter = 0;

    public BaseStats(Entity owner) {
        this.owner = owner;
    }

    @Override
    public void update() {
        deathCounter = isDead ? deathCounter + 1 : 0;

        if (invulnerabilityFrames > 0) invulnerabilityFrames--;
        if (staminaRegenCooldown > 0) staminaRegenCooldown--;

        else stamina = MathTools.Clamp(stamina + 0.5f, 0f, maxStamina);
    }

    public void copyFrom(BaseStats other) {
        if (other == null) return;
        isDead = other.isDead;
        maxHealth = other.maxHealth;
        health = other.health;
        maxHeavyArmor = other.maxHeavyArmor;
        heavyArmor = other.heavyArmor;
        maxArmorPlates = other.maxArmorPlates;
        armorPlates = other.armorPlates;
        hasKevlarVest = other.hasKevlarVest;
        invulnerabilityFrames = other.invulnerabilityFrames;
        baseMoveSpeed = other.baseMoveSpeed;
        deathCounter = other.deathCounter;
        maxStamina = other.maxStamina;
        stamina = other.stamina;
    }

    // Do not use. This is for Kryonet
    public BaseStats() { }
}
