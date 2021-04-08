package Handlers;

import com.google.gson.Gson;
import ru.kyper.FileData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;


public class NetworkHandler implements AutoCloseable {

    private static SocketChannel socketChannel;

    public void start(String hostname, int port) throws IOException {
        Selector selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(hostname, port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_WRITE);
        socketChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Gson gson = new Gson();
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                StringBuilder sb = new StringBuilder();
                int i = 0, j = 0;
                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    client.read(buffer);

                    do {
                        for (int k = 0; k < buffer.position(); k++) {
                            char ch = (char) buffer.get(k);
                            sb.append(ch);
                            switch (ch) {
                                case '{' -> i++;
                                case '}' -> j++;
                            }
                        }
                        buffer.clear();
                        client.read(buffer);
                    } while (i != j );

                    System.out.println("sb.length = " + sb.length());

                    FileData fileData = gson.fromJson(sb.toString(), FileData.class);
                    switch (fileData.getCommandType()) {
                        case 0 -> fileData.writeFileData("E:\\Network_Storage");
                        case 1 -> System.out.println("File accepted: " + fileData.getFileName());
                    }

                    buffer.clear();
                }
                iterator.remove();
            }
        }
    }

    public static void send(String msg) throws IOException {
        socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    public void close() throws IOException {
        socketChannel.close();
    }

}
