package platformer.connection.packets;

import platformer.MainServer;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;
import platformer.world.WorldObj;
import platformer.world.WorldSegment;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

public class PlayerConnectPacket extends Packet {
    private String name;

    public PlayerConnectPacket(String name) {
        if (name.length() > Byte.MAX_VALUE)
            throw new IllegalArgumentException("Length cannot be greater than " + Byte.MAX_VALUE);
        this.name = name;
    }

    // called reflectively
    public PlayerConnectPacket() {
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        out.write(name.length());
        for (char c : name.toCharArray())
            out.write(c);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException {
        char[] c = new char[in.read()];
        for (int i = 0; i < c.length; i++)
            c[i] = (char) in.read();
        name = new String(c);
    }

    private void sendObjects(Collection<WorldSegment> segments, Communicator communicator, Socket socket) throws IOException {
        for (WorldSegment segment : segments) {
            for (WorldObj obj : segment.getObjects()) {
                communicator.sendPacket(socket, new ObjectSpawnPacket(obj));
            }
        }
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) throws IOException {
        System.out.println("(TEST) Player " + name + " connected.");
        MainServer.getServer().connectedPlayers.put(socket, null); // connected players must have this socket registered before the packet is sent
        int objId = MainServer.getServer().getNextObjectId();
        communicator.sendPacket(socket, new PlayerConfirmConnectPacket(objId, MainServer.getServer().getWorld().getSeed()));
        PlayerEntity playerEntity = new PlayerEntity(new Location(0, 0), MainServer.getServer().getWorld(), name, objId);
        MainServer.getServer().connectedPlayers.put(socket, playerEntity); // put actual value of player into map

        sendObjects(MainServer.getServer().getWorld().getPositiveLoadedSegments(), communicator, socket);
        sendObjects(MainServer.getServer().getWorld().getNegativeLoadedSegments(), communicator, socket);
    }
}
