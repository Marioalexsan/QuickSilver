package hg.networking;

/** Represents the current network status of this machine. */
public enum NetworkStatus {
    Ready,
    ConnectingToServer,
    ConnectionFailed,
    GotDisconnectedAsClient
}