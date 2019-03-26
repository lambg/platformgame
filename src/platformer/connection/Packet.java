package platformer.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class Packet {
    protected abstract void breakdown(OutputStream out) throws IOException;

    protected abstract void buildPacket(InputStream in) throws Exception;

    public String getIdentifier() {
        return getClass().getSimpleName();
    }

    public abstract void applyPacket(Communicator communicator, Socket socket) throws Exception;

    public void send(OutputStream out) throws IOException {
        out.write(getIdentifier().length());
        out.write(getIdentifier().getBytes());
        breakdown(out);
    }

    public static Packet build(InputStream in) throws IOException {
        int idLen = in.read();
        byte[] bytes = new byte[idLen];
        if (in.read(bytes) != idLen)
            throw new IOException("Expected " + idLen + " bytes for packet name; not received");

        try {
            //noinspection unchecked
            Class<? extends Packet> packetCl = (Class<? extends Packet>) Class.forName("platformer.connection.packets." + new String(bytes));
            Packet packet = packetCl.newInstance();
            try {
                packet.buildPacket(in);
            } catch (Exception ex) {
                throw new RuntimeException("Packet failed to build", ex);
            }
            return packet;
        } catch (InstantiationException | IllegalAccessException ex) {
            // packet constructor threw exception or packet does not have default constructor
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new IOException("Packet not found", ex);
        }
    }
}
