package Handlers;

import com.google.gson.Gson;
import ru.kyper.FileData;

import java.io.IOException;
import java.nio.file.*;

public class FileHandler {

    private final Path directoryPath;
    private final NetworkHandler networkHandler;

    public FileHandler(Path path, NetworkHandler networkHandler) {
        this.directoryPath = path;
        this.networkHandler = networkHandler;
    }

    public void listen() throws IOException, InterruptedException {

        WatchService watchService = FileSystems.getDefault().newWatchService();

        this.directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        boolean poll = true;
        while (poll) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                readAndSendFile(Paths.get(directoryPath.toString() + "\\" + event.context()));
            }
            poll = key.reset();
        }
    }

    private void readAndSendFile(Path filePath) throws IOException {
        if (filePath.toFile().exists()) {
            Gson gson = new Gson();
            FileData fileData = new FileData(filePath);
            System.out.println(gson.toJson(fileData));
            System.out.println(gson.toJson(fileData).length());
            networkHandler.send(gson.toJson(fileData));
        } else {
            System.out.println("File not exists: " + filePath);
        }
    }

}
