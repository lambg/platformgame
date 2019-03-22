package platformer.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer extends Communicator implements AutoCloseable {
    private ServerSocket socket;

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

    public void acceptConnection(Socket connection) {
        listenTo(connection);
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
