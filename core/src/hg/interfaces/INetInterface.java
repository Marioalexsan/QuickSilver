package hg.interfaces;

import hg.networking.packets.NetInstruction;

public interface INetInterface {
    default void onInstructionFromServer(NetInstruction msg) {}
}
