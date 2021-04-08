package ru.kyper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileData {

    public static final int MAX_LENGTH = 1024 * 64;
    public static final int SEND = 0;
    public static final int RECEIVE = 1;

    int commandType;
    String fileName;
    byte[] data;
    int messageNumber;

    public FileData(String fileName, byte[] data, int messageNumber, int commandType) {
        this.commandType = commandType;
        this.fileName = fileName;
        this.data = data;
        this.messageNumber = messageNumber;
    }

    public void writeFileData(String directory) throws IOException {
        File file = new File(directory, this.fileName);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create file: " + file);
        }
        try (FileOutputStream out = new FileOutputStream(file, this.messageNumber > 0)){
            out.write(this.data);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public int getCommandType() {
        return commandType;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

}
