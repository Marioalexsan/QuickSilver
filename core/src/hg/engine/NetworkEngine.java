package hg.engine;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import hg.game.HgGame;
import hg.networking.*;
import hg.networking.Packet;
import hg.networking.packets.ClientInitRequest;
import hg.networking.packets.DisconnectNotice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// networkLock usage in here could be improved

public class NetworkEngine {
    private class ClientListener extends Listener {
        @Override
        public void connected(Connection connection) {
            ClientInitRequest msg = new ClientInitRequest();
            msg.clientName = HgGame.Game().localName;
            connection.sendTCP(msg);
            synchronized (networkLock) {
                netRole = NetworkRole.Client;
                netStatus = NetworkStatus.Ready;
            }
        }

        @Override
        public void disconnected(Connection connection) {
            kryonetClient.stop();
            synchronized (networkLock) {
                netRole = NetworkRole.Local;
                netStatus = NetworkStatus.GotDisconnectedAsClient;
            }
        }

        @Override
        public void received(Connection connection, Object object) {
            synchronized (networkLock) {
                // Kryonet internal messages can also be received, but we shouldn't store (or drop) them
                if (object instanceof Packet)
                    networkMessages.add(new NetworkMessage(0, (Packet) object));
            }
        }
    }

    private class ServerListener extends Listener {
        @Override
        public void connected(Connection connection) {
            synchronized (networkLock) {
                recentlyConnected.add(connection.getID());
            }
        }

        @Override
        public void disconnected(Connection connection) {
            synchronized (networkLock) {
                recentlyDisconnected.add(connection.getID());
            }
        }

        @Override
        public void received(Connection connection, Object object) {
            synchronized (networkLock) {
                // Kryonet internal messages can also be received, but we shouldn't store (or drop) them
                if (object instanceof Packet)
                    networkMessages.add(new NetworkMessage(connection.getID(), (Packet) object));
            }
        }
    }

    public static final int ConnectionTimeoutInMilli = 2500;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private NetworkRole netRole = NetworkRole.Local;
    private NetworkStatus netStatus = NetworkStatus.Ready;

    private int TCPPort = 52735;
    private int UDPPort = 52216;

    private final Server kryonetServer;
    private final Client kryonetClient;

    private final Object networkLock = new Object();

    private final LinkedList<NetworkMessage> networkMessages = new LinkedList<>(); // Messages received since last dump
    private final ArrayList<Integer> recentlyConnected = new ArrayList<>(); // Clients connected since last dump
    private final ArrayList<Integer> recentlyDisconnected = new ArrayList<>(); // Clients disconnected since last dump

    public NetworkEngine() {
        kryonetClient = new Client();
        kryonetServer = new Server() {
            @Override
            public Connection newConnection() {
                return new ConnectedClient();
            }
        };

        NetworkHelper.KryonetRegisterClasses(kryonetServer);
        NetworkHelper.KryonetRegisterClasses(kryonetClient);

        kryonetServer.addListener(new NetworkEngine.ServerListener());
        kryonetClient.addListener(new NetworkEngine.ClientListener());
    }

    public void startServer() throws IOException, RuntimeException {
        if (netRole != NetworkRole.Local)
            throw new RuntimeException("Tried to start server while another network exists");

        kryonetServer.bind(TCPPort, UDPPort);
        kryonetServer.start();

        netRole = NetworkRole.Server;
        netStatus = NetworkStatus.Ready;
    }

    public void tryStartClient(String serverIP) throws RuntimeException {
        if (netRole != NetworkRole.Local)
            throw new RuntimeException("Tried to start server while another network exists");

        kryonetClient.stop();
        kryonetClient.start();

        netStatus = NetworkStatus.ConnectingToServer;
        threadPool.submit(() -> {
            try {
                kryonetClient.connect(ConnectionTimeoutInMilli, serverIP, TCPPort, UDPPort);
            } catch (IOException e) {
                e.printStackTrace();
                netStatus = NetworkStatus.ConnectionFailed;
                kryonetClient.stop();
            }
        });
    }

    public void stopNetwork() {
        switch (netRole) {
            case Server -> kryonetServer.stop();
            case Client, Local -> kryonetClient.stop();
        }
        netRole = NetworkRole.Local;
        netStatus = NetworkStatus.Ready;
    }

    public NetworkRole getNetRole() { return netRole; }

    public boolean isLocalOrServer() { return netRole != NetworkRole.Client; }

    public NetworkStatus getNetStatus() { return netStatus; }

    public void clearStatus() {
        if (netRole == NetworkRole.Local && (netStatus == NetworkStatus.ConnectionFailed || netStatus == NetworkStatus.GotDisconnectedAsClient)) {
            synchronized (networkLock) {
                netStatus = NetworkStatus.Ready;
            }
        }
    }

    // Information Dump methods

    /** Dumps messages received since last call */
    public LinkedList<NetworkMessage> dumpMessages() {
        LinkedList<NetworkMessage> recent;
        synchronized (networkLock) {
            recent = new LinkedList<>(networkMessages);
            networkMessages.clear();
        }
        return recent;
    }

    /** Dumps a list of connection identifiers that were added to the server since last call */
    public ArrayList<Integer> dumpConnectedClients() {
        ArrayList<Integer> recent;
        synchronized (networkLock) {
            recent = new ArrayList<>(recentlyConnected);
            recentlyConnected.clear();
        }
        return recent;
    }

    /** Dumps a list of connection identifiers that were removed from the server since last call */
    public ArrayList<Integer> dumpDisconnectedClients() {
        ArrayList<Integer> recent;
        synchronized (networkLock) {
            recent = new ArrayList<>(recentlyDisconnected);
            recentlyDisconnected.clear();
        }
        return recent;
    }

    // Message sending

    public boolean sendPacketToServer(Packet packet, boolean isVIP) {
        if (netRole != NetworkRole.Client) return false;

        if (isVIP) {
            kryonetClient.sendTCP(packet);
        }
        else {
            kryonetClient.sendUDP(packet);
        }

        return true;
    }

    public boolean sendPacketToClient(int connectionID, Packet packet, boolean isVIP) {
        if (netRole != NetworkRole.Server) return false;

        Connection which = null;
        for (var connection: kryonetServer.getConnections()) {
            if (connection.getID() == connectionID) {
                which = connection;
                break;
            }
        }

        if (which == null) return false;

        if (isVIP) {
            kryonetServer.sendToTCP(connectionID, packet);
        }
        else {
            kryonetServer.sendToUDP(connectionID, packet);
        }
        return true;
    }

    public void sendPacketToAllClients(Packet packet, boolean isVIP) {
        if (isVIP) {
            kryonetServer.sendToAllTCP(packet);
        }
        else {
            kryonetServer.sendToAllUDP(packet);
        }
    }

    // Other

    public void update() {

    }

    public void cleanup() {
        kryonetClient.stop();
        kryonetServer.stop();
        try {
            kryonetClient.dispose();
        }
        catch (IOException ignored) {}
        try {
            kryonetServer.dispose();
        }
        catch (IOException ignored) {}
        threadPool.shutdown();
    }
}
