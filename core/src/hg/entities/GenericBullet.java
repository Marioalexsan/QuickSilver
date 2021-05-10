package hg.entities;

import hg.drawables.BasicSprite;
import hg.drawables.DrawLayer;
import hg.engine.AssetEngine;
import hg.game.HgGame;
import hg.game.State;
import hg.gamelogic.BaseStats;
import hg.gamelogic.AttackStats;
import hg.physics.*;
import hg.types.EntityType;
import hg.utils.Angle;

public class GenericBullet extends Entity {
    public float speed = 38f;
    public float maxDistance = 1800f;
    public float currentDistance = 0.0f;

    private final BasicSprite drawable = new BasicSprite();
    private final BoxCollider collider = new BoxCollider(32f, 12f);

    public GenericBullet() {
        AssetEngine assets = HgGame.Assets(); // Extra dependency

        collider.owner = this;
        collider.group = ColliderGroup.Player_Projectile;
        for (var value : ColliderGroup.values()) {
            collider.groupProperties.put(value, ColliderProperty.Notify);
        }
        collider.groupProperties.put(ColliderGroup.Environment_ShootThrough, ColliderProperty.DoNothing);
        collider.groupProperties.put(ColliderGroup.Player, ColliderProperty.DoNothing);

        drawable.setTexture(assets.loadTexture("Assets/Sprites/Projectiles/Bullet.png"));
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

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
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
    public void update() {
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

    public static class GenericBulletState extends State {
        public float posX;
        public float posY;
        public float angle;
        public float speed;
        public float maxDistance;
        public float currentDistance;
    }

    @Override
    public State tryGenerateState() {
        GenericBulletState stuff = new GenericBulletState();
        stuff.posX = position.x;
        stuff.posY = position.y;
        stuff.angle = angle.getDeg();
        stuff.speed = speed;
        stuff.currentDistance = currentDistance;
        stuff.maxDistance = maxDistance;
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof GenericBulletState) {
            GenericBulletState stuff = (GenericBulletState) state;
            this.position.set(stuff.posX, stuff.posY);
            this.angle.set(stuff.angle);
            this.speed = stuff.speed;
            this.currentDistance = stuff.currentDistance;
            this.maxDistance = stuff.maxDistance;
        }
    }
}
