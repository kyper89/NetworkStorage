import Handlers.CommandHandler;
import Handlers.FileHandler;
import Handlers.NetworkHandler;

import java.io.IOException;
import java.nio.file.Paths;

public class ClientApp {
    private static final int PORT = 9000;
    private static final String HOSTNAME = "localhost";
    private static final String DIRECTORY = "E:\\Network_Storage";

    public static void main(String[] args) {
        System.out.println("Client started");
        new Thread(new CommandHandler()).start();
        try (NetworkHandler networkHandler = new NetworkHandler()){
            new Thread(new FileHandler(Paths.get(DIRECTORY))).start();
            networkHandler.start(HOSTNAME, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}