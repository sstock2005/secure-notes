package com.sstock2005.demo;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

public class Helper {

    // https://stackoverflow.com/a/30246380
    public static boolean createVault(String hash) {

        File yourFolder = null;

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(new java.io.File("."));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            yourFolder = fc.getSelectedFile();
        }

        try {
            
            saveHistory(yourFolder.getPath());
            

        } catch (NullPointerException npe) {
            showMessageDialog(null, 
            "Could not save directory! If you cancelled the file dialog, please ignore this.",
              "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        

        Encryption enc = new Encryption(hash);
        
        enc.createTestFile();

        return true;
    }

    public static String getHistory() {
        try {

            Path path = Paths.get(".cache");

            if (!Files.exists(path)) {
                return null;
            }

            File cache = new File(".cache");

            Scanner scan = new Scanner(cache);

            if (!scan.hasNextLine()) {
                scan.close();
                return null; // file is empty
            }

            String location = scan.nextLine();

            scan.close();

            return location;

         } catch (IOException ioe) {
            return null; // silently fail
        }
    }

    private static void saveHistory(String location) {

        try {

            Path path = Paths.get(".cache");

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            FileWriter writer = new FileWriter(".cache");
            writer.write(location);
            writer.close();

            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }
            
        } catch (IOException ioe) {
            showMessageDialog(null, 
            "Could not create cache file! Make sure this program can read and write in the current directory!",
              "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
