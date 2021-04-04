package Handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class NetworkHandler {

    public NetworkHandler(String hostname, int port) throws IOException {
        this.channel = SocketChannel.open(new InetSocketAddress(hostname, port));
    }

    private final SocketChannel channel;

    public void send(String msg) throws IOException {
        channel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    public void close() throws IOException {
        channel.close();
    }
}
