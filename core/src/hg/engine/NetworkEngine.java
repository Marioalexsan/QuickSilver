package hg.engine;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import hg.networking.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkEngine {
    private class ClientListener extends Listener {
        @Override
        public void connected(Connection connection) {
            connection.sendTCP(new Packets.ClientInitRequest());
        }

        @Override
        public void disconnected(Connection connection) {
            stopNetwork();
        }

        @Override
        public void received(Connection connection, Object object) {
            synchronized (messageLock) {
                receivedMessages.add(object);
            }
        }
    }

    private class ServerListener extends Listener {
        @Override
        public void connected(Connection connection) {
            if (!connectionDenyReason.equals("")) {
                var msg = new Packets.ConnectionDenied();
                msg.reason = connectionDenyReason;
                connection.sendTCP(msg);
                connection.close();
            }
        }

        @Override
        public void disconnected(Connection connection) {

        }

        @Override
        public void received(Connection connection, Object object) {
            ConnectedClient client = (ConnectedClient) connection;
            synchronized (messageLock) {
                receivedMessages.add(object);
            }
        }
    }

    public static final int ConnectionTimeoutInMilli = 2500;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private NetworkRole netRole = NetworkRole.Local;
    private NetworkStatus netStatus = NetworkStatus.NotStarted;

    private int TCPPort = 52735;
    private int UDPPort = 52216;

    private final Server server;
    private final Client client;

    private String connectionDenyReason = "";

    private final Object messageLock = new Object();
    private final ArrayList<Object> receivedMessages = new ArrayList<>();

    public NetworkEngine() {
        client = new Client();
        server = new Server() {
            @Override
            public Connection newConnection() {
                return new ConnectedClient();
            }
        };

        NetworkHelper.KryonetRegisterClasses(server);
        NetworkHelper.KryonetRegisterClasses(client);

        server.addListener(new NetworkEngine.ServerListener());
        client.addListener(new NetworkEngine.ClientListener());
    }

    public void startServer() throws IOException, RuntimeException {
        if (netRole != NetworkRole.Local)
            throw new RuntimeException("Tried to start server while another network exists");

        server.bind(TCPPort, UDPPort);
        server.start();

        netRole = NetworkRole.Server;
        netStatus = NetworkStatus.Ready;
    }

    public void stopNetwork() throws RuntimeException {
        switch (netRole) {
            case Server -> server.stop();
            case Client, Local -> client.stop();
        }
        netRole = NetworkRole.Local;
        netStatus = NetworkStatus.NotStarted;
    }

    public void tryStartClient(String serverIP) throws RuntimeException {
        if (netRole != NetworkRole.Local)
            throw new RuntimeException("Tried to start server while another network exists");

        client.stop();
        client.start();

        netStatus = NetworkStatus.ConnectingToServer;
        threadPool.submit(() -> {
            try {
                client.connect(ConnectionTimeoutInMilli, serverIP, TCPPort, UDPPort);
                netRole = NetworkRole.Client;
                netStatus = NetworkStatus.Ready;
            } catch (IOException e) {
                e.printStackTrace();
                netStatus = NetworkStatus.ConnectionFailed;
                client.stop();
            }
        });
    }

    public NetworkRole getCurrentRole() {
        return netRole;
    }

    public NetworkStatus getNetStatus() {
        return netStatus;
    }

    /** Sets the connection deny reason for server. If the reason is not equal to the empty string,
     * clients trying to connect will receive a ConnectionDenied packet and will be disconnected  */
    public void setConnectionDenyReason(String reason) {
        connectionDenyReason = reason;
    }

    public boolean isLocalOrServer() {
        return netRole != NetworkRole.Client;
    }

    private ArrayList<Object> getPendingMessages() {
        ArrayList<Object> currentMessages;
        synchronized (messageLock) {
            currentMessages = new ArrayList<>(receivedMessages);
            receivedMessages.clear();
        }
        return currentMessages;
    }

    public void update() {
        if (netRole == NetworkRole.Server) updateAsServer();
        else if (netRole == NetworkRole.Client) updateAsClient();
    }

    private void updateAsServer() {
    }

    private void updateAsClient() {

    }

    public void cleanup() {
        client.stop();
        server.stop();
        try {
            client.dispose();
        }
        catch (IOException ignored) {}
        try {
            server.dispose();
        }
        catch (IOException ignored) {}
    }
}
