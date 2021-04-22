package hg.interfaces;

public interface IDestroyable {
    void signalDestruction();
    boolean isDestructionSignalled();
    void destroy();
}
