package hg.entities;

import hg.gamelogic.AttackStats;
import hg.gamelogic.BaseStats;

public class PlayerSpawnPoint extends Entity {
    public void respawnPlayerHere(Player player) {
        player.setPosition(this.position);
        player.setAngle(this.angle);
        player.revive();
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
    public void clientUpdate() { }

    @Override
    public void serverUpdate() { }
}
