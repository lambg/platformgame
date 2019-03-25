package platformer.connection;

import platformer.connection.packets.PlayerConnectPacket;
import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkServer extends Communicator implements AutoCloseable {
    private ServerSocket socket;
    private List<PlayerEntity> connectedPlayers = new ArrayList<>();
    private World world = new World();

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
        world.updateAround(connectedPlayers);
    }

    public void acceptConnection(Socket connection) {
        listenTo(connection);
        System.out.println(connection.getInetAddress() + " connected.");
        // todo - send confirmation packet
    }

    public void removeConnection(Socket connection) {
        stopListeningTo(connection);
        // todo - send disconnect packet
    }

    public boolean isClosed() {
        return socket == null;
    }

    @Override
    public void close() throws Exception {
        socket.close();

        socket = null;
    }
}
