package hg.entities.pickups;

import hg.entities.Entity;
import hg.entities.PlayerEntity;
import hg.game.HgGame;
import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;
import hg.gamelogic.states.PickupState;
import hg.gamelogic.states.State;
import hg.interfaces.ICollisionObserver;
import hg.physics.Collider;
import hg.physics.ColliderGroup;
import hg.physics.ColliderProperty;
import hg.physics.SphereCollider;

public abstract class Pickup extends Entity {
    protected final SphereCollider pickupZone;
    protected boolean pickedUp = false;

    public Pickup(int pickupRadius) {
        pickupZone = new SphereCollider(pickupRadius);
        pickupZone.owner = this;
        pickupZone.group = ColliderGroup.Pickups;
        pickupZone.clearGroupProperties();
        pickupZone.groupProperties.put(ColliderGroup.Player, ColliderProperty.Notify);
        pickupZone.registerToEngine();
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
            onPickup((PlayerEntity) which);
        }
    }

    abstract public void onPickup(PlayerEntity target);

    @Override
    public void destroy() {
        pickupZone.unregisterFromEngine();
    }

    @Override
    public State tryGenerateState() {
        PickupState stuff = new PickupState();
        stuff.copyPosition(this);
        stuff.pickedUp = pickedUp;
        return stuff;
    }

    @Override
    public void tryApplyState(State state) {
        if (state instanceof PickupState) {
            PickupState stuff = (PickupState) state;
            stuff.applyPosition(this);
            pickedUp = stuff.pickedUp;
        }
    }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }
}
