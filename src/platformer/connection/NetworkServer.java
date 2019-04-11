package platformer.connection;

import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.world.World;
import platformer.world.WorldObj;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NetworkServer extends Communicator implements AutoCloseable {
    public final Map<Socket, PlayerEntity> connectedPlayers = new HashMap<>(); // should not be public, but this is easier
    private ServerSocket socket;
    private World world = new World((int) (Math.random() * Integer.MAX_VALUE));
    private int nextObjectId = 1;
    private List<Socket> removeSockets = new ArrayList<>();

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

    public int getNextObjectId() {
        return nextObjectId++;
    }

    @Override
    public void update() {
        super.update();

        // update segments around connected players
        world.updateAround(connectedPlayers.values());
    }

    public void sendPacketToAll(Packet packet, Socket... exclude) {
        List<Socket> excludeL = Arrays.asList(exclude);
        for (Socket socket : connectedPlayers.keySet()) {
            if (!excludeL.contains(socket)) {
                try {
                    sendPacket(socket, packet);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        for(int i = removeSockets.size() - 1; i >= 0; i--) {
            try {
                removeConnection(removeSockets.remove(i));
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void acceptConnection(Socket connection) {
        listenTo(connection);
        System.out.println(connection.getInetAddress() + " established connection.");
    }

    @Override
    protected void disconnect(Socket socket) {
        removeSockets.add(socket);
    }

    public void removeConnection(Socket connection) {
        stopListeningTo(connection);
        WorldObj obj = connectedPlayers.remove(connection);
        obj.getWorld().removeObjectFromWorld(obj);
        sendPacketToAll(new ObjectDeSpawnPacket(obj.getObjectId()));
    }

    public boolean isClosed() {
        return socket == null;
    }

    public PlayerEntity getPlayerOf(Socket socket) {
        return connectedPlayers.get(socket);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void close() throws Exception {
        socket.close();

        socket = null;
    }
}
