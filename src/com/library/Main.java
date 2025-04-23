package com.library;

/**
 * Main class to launch the Library Management System
 */
public class Main {
    /**
     * Entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and display the login frame
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimpleDemo.LoginFrame().setVisible(true);
            }
        });
    }
} 