package com.sstock2005.securenotes;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.showMessageDialog;

@SpringBootApplication
public class Menu {

	public static void main(String[] args) 
    {
		SpringApplication.run(Menu.class, args);
        FlatDarkLaf.setup();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(null);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Menu - Secure Notes");

        JLabel title_label = new JLabel("Secure Notes");
        title_label.setFont(UIManager.getFont("h00.font"));
        title_label.setBounds(150, 100, 440, 50);

        JLabel encryption_key_label = new JLabel("Master Password");
        encryption_key_label.setBounds(50, 165, 400, 50);

        JPasswordField encryption_key_passwordfield = new JPasswordField();
        encryption_key_passwordfield.setHorizontalAlignment(JTextField.CENTER);
        encryption_key_passwordfield.setBounds(50, 200, 400, 50);

        JButton open_button = new JButton("Open Vault");
        open_button.addActionListener(new ActionListener() {  
        public void actionPerformed(ActionEvent e) {  

                String history = Helper.getHistory();

                if (history != null) {
                    int choice = JOptionPane.showConfirmDialog(null, MessageFormat.format("Do you want to open the vault at {0}", history), "Confirm", JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        char[] plaintext = encryption_key_passwordfield.getPassword();

                        if (plaintext.length < 14) {
                            showMessageDialog(null, 
                                "Your vault password must be atleast 14 characters long!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            Encryption.plaintext = plaintext;
                            boolean result = Helper.testVault();

                            if (result) {
                                frame.dispose();
                                new Main().main();
                            } else {
                                for (int i = 0; i < plaintext.length; i++) {
                                    plaintext[i] = 0;
                                }
                                Encryption.plaintext = null;
                                showMessageDialog(null, 
                                    "Vault corrupted or wrong password!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    else {
                        File yourFolder = null;

                        JFileChooser fc = new JFileChooser();

                        fc.setDialogTitle("Choose an existing Vault folder");
                        fc.setCurrentDirectory(new java.io.File("."));
                        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        int returnVal = fc.showSaveDialog(null);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            yourFolder = fc.getSelectedFile();
                        }

                        try {
                            
                            Helper.saveHistory(yourFolder.getPath());
                            history = yourFolder.getPath();
                            
                        } catch (NullPointerException npe) {
                            showMessageDialog(null, 
                            "Could not save directory! If you cancelled the file dialog, please ignore this.",
                            "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            history = null;
                        }

                        if (history != null) {
                            char[] plaintext = encryption_key_passwordfield.getPassword();

                            if (plaintext.length < 14) {
                                showMessageDialog(null, 
                                    "Your vault password must be atleast 14 characters long!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                            else {
                                Encryption.plaintext = plaintext;
                                boolean result = Helper.testVault();

                                if (result) {
                                    frame.dispose();
                                    new Main().main();
                                } else {
                                    for (int i = 0; i < plaintext.length; i++) {
                                        plaintext[i] = 0;
                                    }
                                    Encryption.plaintext = null;
                                    showMessageDialog(null, 
                                        "Vault corrupted or wrong password!",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                } else {
                    File yourFolder = null;

                    JFileChooser fc = new JFileChooser();

                    fc.setDialogTitle("Choose an existing Vault folder");
                    fc.setCurrentDirectory(new java.io.File("."));
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    int returnVal = fc.showSaveDialog(null);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        yourFolder = fc.getSelectedFile();
                    }

                    try {
                            
                        Helper.saveHistory(yourFolder.getPath());
                        history = yourFolder.getPath();
                            
                    } catch (NullPointerException npe) {
                        showMessageDialog(null, 
                        "Could not save directory! If you cancelled the file dialog, please ignore this.",
                        "Error",
                                JOptionPane.ERROR_MESSAGE);
                        history = null;
                    }

                    if (history != null) {
                        char[] plaintext = encryption_key_passwordfield.getPassword();

                        if (plaintext.length < 14) {
                            showMessageDialog(null, 
                                    "Your vault password must be atleast 14 characters long!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else {
                            Encryption.plaintext = plaintext;
                            boolean result = Helper.testVault();

                            if (result) {
                                frame.dispose();
                                new Main().main();
                            } else {
                                for (int i = 0; i < plaintext.length; i++) {
                                    plaintext[i] = 0;
                                }
                                Encryption.plaintext = null;
                                showMessageDialog(null, 
                                    "Vault corrupted or wrong password!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }  
        });

        open_button.setBounds(150, 300, 220, 50);

        JButton create_button = new JButton("Create Vault");
        create_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                char[] plaintext = encryption_key_passwordfield.getPassword();

                if (plaintext.length < 14) {
                    showMessageDialog(null, 
                                    "Your vault password must be atleast 14 characters long!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Encryption.plaintext = plaintext;
                    boolean result = Helper.createVault();

                    if (result) {
                        frame.dispose();
                        new Main().main();
                    } else {
                        for (int i = 0; i < plaintext.length; i++) {
                            plaintext[i] = 0;
                        }
                        Encryption.plaintext = null;
                    }
                }
            }
        });

        create_button.setBounds(150, 370, 220, 50);

        frame.add(title_label);
        frame.add(encryption_key_label);
        frame.add(encryption_key_passwordfield);
        frame.add(open_button);
        frame.add(create_button);

        frame.setVisible(true);
	}

}
