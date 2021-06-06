package hg.interfaces.callbacks;

import hg.entities.PlayerEntity;

public interface IGenericPickupCallback {
    void onPickup(PlayerEntity target);
}
