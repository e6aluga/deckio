package com.example.dddeck;

import java.io.IOException;
import java.nio.file.*;

public class BackupManager {

        public void backupSaveFromPC(String path, String dirName){
        Path sourceDir = Paths.get(path);
        Path targetDir = Paths.get(String.format("backups/" + App.timestamp() + "%s/", dirName));

        try {
            App.copyDirectory(sourceDir, targetDir);
            System.out.println(App.timestamp() + "Directory copied successfully!");
        } catch (IOException e) {
            System.err.println(App.timestamp() + "Failed to copy directory: " + e.getMessage());
        }
    }

}
