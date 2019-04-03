package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PlayerConfirmConnectPacket extends Packet {
    private int playerId;
    private int worldSeed;

    public PlayerConfirmConnectPacket(int playerId, int worldSeed) {
        this.playerId = playerId;
        this.worldSeed = worldSeed;
    }

    // called reflectively
    public PlayerConfirmConnectPacket() {
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        GameUtil.write(out, playerId);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException {
        playerId = GameUtil.readInt(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        MainClient.WORLD = new World(worldSeed);
        MainClient.PLAYER_ID = playerId;
    }
}
