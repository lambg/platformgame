package platformer.connection;

import platformer.MainClient;
import platformer.world.WorldSegment;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NetworkClient extends Communicator implements AutoCloseable {
    private Socket socket;
    private Collection<WorldSegment> lastLoadedSegments = new ArrayList<>();

    public NetworkClient(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        listenTo(socket); // listen to communication sent by server
    }

    public NetworkClient(String ip) throws IOException {
        this(ip, Connection.PORT);
    }

    @Override
    public void update() {
        super.update();

        if(MainClient.PLAYER != null) { // otherwise world has not been loaded yet
            MainClient.WORLD.transferObjects();

            Collection<WorldSegment> localSegments = MainClient.WORLD.getSegmentsAround(Collections.singleton(MainClient.PLAYER));

            for(WorldSegment currentSegment : localSegments) {
                // segments to be shown this update
                currentSegment.updateShapes();
            }

            // todo - re-add this
//            lastLoadedSegments.removeAll(localSegments);
//
//            for (WorldSegment previousSegment : lastLoadedSegments) {
//                // segments that were shown in the last update, but not in this update
//                previousSegment.hideSegment();
//            }
            lastLoadedSegments = localSegments;
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return socket == null;
    }

    @Override
    public void close() throws IOException {
        socket.close();

        socket = null;
    }
}
