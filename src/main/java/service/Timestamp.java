package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public interface Timestamp {
    String RESOURCE_FOLDER_LOGS = System.getProperty("user.dir") + "\\src\\main\\java\\service\\files\\resources\\logs\\Logs.csv";

    static void timestamp(String text) {
        try {
            BufferedWriter LogsReader = new BufferedWriter(new FileWriter(RESOURCE_FOLDER_LOGS, true));
            LogsReader.write(text + "," + LocalDateTime.now() + "\n");
            LogsReader.close();
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in fisierul Logs.csv (" + text + ").");
        }
    }

}
