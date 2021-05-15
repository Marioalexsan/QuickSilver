package hg.gamelogic;

import hg.entities.Entity;
import hg.interfaces.ICollisionObserver;
import hg.physics.ColliderGroup;

import java.util.Arrays;
import java.util.HashSet;

public class AttackStats {

    public Entity owner;

    public float baseDamage;

    public boolean hurtsTeammates = false;
    public boolean hurtsSelf = false;

    public final HashSet<ColliderGroup> targetedGroups = new HashSet<>();
    public final HashSet<ICollisionObserver> hitList = new HashSet<>();

    public AttackStats(Entity attacker, float baseDamage, ColliderGroup... targetedGroups) {
        this.owner = attacker;
        this.baseDamage = baseDamage;
        this.targetedGroups.addAll(Arrays.asList(targetedGroups));
    }

}
