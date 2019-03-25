package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.WorldObj;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PlayerConfirmConnectPacket extends Packet {
    private int playerId;

    public PlayerConfirmConnectPacket(int playerId) {
        this.playerId = playerId;
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
        MainClient.PLAYER = (PlayerEntity) WorldObj.getObject(playerId);
    }
}
