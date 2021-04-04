import Handlers.FileHandler;
import Handlers.NetworkHandler;

import java.io.IOException;
import java.nio.file.Paths;

public class ClientApp {
    private static final int PORT = 9000;
    private static final String HOSTNAME = "localhost";
    private static final String DIRECTORY = "E:\\Network_Storage";

    public static void main(String[] args) {
        new Thread(new SimpleClient()).start();
    }

    static class SimpleClient implements Runnable {

        @Override
        public void run() {
            System.out.println("Client started");
            NetworkHandler networkHandler = null;
            try {
                networkHandler = new NetworkHandler(HOSTNAME, PORT);
                new FileHandler(Paths.get(DIRECTORY), networkHandler).listen();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (networkHandler != null) {
                    try {
                        networkHandler.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}