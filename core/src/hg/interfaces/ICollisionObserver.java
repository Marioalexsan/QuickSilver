package hg.interfaces;

import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.physics.Collider;

/**
 * A collision observer can be motified by a Collider of collisions that happen.
 * Generally the oberver would be the entity that owns the collider, or something like that
 */
public interface ICollisionObserver {
    /**
     * onMovementCollision communicates to collider owner that a movement collision happened
     * @param other The collider that overlapped this one
     */
    default void onGenericCollision(Collider other) {}

    /**
     * onMovementCollision communicates to collider owner that a combat collision happened
     * It's not necessary for the collider owner to also be the attack owner
     * @param other The collider that overlapped this one
     */
    default void onCombatCollision(Collider other) {}

    /**
     * onAttackHit processes effects applied to the attack owner after a succesful attack
     * @param defender The defender hit
     */
    void onAttackHit(BaseStats defender);

    /**
     * onHitByAttack processes effects applied to the defender owner after getting hit by an attack
     * @param attacker The attacker that hit
     */
    void onHitByAttack(AttackStats attacker);
}
