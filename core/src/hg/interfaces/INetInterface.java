package hg.interfaces;

import hg.networking.packets.NetInstruction;

/** Interface for objects that can receive NetInstructions */
public interface INetInterface {
    default void onInstructionFromServer(NetInstruction msg) {}
}
