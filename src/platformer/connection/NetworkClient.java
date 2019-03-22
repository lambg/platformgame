package platformer.connection;

import java.io.IOException;
import java.net.Socket;

public class NetworkClient extends Communicator implements AutoCloseable{
    private Socket socket;

    public NetworkClient(String ip, int port) throws IOException {
        socket = new Socket(ip,port);
        listenTo(socket); // listen to communication sent by server
    }

    public NetworkClient(String ip) throws IOException {
        this(ip, Connection.PORT);
    }

    public boolean isClosed() {
        return socket == null;
    }

    @Override
    public void close() throws Exception {
        // todo - send disconnect packet
        socket.close();

        socket = null;
    }
}
