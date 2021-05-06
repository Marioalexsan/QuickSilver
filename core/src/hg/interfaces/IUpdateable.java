package hg.interfaces;

/**
 * IUpdate defines an interface for updating objects each frame.
 * Call order inside GameManager is localUpdate(), then either clientUpdate() or serverUpdate() based on network role
 */
public interface IUpdateable {
    /** localUpdate() runs updates that are only relevant for the local machine */
    default void localUpdate() { }

    /** clientUpdate() runs updates that are relevant if this machine is a client */
    default void clientUpdate() { }

    /** serverUpdate() runs updates that are relevant if this machine is a host (either server, or local game) */
    default void serverUpdate() { }
}

// TO BE IMPROVED LATER