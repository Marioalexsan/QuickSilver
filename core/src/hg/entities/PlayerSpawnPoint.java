package hg.entities;

import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;

public class PlayerSpawnPoint extends Entity {
    public void respawnPlayerHere(PlayerEntity playerEntity) {
        playerEntity.setPosition(this.position);
        playerEntity.setAngle(this.angle);
        playerEntity.revive();
    }

    @Override
    public void onDeath(Entity killer) { }

    @Override
    public void onKill(Entity victim) { }

    @Override
    public void onAttackHit(BaseStats defender) { }

    @Override
    public void onHitByAttack(AttackStats attacker) { }

    @Override
    public void destroy() { }

    @Override
    public void update() { }
}
