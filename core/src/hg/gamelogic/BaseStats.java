package hg.gamelogic;

import hg.entities.Entity;
import hg.interfaces.ICollisionObserver;

/**
 * Structure that holds information about entities.
 */
public class BaseStats {

    public Entity owner;

    public boolean isDead = false; // 0 HP may not necessarily mean dead, and death may not necessarily mean the entity should be gone
    public float maxHealth = 100f;
    public float health = 100f;
    public float maxHeavyArmor = 100f;
    public float heavyArmor = 0f;
    public int maxArmorPlates = 10;
    public int armorPlates = 3;
    public boolean hasKevlarVest = false;
    public int invulnerabilityFrames = 0;

    public float baseMoveSpeed = 10f;

    public BaseStats(Entity owner) {
        this.owner = owner;
    }
}
