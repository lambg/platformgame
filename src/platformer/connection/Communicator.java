package platformer.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Communicator {
    private List<Socket> updaters = new ArrayList<>();

    public void applyUpdatePacket(Packet packet) {
        packet.applyPacket(this);
    }

    public void sendPacket(Socket socket, Packet packet) throws IOException {
        packet.send(socket.getOutputStream());
    }

    public void listenTo(Socket socket) {
        updaters.add(socket);
    }

    public void stopListeningTo(Socket socket) {
        updaters.remove(socket);
    }

    public void update() { // todo - make sure update is called after Server/Client is created
        for (Socket updater : updaters) {
            try {
                while (updater.getInputStream().available() != 0) {
                    applyUpdatePacket(Packet.build(updater.getInputStream()));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
