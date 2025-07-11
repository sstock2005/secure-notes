package com.sstock2005.demo;

import javax.swing.JButton;
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
                System.out.println("Open button was clicked!");
            }  
        });
        open_button.setBounds(150, 300, 220, 50);

        JButton create_button = new JButton("Create Vault");
        create_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Create button was clicked!");

                char[] plaintext = encryption_key_passwordfield.getPassword();

                if (plaintext.length < 14) {
                    showMessageDialog(null, 
                                    "Your vault password must be atleast 14 characters long!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                }
                else {
                    boolean result = Helper.createVault(Encryption.md5(plaintext));

                    for (int i = 0; i < plaintext.length; i++) {
                        plaintext[i] = 0;
                    }

                    System.out.println(plaintext);
                    
                    System.out.println(Helper.getHistory());

                    if (result) {
                        frame.dispose();
                        new Main().main();
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
