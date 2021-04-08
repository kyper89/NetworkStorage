package Handlers;

import com.google.gson.Gson;
import ru.kyper.FileData;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;


public class FileHandler implements Runnable{

    private final Path directoryPath;

    public FileHandler(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.out.println("Failed to create root directory.");
                e.printStackTrace();
            }
        }
        this.directoryPath = path;
    }

    private void readAndSendFile(Path filePath) throws IOException {
        if (Files.exists(filePath)) {

            Gson gson = new Gson();
            try (FileInputStream in = new FileInputStream(filePath.toFile())) {
                int messageNumber = 0;
                int length;
                do {
                    FileData fileData = new FileData(filePath.getFileName().toString(), in.readNBytes(FileData.MAX_LENGTH), messageNumber, FileData.SEND);
                    System.out.println("длина gson = " + gson.toJson(fileData).length());
                    NetworkHandler.send(gson.toJson(fileData));
                    length = fileData.getData().length;
                    messageNumber++;
                } while (length == FileData.MAX_LENGTH);
            }
        } else {
            System.out.println("File not exists: " + filePath);
        }
    }

    @Override
    public void run() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
