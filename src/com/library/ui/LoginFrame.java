package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.library.SimpleDemo;

/**
 * Login screen for the Library Management System
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    
    /**
     * Constructor to initialize the login frame
     */
    public LoginFrame() {
        initComponents();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Set frame properties
        setTitle("Library Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add title label
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        // Add some spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create form panel for username and password
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        
        // Username components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        
        // Password components
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        
        // Add form panel to main panel
        mainPanel.add(formPanel);
        
        // Add some spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonActionPerformed(e);
            }
        });
        
        // Exit button
        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add buttons to panel
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        // Add button panel to main panel
        mainPanel.add(buttonPanel);
        
        // Set the content pane
        setContentPane(mainPanel);
    }
    
    /**
     * Handle login button action
     * @param evt ActionEvent object
     */
    private void loginButtonActionPerformed(ActionEvent evt) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Simple authentication for demo: admin/admin123
        if (username.equals("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome, Administrator", 
                "Login Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open main application frame using SimpleDemo's MainFrame
            SimpleDemo.MainFrame mainFrame = new SimpleDemo.MainFrame();
            mainFrame.setVisible(true);
            
            // Close login frame
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 