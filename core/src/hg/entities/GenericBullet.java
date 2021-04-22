package hg.entities;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.AssetLoader;
import hg.game.HgGame;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.ColliderProperty;
import hg.physics.SphereCollider;
import hg.utils.Angle;

public class GenericBullet extends Entity {
    public float speed = 30.0f;
    public float maxDistance = 1800f;
    public float currentDistance = 0.0f;

    private final BasicSprite drawable = new BasicSprite();
    private final SphereCollider collider = new SphereCollider(14f);

    public GenericBullet() {
        AssetLoader assets = HgGame.Assets(); // Extra dependency

        collider.owner = this;
        collider.group = ColliderGroup.Player_Projectile;
        for (var value : ColliderGroup.values()) {
            collider.groupProperties.put(value, ColliderProperty.Notify);
        }
        collider.groupProperties.put(ColliderGroup.Environment_ShootThrough, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.Player, ColliderProperty.DoNothing);

        drawable.setTexture(assets.loadTexture("Assets/Bullet.png"));
        drawable.setTextureAngle(new Angle(-90f));
        drawable.setLayer(DrawLayer.CeilingAir);

        collider.setPosition(position);
        collider.setAngle(angle);

        drawable.setPosition(position);
        drawable.setAngle(angle);
        drawable.centerToRegion();

        collider.registerToEngine(); // Allocation!
        drawable.registerToEngine(); // Allocation!
    }

    @Override
    public void onCombatCollision(Collider other) {
        toBeDestroyed = true;
    }

    @Override
    public void onGenericCollision(Collider other) {
        toBeDestroyed = true;
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onKill(Entity victim) {}

    @Override
    public void onHitByAttack(AttackStats attacker) { }

    @Override
    public void onDeath(Entity killer) {}

    @Override
    public void destroy() {
        collider.unregisterFromEngine(); // Deallocation!
        drawable.unregisterFromEngine(); // Deallocation!
    }

    @Override
    public void clientUpdate() {
        serverUpdate();
    }

    @Override
    public void serverUpdate() {
        if (speed <= 0) {
            toBeDestroyed = true;
            return;
        }

        moveForward(speed);
        currentDistance += speed;

        if (currentDistance >= maxDistance) toBeDestroyed = true;
    }

    @Override
    public Collider getColliderIfAny() {
        return collider;
    }
}
