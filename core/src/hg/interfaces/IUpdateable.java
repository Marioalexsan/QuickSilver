package hg.interfaces;

/**
 * IUpdate defines an interface for updating objects each frame.
 * Two things must be defined:
 * * updates that run on clients
 * * updates that run on servers
 */
public interface IUpdateable {
    void clientUpdate();
    void serverUpdate();
}
