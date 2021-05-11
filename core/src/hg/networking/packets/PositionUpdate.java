package hg.networking.packets;

import hg.entities.Entity;
import hg.game.HgGame;
import hg.networking.Packet;
import hg.utils.Angle;

/** This packet is used for lightweight position updates. */
public class PositionUpdate extends Packet {
    public int targetID;
    public float posX;
    public float posY;
    public float angle;

    public PositionUpdate(int targetID, float posX, float posY, float angle) {
        this.targetID = targetID;
        this.posX = posX;
        this.posY = posY;
        this.angle = angle;
    }

    @Override
    public void parseOnClient() {
        Entity target = HgGame.Manager().getActor(targetID);

        if (target == null) return;

        target.setPosition(posX, posY);
        target.setAngle(new Angle(angle));
    }

    public PositionUpdate() {} // For Kryonet
}
