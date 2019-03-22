package platformer.connection.packet;

import platformer.connection.Communicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
    private static final Map<Integer, Class<? extends Packet>> idToPacketMap = new HashMap<>();

    public void allowPacketDecoding() {
        idToPacketMap.put(getId(), getClass());
    }

    protected abstract void breakdown(OutputStream out);

    protected abstract void buildPacket(InputStream in);

    protected abstract int getId();

    public abstract void applyPacket(Communicator communicator);

    public void send(OutputStream out) throws IOException {
        out.write(getId());
        breakdown(out);
    }

    public static Class<? extends Packet> getFromId(int id) {
        return idToPacketMap.get(id);
    }

    public static Packet build(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        if (in.read(bytes) != 4)
            throw new IOException("Expected 4 byte id, not received");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Class<? extends Packet> packetCl = getFromId(buffer.getInt());

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
