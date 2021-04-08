package Handlers;

import com.google.gson.Gson;
import ru.kyper.FileData;

import java.io.IOException;
import java.util.Scanner;

public class CommandHandler implements Runnable, AutoCloseable{

    private final Scanner scanner;

    public CommandHandler() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try (this) {
            Gson gson = new Gson();
            while (true) {
                System.out.println("Enter file name");
                String fileName = this.scanner.next();
                FileData fileData = new FileData(fileName, new byte[0], 0, FileData.RECEIVE);
                try {
                    NetworkHandler.send(gson.toJson(fileData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
