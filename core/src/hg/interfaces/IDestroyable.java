package hg.interfaces;

/** Interface for objects which need special actions to be done before they can be removed safely */
public interface IDestroyable {

    /** Signals that this object should be removed. Objects holding IDestroyables can then check if the object is destroyed, and do things */
    void signalDestroy();
    boolean isDestroySignalled();

    /** Executes destruction. This should be used for stuff that registers itself to the game engine, holds resources, etc. */
    void destroy();
}
