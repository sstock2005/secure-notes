package com.sstock2005.securenotes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import java.awt.event.MouseEvent;

public class Main extends JFrame {

    public void main() {
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (Encryption.plaintext != null) {
                    for (int i = 0; i < Encryption.plaintext.length; i++) {
                        Encryption.plaintext[i] = 0;
                    }
                    Encryption.plaintext = null;
                }
                System.exit(0);
            }
        });
        
        frame.setSize(1280, 720);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Menu - Secure Notes");

        JTextArea contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setFont(UIManager.getFont("large.font"));
        contentArea.setBounds(300, 0, 980, 720);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 300, 720);
        panel.setBackground(Color.GRAY);
        
        JList<Object> list = new JList<Object>(Helper.getNotes().toArray());
        list.setBounds(0, 450, 300, 670);
        list.setFixedCellWidth(250);
        list.setFixedCellHeight(50);
        list.setFont(UIManager.getFont("h3.font"));
        
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {

                @SuppressWarnings("unchecked")
                JList<Object> clickedList = (JList<Object>) event.getSource();
                
                if (event.getClickCount() == 1) {
                    try {
                        int index = clickedList.locationToIndex(event.getPoint());
                        Object selectedItem = clickedList.getModel().getElementAt(index);
                        String text = Helper.getNoteFromName(selectedItem.toString());
                        contentArea.setText(text);
                    } catch (ArrayIndexOutOfBoundsException aioobe) {
                        return; // no notes
                    }
                    
                }
            }
        });

        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();  
        renderer.setHorizontalAlignment(JLabel.CENTER);

        JButton createButton = new JButton("Create Note");
        createButton.setBounds(0, 0, 125, 50);
        createButton.setFont(UIManager.getFont("h3.font"));
        createButton.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                String input = JOptionPane.showInputDialog("Note Name");
                Helper.createNote(input);
                list.setListData(Helper.getNotes().toArray());
            }
        });

        JButton deleteButton = new JButton("Delete Note");
        deleteButton.setBounds(150, 0, 125, 50);
        deleteButton.setFont(UIManager.getFont("h3.font"));
        deleteButton.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) { 
                try {
                    String name = list.getSelectedValue().toString();
                    int cancel = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + name + "?", "Confirm", JOptionPane.CANCEL_OPTION);

                    if (cancel == 0) {
                        Helper.deleteNote(name);
                        list.setListData(Helper.getNotes().toArray());
                        contentArea.setText(""); // Clear content area
                    }
                } catch (NullPointerException npe) {
                    JOptionPane.showMessageDialog(null, "Please select a note to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(UIManager.getFont("h3.font"));
        saveButton.setBounds(0, 450, 200, 100);
        saveButton.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) { 
                try {
                    String name = list.getSelectedValue().toString();
                    String content = contentArea.getText();
                    
                    boolean success = Helper.saveNote(name, content);
                    if (success) {
                        list.setListData(Helper.getNotes().toArray());
                        JOptionPane.showMessageDialog(null, "Note saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to save note. Check your password and try again.", "Save Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NullPointerException npe) {
                    JOptionPane.showMessageDialog(null, "Please select a note to save.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        panel.add(createButton);
        panel.add(deleteButton);
        panel.add(new JScrollPane(list));
        panel.add(saveButton);

        frame.add(panel);
        frame.add(contentArea);

        frame.setVisible(true);
    }
}
