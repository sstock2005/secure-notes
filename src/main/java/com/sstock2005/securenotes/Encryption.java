package com.sstock2005.securenotes;

import static javax.swing.JOptionPane.showMessageDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

public class Encryption {

    public static char[] plaintext = null;

    public static String decryptString(SecretKey key, String input) {
        try {

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] combined = Base64.getDecoder().decode(input);

            ByteArrayInputStream reader = new ByteArrayInputStream(combined);
            
            byte[] ivbytes = new byte[12]; 

            reader.read(ivbytes);

            reader.close();
            
            GCMParameterSpec iv = new GCMParameterSpec(128, ivbytes);

            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            combined = Arrays.copyOfRange(combined, 12, combined.length);
            
            byte[] plainText = cipher.doFinal(combined);

            return new String(plainText);

        } catch (NoSuchAlgorithmException nsae) {
            return null;
        } catch (NoSuchPaddingException nspe) {
            return null;
        } catch (InvalidKeyException ike) {
            return null; // maybe alert user?
        } catch (InvalidAlgorithmParameterException iape) {
            return null;
        }  catch (IllegalBlockSizeException ibse) {
            return null;
        } catch (BadPaddingException bpe) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
    }

    public static String encryptString(SecretKey key, String input) {

        try {
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec iv = generateIV();

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            
            byte[] cipherText = cipher.doFinal(input.getBytes());

            ByteArrayOutputStream cipherTextFinal = new ByteArrayOutputStream();
            
            cipherTextFinal.write(iv.getIV());
            cipherTextFinal.write(cipherText);

            return Base64.getEncoder().encodeToString(cipherTextFinal.toByteArray());

        } catch (NoSuchAlgorithmException nsae) {
            return null;
        } catch (NoSuchPaddingException nspe) {
            return null;
        } catch (InvalidKeyException ike) {
            return null; // maybe alert user?
        } catch (InvalidAlgorithmParameterException iape) {
            return null;
        }  catch (IllegalBlockSizeException ibse) {
            return null;
        } catch (BadPaddingException bpe) {
            return null;
        } catch (IOException ioe) {
            return null;
        }
        
    }

    public static boolean saveNote(String notepath, String content) {
        try{
            
            if (!isPasswordSet()) {
                return false;
            }

            String directory = Helper.getHistory();

            Path path = Paths.get(directory + "/.enc");

            if (!Files.exists(path)) {
                return false;
            }

            Scanner scan = new Scanner(new File(directory + "/.enc"));

            String salt = scan.nextLine();

            scan.close();

            SecretKey key = generateKey(salt);

            if (key == null) {
                return false;
            }

            String encrypted = encryptString(key, content);

            if (encrypted == null) {
                return false;
            }

            FileWriter filewriter = new FileWriter(notepath);

            filewriter.write(encrypted);
            filewriter.close();

            return true;

        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (IOException ioe) {
            return false;
        }
    }

    public static String readNote(String notepath) {
        try {
            
            if (!isPasswordSet()) {
                return "Error: Password not set";
            }

            String directory = Helper.getHistory();

            Path path = Paths.get(directory + "/.enc");

            if (!Files.exists(path)) {
                return "Error: Vault file not found.";
            }

            Scanner scan = new Scanner(new File(directory + "/.enc"));

            String salt = scan.nextLine();

            scan.close();

            SecretKey key = generateKey(salt);

            if (key == null) {
                return "Error: Could not generate decryption key";
            }

            byte[] fileBytes = Files.readAllBytes(Paths.get(notepath));
            String encrypted = new String(fileBytes).trim();

            if (encrypted.isEmpty()|| encrypted.isBlank()) {
                return encrypted;
            }

            String content = decryptString(key, encrypted);
            
            if (content == null) {
                return "Error: Decryption failed";
            }
            
            return content;

        } catch (FileNotFoundException fnfe) {
            return "Error: File not found";
        } catch (IOException ioe) {
            return "Error: IOException";
        }
    }

    public static boolean decryptFile(SecretKey key, File inputFile, File outputFile) {
        try {

            FileInputStream inputStream = new FileInputStream(inputFile);

            byte[] ivbytes = new byte[12];

            int ivBytesRead = inputStream.read(ivbytes);
            if (ivBytesRead != 12) {
                inputStream.close();
                return false;
            }

            GCMParameterSpec iv = new GCMParameterSpec(128, ivbytes);
            
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[64];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                byte[] output = cipher.update(buffer, 0, bytesRead);

                if (output != null) {
                    outputStream.write(output);
                }
            }

            byte[] outputBytes = cipher.doFinal();

            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }

            inputStream.close();
            outputStream.close();

            return true;

        } catch (NoSuchAlgorithmException nsae) {
            return false;
        } catch (NoSuchPaddingException nspe) {
            return false;
        } catch (InvalidKeyException ike) {
            return false; // maybe alert user?
        } catch (InvalidAlgorithmParameterException iape) {
            return false;
        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (IOException ioe) {
            return false;
        }  catch (IllegalBlockSizeException ibse) {
            return false;
        } catch (BadPaddingException bpe) {
            return false;
        }
    }

    // https://www.baeldung.com/java-aes-encryption-decryption
    public static boolean encryptFile(SecretKey key, File inputFile, File outputFile) {

        try {

            GCMParameterSpec iv = generateIV();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            FileInputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            outputStream.write(iv.getIV());

            byte[] buffer = new byte[64];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                byte[] output = cipher.update(buffer, 0, bytesRead);

                if (output != null) {
                    outputStream.write(output);
                }
            }

            byte[] outputBytes = cipher.doFinal();

            if (outputBytes != null) {
                outputStream.write(outputBytes);
            }

            inputStream.close();
            outputStream.close();

            return true;

        } catch (NoSuchAlgorithmException nsae) {
            return false;
        } catch (NoSuchPaddingException nspe) {
            return false;
        } catch (InvalidKeyException ike) {
            return false; // maybe alert user?
        } catch (InvalidAlgorithmParameterException iape) {
            return false;
        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (IOException ioe) {
            return false;
        } catch (IllegalBlockSizeException ibse) {
            return false;
        } catch (BadPaddingException bpe) {
            return false;
        }
    }

    // https://www.baeldung.com/java-aes-encryption-decryption
    private static GCMParameterSpec generateIV() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
    }

    // https://www.baeldung.com/java-aes-encryption-decryption
    private static SecretKey generateKey(String salt) {
        try {
            
            if (!isPasswordSet()) {
                throw new IllegalStateException("Password not set - cannot generate encryption key");
            }
            
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            PBEKeySpec s = new PBEKeySpec(plaintext, salt.getBytes(), 65536, 256);

            SecretKey key = new SecretKeySpec(f.generateSecret(s).getEncoded(), "AES");

            return key;

        } catch (NoSuchAlgorithmException nsae) {

            return null;

        } catch (InvalidKeySpecException ikse) {

            return null;

        } catch (IllegalStateException ise) {

            return null;

        }
    }

    // https://www.geeksforgeeks.org/java/generate-random-string-of-given-size-in-java/
    public static String generateSalt() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

        StringBuilder bob = new StringBuilder(32);

        for (int i = 0; i < 32; i++) {
            int index = (int)(characters.length() * Math.random());
            bob.append(characters.charAt(index));
        }

        return md5(bob.toString());
    }

    // https://www.geeksforgeeks.org/java/md5-hash-in-java/
    private static String md5(String text) {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(text.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hash = no.toString(16);

            while (hash.length() < 32) {
                hash = "0" + hash;
            }

            return hash;

        } catch (NoSuchAlgorithmException nsae) {
            return null; // silently fail
        }
    }

    public static boolean testVaultFile() {

        try {
            
            if (!isPasswordSet()) {
                return false;
            }

            String directory = Helper.getHistory();

            Path path = Paths.get(directory + "/.enc");

            if (!Files.exists(path)) {
                return false;
            }

            Scanner scan = new Scanner(new File(directory + "/.enc"));

            String salt = scan.nextLine();

            String testString = "Is it secret? Is it safe?";

            String testInputString = decryptString(generateKey(salt), scan.nextLine());

            scan.close();

            if (testInputString == null) {
                return false;
            }

            if (testInputString.compareTo(testString) == 0) {
                return true;
            } else {
                return false;
            }

        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }

    }

    public static void createVaultFile() {
        
        if (!isPasswordSet()) {
            showMessageDialog(null, 
                    "Password must be set before creating vault!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String salt = generateSalt();

        String directory = Helper.getHistory();

        try {

            Path path = Paths.get(directory + "/.enc");

            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            
            String testString = encryptString(generateKey(salt), "Is it secret? Is it safe?");

            if (testString == null) {
                showMessageDialog(null, 
                        "Failed to encrypt test string!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            FileWriter writer = new FileWriter(directory + "/.enc");
            writer.write(salt + "\n");
            writer.write(testString);
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

    // Helper method to check if password is set
    private static boolean isPasswordSet() {
        return plaintext != null && plaintext.length > 0;
    }
}
