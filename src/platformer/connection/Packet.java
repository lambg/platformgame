package platformer.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
    private static final Map<Integer, Class<? extends Packet>> idToPacketMap = new HashMap<>();

    public static void allowPacketDecoding(int id, Class<? extends Packet> cl) { // todo - call this method
        idToPacketMap.put(id, cl);
    }

    protected abstract void breakdown(OutputStream out) throws IOException;

    protected abstract void buildPacket(InputStream in) throws IOException;

    protected abstract byte getId(); // PlayerDisconnect - 6

    public abstract void applyPacket(Communicator communicator);

    public void send(OutputStream out) throws IOException {
        out.write(getId());
        breakdown(out);
    }

    public static Class<? extends Packet> getFromId(int id) {
        return idToPacketMap.get(id);
    }

    public static Packet build(InputStream in) throws IOException {
        int id = in.read();
        Class<? extends Packet> packetCl = getFromId(id);
        if (packetCl == null)
            throw new IOException("Packet cannot be rebuilt; id \""+id+"\" not recognized");

        try {
            Packet packet = packetCl.newInstance();
            packet.buildPacket(in);
            return packet;
        } catch (InstantiationException | IllegalAccessException ex) {
            // packet constructor threw exception or packet does not have default constructor
            throw new RuntimeException(ex);
        }
    }
}
