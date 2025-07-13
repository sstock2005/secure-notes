package com.sstock2005.securenotes;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

public class Helper {

    public static String getNoteFromName(String name) {
        String note_filepath = Helper.getHistory() + "/" + name + ".txt";
        Path note_path = Paths.get(note_filepath);

        if (!Files.exists(note_path)) {
            return "Error: File does not exist.";
        }

        String content = Encryption.readNote(note_filepath);

        return content;
    }
    public static ArrayList<String> getNotes() {

        ArrayList<String> result = new ArrayList<String>();

        File[] files = new File(getHistory()).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName();

                if (name.contains(".enc")) {
                    continue;
                }

                result.add(name.split("\\.")[0]);
            }
        }

        return result;
    }

    public static boolean saveNote(String name, String content) {
        return Encryption.saveNote(Helper.getHistory() + "/" + name + ".txt", content);
    }

    public static boolean createNote(String name) {
        try {

            Path p = Paths.get(Helper.getHistory() + "/" + name + ".txt");

            if (Files.exists(p)) {
                JOptionPane.showMessageDialog(null, name + " already exists!", "Note Already Exists", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                Files.createFile(p);
                return true;
            }

        } catch (IOException ioe) {
            return false;
        }
    }

    public static boolean deleteNote(String name) {
        try {

            Path p = Paths.get(Helper.getHistory() + "/" + name + ".txt");

            if (Files.exists(p)) {
                Files.delete(p);
                return true;
            }
            else {
                return true;
            }

        } catch (IOException ioe) {
            return false;
        }
    }
    // https://stackoverflow.com/a/30246380
    public static boolean createVault() {
        
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
        
        Encryption.createVaultFile();

        return true;
    }

    public static boolean testVault() {

        return Encryption.testVaultFile();
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
                return null;
            }

            String location = scan.nextLine();

            scan.close();

            return location;

         } catch (IOException ioe) {
            return null; // silently fail
        }
    }

    public static void saveHistory(String location) {

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
