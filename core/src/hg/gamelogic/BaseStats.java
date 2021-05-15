package hg.gamelogic;

import hg.entities.Entity;
import hg.interfaces.IUpdateable;

/**
 * Structure that holds information about entities.
 */
public class BaseStats implements IUpdateable {

    public transient Entity owner;

    public boolean isDead = false; // 0 HP may not necessarily mean dead, and death may not necessarily mean the entity should be gone
    public float maxHealth = 100f;
    public float health = 100f;
    public float maxHeavyArmor = 100f;
    public float heavyArmor = 0f;
    public int maxArmorPlates = 10;
    public int armorPlates = 0;
    public boolean hasKevlarVest = false;
    public int invulnerabilityFrames = 0;

    public float baseMoveSpeed = 9.4f;

    public int deathCounter = 0;


    public BaseStats(Entity owner) {
        this.owner = owner;
    }

    @Override
    public void update() {
        if (isDead) deathCounter++;
        else deathCounter = 0;

        if (invulnerabilityFrames > 0) invulnerabilityFrames--;
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
    }

    // Do not use. This is for Kryonet
    public BaseStats() { }
}
