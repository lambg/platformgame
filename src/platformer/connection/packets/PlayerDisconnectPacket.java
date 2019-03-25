package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.NetworkServer;
import platformer.connection.Packet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PlayerDisconnectPacket extends Packet {
    @Override
    protected void breakdown(OutputStream out) {
        // no extra data needs to be sent
    }

    @Override
    protected void buildPacket(InputStream in) {
        // no extra data to be received
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        System.out.println("(TEST) Player disconnected.");
        ((NetworkServer) communicator).removeConnection(socket);
    }
}
