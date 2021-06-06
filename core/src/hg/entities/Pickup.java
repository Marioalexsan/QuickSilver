package hg.entities;

import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.gamelogic.ObjectState;
import hg.interfaces.ICollisionObserver;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.ColliderProperty;
import hg.physics.SphereCollider;

/** Pickup is a base class for world objects which can be "picked up", triggering actions upon doing so. */
public abstract class Pickup extends Entity {
    public static class State extends ObjectState.PositionState {
        public boolean pickedUp;
    }

    protected final SphereCollider pickupZone;
    protected Spawner creator = null;
    protected boolean pickedUp = false;

    public Pickup(int pickupRadius) {
        pickupZone = new SphereCollider(pickupRadius);
        pickupZone.owner = this;
        pickupZone.group = ColliderGroup.Pickups;
        pickupZone.clearGroupProperties();
        pickupZone.groupProperties.put(ColliderGroup.Player, ColliderProperty.Notify);
        pickupZone.registerToEngine();
    }

    public void setCreator(Spawner creator) {
        this.creator = creator;
    }

    public void setPickupRadius(int radius) {
        pickupZone.setRadius(radius);
    }

    @Override
    public void onGenericCollision(Collider other) {
        if (HgGame.Network().isLocalOrServer()) pickedUp(other.owner);
    }

    public void pickedUp(ICollisionObserver which) {
        // Called by server on pickup, and by client on net instruction!
        if (!pickedUp && which instanceof PlayerEntity) {
            pickedUp = true;
            toBeDestroyed = true;
            if (creator != null) creator.addSpawns(1);
            onPickup((PlayerEntity) which);
        }
    }

    abstract public void onPickup(PlayerEntity target);

    @Override
    public void destroy() {
        pickupZone.unregisterFromEngine();
    }

    @Override
    public ObjectState tryGenerateState() {
        State stuff = new State();
        stuff.copyPosition(this);
        stuff.pickedUp = pickedUp;
        return stuff;
    }

    @Override
    public void tryApplyState(ObjectState state) {
        if (state instanceof State) {
            State stuff = (State) state;
            stuff.applyPosition(this);
            pickedUp = stuff.pickedUp;
        }
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }
}
