package com.example.dddeck;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BackupManager {

        public void backupSaveFromPC(String path, String dirName){
        Path sourceDir = Paths.get(path);
        Path targetDir = Paths.get(String.format("backups/" + "[PC] " + App.timestamp() + "%s/", dirName));

        try {
            App.copyDirectory(sourceDir, targetDir);
            System.out.println(App.timestamp() + sourceDir + " -> " + targetDir + "\nDirectory copied successfully!");
        } catch (IOException e) {
            System.err.println(App.timestamp() + sourceDir + " -> " + targetDir + "Failed to copy directory: " + e.getMessage());
        }
    }

}
