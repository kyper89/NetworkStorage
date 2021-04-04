package ru.kyper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;


public class FileData {

    String fileName;
    byte[] data;

    public FileData(Path filePath) throws IOException {
        this.fileName = filePath.getFileName().toString();
        this.data = new FileInputStream(filePath.toString()).readAllBytes();
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }
}
