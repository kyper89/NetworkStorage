import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        new ClientApp().start();
    }

    public void start() {
        System.out.println("Client started");
        try {
            SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost",9000));
            ByteBuffer byteBuffer = ByteBuffer.allocate(256);
            Scanner scanner = new Scanner(System.in);
            while (true) {

                System.out.println("Inter file name:");
                Path path = Paths.get(scanner.next());
                if (!Files.exists(path)) {
                    System.out.println("File is not exists!");
                    continue;
                }

                channel.write(ByteBuffer.wrap(Files.readAllBytes(path)));

                channel.read(byteBuffer);
                String message = new String(byteBuffer.array());
                System.out.println("New message: " + message);
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}