package com.library.ui;

import com.library.database.UserDAO;
import com.library.models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing system users
 */
public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private UserDAO userDAO;
    
    /**
     * Constructor to initialize the panel
     */
    public UserPanel() {
        userDAO = new UserDAO();
        initComponents();
        loadUserData();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Role", "Last Login"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        
        editButton = new JButton("Edit User");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        
        deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load user data into the table
     */
    private void loadUserData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all users from the database
        List<User> users = userDAO.getAllUsers();
        
        // Add users to the table model
        for (User user : users) {
            Object[] rowData = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getLastLogin()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Add a new user
     */
    private void addUser() {
        // Create and show the user dialog
        UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        // If a user was added, refresh the table
        if (dialog.isUserSaved()) {
            refreshData();
        }
    }
    
    /**
     * Edit the selected user
     */
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to edit", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the user ID from the selected row
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get all users
        List<User> users = userDAO.getAllUsers();
        User selectedUser = null;
        
        // Find the user with the matching ID
        for (User user : users) {
            if (user.getUserId() == userId) {
                selectedUser = user;
                break;
            }
        }
        
        if (selectedUser != null) {
            // Create and show the user dialog
            UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), selectedUser);
            dialog.setVisible(true);
            
            // If a user was edited, refresh the table
            if (dialog.isUserSaved()) {
                refreshData();
            }
        }
    }
    
    /**
     * Delete the selected user
     */
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a user to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the user ID and username from the selected row
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the user '" + username + "'?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean deleted = userDAO.deleteUser(userId);
            
            if (deleted) {
                JOptionPane.showMessageDialog(this, 
                    "User deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete user.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh the user data
     */
    public void refreshData() {
        loadUserData();
    }
    
    /**
     * Inner class for user dialog
     */
    private class UserDialog extends JDialog {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JTextField fullNameField;
        private JTextField emailField;
        private JComboBox<String> roleCombo;
        
        private User user;
        private boolean userSaved = false;
        
        /**
         * Constructor for add/edit user dialog
         * @param parent Parent window
         * @param user User to edit, or null for a new user
         */
        public UserDialog(Window parent, User user) {
            super(parent, user == null ? "Add New User" : "Edit User", ModalityType.APPLICATION_MODAL);
            this.user = user;
            
            initDialog();
            
            if (user != null) {
                // Fill fields with user data
                usernameField.setText(user.getUsername());
                // Don't fill password field for security
                fullNameField.setText(user.getFullName());
                emailField.setText(user.getEmail());
                roleCombo.setSelectedItem(user.getRole());
                
                // Disable username field for existing users
                usernameField.setEnabled(false);
            }
        }
        
        /**
         * Initialize dialog components
         */
        private void initDialog() {
            setSize(400, 300);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 10));
            
            // Username
            formPanel.add(new JLabel("Username:"));
            usernameField = new JTextField();
            formPanel.add(usernameField);
            
            // Password
            formPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            formPanel.add(passwordField);
            
            // Full Name
            formPanel.add(new JLabel("Full Name:"));
            fullNameField = new JTextField();
            formPanel.add(fullNameField);
            
            // Email
            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);
            
            // Role
            formPanel.add(new JLabel("Role:"));
            String[] roles = {"Administrator", "Librarian"};
            roleCombo = new JComboBox<>(roles);
            formPanel.add(roleCombo);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // Buttons
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveUser();
                }
            });
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add panel to dialog
            getContentPane().add(mainPanel);
        }
        
        /**
         * Save the user
         */
        private void saveUser() {
            // Validate input
            if (usernameField.getText().trim().isEmpty() ||
                (user == null && passwordField.getPassword().length == 0) ||
                fullNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get values from fields
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            
            boolean success;
            
            if (user == null) {
                // Check if username exists
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose another.",
                        "Username Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Add new user
                User newUser = new User(username, password, fullName, email, role);
                success = userDAO.addUser(newUser);
            } else {
                // Update existing user
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(role);
                
                // Only update password if a new one is provided
                if (password.length() > 0) {
                    user.setPassword(password);
                }
                
                success = userDAO.updateUser(user);
            }
            
            if (success) {
                userSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error saving user. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Check if user was saved
         * @return true if user was saved, false otherwise
         */
        public boolean isUserSaved() {
            return userSaved;
        }
    }
} 