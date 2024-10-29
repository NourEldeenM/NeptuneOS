package org.os;

import java.io.File;
import java.io.IOException;

public class ebaa {
    public void mkdirCommand(String dirName){
        File directory = new File(dirName);
        if (directory.exists()){
            System.out.println("Directory already exists");
        }
        else{
            boolean created = directory.mkdir();
            if (created) System.out.println("Directory " + dirName + " is created successfully");
            else System.out.println("Failed to create directory");
        }
    }
    public void touchCommand(String fileName) {
        File file = new File(fileName);
        try {
            if (file.exists()) {
                boolean modified = file.setLastModified(System.currentTimeMillis());
                if (modified) {
                    System.out.println("Updated the last modified time of " + fileName);
                } else {
                    System.out.println("Failed to update the last modified time of " + fileName);
                }
            } else {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("File " + fileName + " is created successfully");
                } else {
                    System.out.println("Failed to create file " + fileName);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


}
