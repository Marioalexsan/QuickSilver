package hg.entities;

import hg.drawables.Drawable;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.Collider;

import java.util.LinkedList;


/** Environments are entities that represent walls, decorations, etc. that don't change their state during a match.
 * These are not updated by the network (hence, the name Static).
 * Ideally, Colliders used by Environments should be heavy, movement-only colliders */
public class Environment extends Entity {
    private final LinkedList<Drawable> drawables;
    private final LinkedList<Collider> colliders;

    public Environment(LinkedList<Drawable> drawables, LinkedList<Collider> colliders) {
        this.drawables = drawables;
        this.colliders = colliders;

        for (var drawable : drawables) {
            drawable.setPosition(position);
            drawable.setAngle(angle);
            drawable.registerToEngine();
        }
        for (var collider : colliders) {
            collider.setPosition(position);
            collider.setAngle(angle);
            collider.registerToEngine();
            collider.makeHeavy();

            collider.owner = this;
        }
    }

    @Override
    public void destroy() {
        for (var drawable : drawables) {
            drawable.unregisterFromEngine();
        }
        for (var collider : colliders) {
            collider.unregisterFromEngine();
        }
    }

    @Override
    public void update() {}

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }
}
