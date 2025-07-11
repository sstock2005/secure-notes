package com.sstock2005.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import static javax.swing.JOptionPane.showMessageDialog;

public class Encryption {

    String hash;

    public Encryption() {
        this.hash = null;
    }
    public Encryption(String hash) {
        this.hash = hash;
    }

    public static String md5(char[] plaintext) {
        return "";
    }

    public void createTestFile() {
        String directory = Helper.getHistory();

        try {

            Path path = Paths.get(directory + "/.enctest");

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            FileWriter writer = new FileWriter(directory + "/.enctest");
            writer.write("blablabla"); // encrypt text and save it here to attempt to check later for decryption success with given key
            writer.close();

            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }

        } catch (IOException ioe) {
            showMessageDialog(null, 
                    "Could not initialize vault! Make sure this program can read and write in vault directory!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
