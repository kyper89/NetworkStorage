import com.google.gson.Gson;
import ru.kyper.FileData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerApp {
    private static final int PORT = 9000;
    private static final String HOSTNAME = "localhost";
    private static final String DIRECTORY = "D:\\Network_Storage";

    public static void main(String[] args) throws IOException {
        new ServerApp().start();
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(HOSTNAME, PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started");

        while (true) {
            selector.select();
            System.out.println("New selector event");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event");
                    register(selector, serverSocket);
                }
                if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event");
                    readMessage(selectionKey);
                }
                iterator.remove();
            }
        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("New client is connected");
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        try {
            client.read(byteBuffer);
            String msg = new String(byteBuffer.array(), 0, byteBuffer.position() );
            FileData fileData = new Gson().fromJson(msg, FileData.class);
            System.out.println("New message: " + fileData.getFileName());
            FileOutputStream out = new FileOutputStream(DIRECTORY + "\\" + fileData.getFileName());
            out.write(fileData.getData());
        } catch (SocketException e) {
            client.close();
            e.printStackTrace();
        }

    }

}