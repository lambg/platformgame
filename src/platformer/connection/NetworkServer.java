package platformer.connection;

import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetworkServer extends Communicator implements AutoCloseable {
    private Map<Socket, PlayerEntity> connectedPlayers = new HashMap<>();
    private ServerSocket socket;
    private World world = new World();

    // todo - send update packets to client
    // todo - cannot handle directly in world/worldobj classes (otherwise client will try and send update
    // todo - packets to server when packets received

    public NetworkServer(int port) throws IOException {
        socket = new ServerSocket(port);

        new Thread(() -> {
            while (true) {
                try {
                    acceptConnection(socket.accept());
                } catch (IOException ex) {
                    // exception thrown when socket is closed; this is valid, so do nothing
                }
            }
        }).start();
    }

    public NetworkServer() throws IOException {
        this(Connection.PORT);
    }

    @Override
    public void update() {
        super.update();

        // update segments around connected players
        world.updateAround(connectedPlayers.values());
    }

    public void acceptConnection(Socket connection) {
        listenTo(connection);
        System.out.println(connection.getInetAddress() + " established connection.");
    }

    public void removeConnection(Socket connection) {
        stopListeningTo(connection);
        connectedPlayers.remove(connection);
    }

    public boolean isClosed() {
        return socket == null;
    }

    public PlayerEntity getPlayerOf(Socket socket) {
        return connectedPlayers.get(socket);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void close() throws Exception {
        socket.close();

        socket = null;
    }
}
