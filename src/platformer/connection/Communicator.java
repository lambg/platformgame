package platformer.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public abstract class Communicator {
    private List<Socket> updaters = new ArrayList<>();

    public void applyUpdatePacket(Packet packet, Socket socket) throws Exception {
        packet.applyPacket(this, socket);
    }

    public void sendPacket(Socket socket, Packet packet) throws IOException {
        try {
            if (socket != null) // else communicator is closed
                packet.send(socket.getOutputStream());
            else System.out.println("Failed to send packet " + packet.getIdentifier() + "; socket closed");
        } catch(SocketException ex) {
            disconnect(socket);
        }
    }

    protected abstract void disconnect(Socket socket) throws IOException;

    public void listenTo(Socket socket) {
        updaters.add(socket);
    }

    public void stopListeningTo(Socket socket) {
        updaters.remove(socket);
    }

    public void update() {
        for (int i = updaters.size() - 1; i >= 0; i--) {
            try {
                Socket updater = updaters.get(i);
                while (updater.getInputStream().available() != 0) {
                    applyUpdatePacket(Packet.build(updater.getInputStream()), updater);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex); // don't try and handle; packets have varying size, no way to correct
            }
        }
    }
}
