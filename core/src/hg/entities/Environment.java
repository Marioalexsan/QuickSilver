package hg.entities;

import hg.drawables.Drawable;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.Collider;

import java.util.LinkedList;

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
    public void clientUpdate() {}

    @Override
    public void serverUpdate() {}

    @Override
    public void onGenericCollision(Collider other) {}

    @Override
    public void onCombatCollision(Collider other) {}

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) {}

    @Override
    public void onKill(Entity victim) { }

    @Override
    public void onDeath(Entity killer) { }
}
