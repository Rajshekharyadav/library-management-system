package com.library;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SimpleDemo {
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and display the login frame
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
    
    /**
     * Login Frame
     */
    public static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        
        public LoginFrame() {
            setTitle("Library Management System - Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Title
            JLabel titleLabel = new JLabel("Library Management System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            // Form
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 10));
            formPanel.add(new JLabel("Username:"));
            usernameField = new JTextField(15);
            formPanel.add(usernameField);
            formPanel.add(new JLabel("Password:"));
            passwordField = new JPasswordField(15);
            formPanel.add(passwordField);
            mainPanel.add(formPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter both username and password", 
                        "Login Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Simple authentication: admin/admin123
                if (username.equals("admin") && password.equals("admin123")) {
                    JOptionPane.showMessageDialog(this, 
                        "Login successful! Welcome, Administrator", 
                        "Login Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open main application frame
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    
                    // Close login frame
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid username or password", 
                        "Login Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> System.exit(0));
            
            buttonPanel.add(loginButton);
            buttonPanel.add(exitButton);
            mainPanel.add(buttonPanel);
            
            setContentPane(mainPanel);
        }
    }
    
    /**
     * Main application frame
     */
    public static class MainFrame extends JFrame {
        private JPanel contentPanel;
        private CardLayout cardLayout;
        
        // Panels
        private BooksPanel booksPanel;
        private ReturnBooksPanel returnBooksPanel;
        
        public MainFrame() {
            setTitle("Library Management System");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            
            // Create menu bar
            JMenuBar menuBar = new JMenuBar();
            
            // File menu
            JMenu fileMenu = new JMenu("File");
            JMenuItem logoutItem = new JMenuItem("Logout");
            logoutItem.addActionListener(e -> {
                dispose();
                new LoginFrame().setVisible(true);
            });
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            fileMenu.add(logoutItem);
            fileMenu.addSeparator();
            fileMenu.add(exitItem);
            
            // Operations menu
            JMenu operationsMenu = new JMenu("Operations");
            JMenuItem booksItem = new JMenuItem("Manage Books");
            booksItem.addActionListener(e -> showPanel("books"));
            JMenuItem returnBookItem = new JMenuItem("Return Book");
            returnBookItem.addActionListener(e -> showPanel("returnBook"));
            operationsMenu.add(booksItem);
            operationsMenu.add(returnBookItem);
            
            menuBar.add(fileMenu);
            menuBar.add(operationsMenu);
            setJMenuBar(menuBar);
            
            // Content panel with CardLayout
            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            
            // Initialize panels
            booksPanel = new BooksPanel();
            returnBooksPanel = new ReturnBooksPanel();
            
            // Add panels to content panel
            contentPanel.add(new DashboardPanel(), "dashboard");
            contentPanel.add(booksPanel, "books");
            contentPanel.add(returnBooksPanel, "returnBook");
            
            // Add content panel to the frame
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            
            // Status bar
            JPanel statusBar = new JPanel(new BorderLayout());
            statusBar.setBorder(BorderFactory.createEtchedBorder());
            JLabel statusLabel = new JLabel("  Logged in as: Administrator");
            statusBar.add(statusLabel, BorderLayout.WEST);
            JLabel versionLabel = new JLabel("Library Management System Demo  ");
            statusBar.add(versionLabel, BorderLayout.EAST);
            getContentPane().add(statusBar, BorderLayout.SOUTH);
            
            // Show dashboard by default
            cardLayout.show(contentPanel, "dashboard");
        }
        
        private void showPanel(String panelName) {
            cardLayout.show(contentPanel, panelName);
        }
    }
    
    /**
     * Dashboard Panel
     */
    static class DashboardPanel extends JPanel {
        public DashboardPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Welcome panel
            JPanel welcomePanel = new JPanel(new BorderLayout());
            welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            JLabel welcomeLabel = new JLabel("Welcome to the Library Management System");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
            welcomePanel.add(welcomeLabel, BorderLayout.WEST);
            add(welcomePanel, BorderLayout.NORTH);
            
            // Statistics panel
            JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            statsPanel.add(createStatsCard("Total Books", "125", new Color(41, 128, 185)));
            statsPanel.add(createStatsCard("Total Members", "42", new Color(39, 174, 96)));
            statsPanel.add(createStatsCard("Books Issued", "37", new Color(142, 68, 173)));
            statsPanel.add(createStatsCard("Overdue Books", "5", new Color(231, 76, 60)));
            add(statsPanel, BorderLayout.CENTER);
            
            // Quick actions panel
            JPanel actionsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
            actionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Quick Actions", 
                0, 
                0,
                new Font("Arial", Font.BOLD, 14)
            ));
            
            JButton issueBookBtn = new JButton("Issue Book");
            JButton returnBookBtn = new JButton("Return Book");
            returnBookBtn.addActionListener(e -> {
                Component comp = SwingUtilities.getRoot(this);
                if (comp instanceof MainFrame) {
                    ((MainFrame) comp).showPanel("returnBook");
                }
            });
            JButton addBookBtn = new JButton("Add Book");
            addBookBtn.addActionListener(e -> {
                Component comp = SwingUtilities.getRoot(this);
                if (comp instanceof MainFrame) {
                    ((MainFrame) comp).showPanel("books");
                }
            });
            JButton addMemberBtn = new JButton("Add Member");
            
            actionsPanel.add(issueBookBtn);
            actionsPanel.add(returnBookBtn);
            actionsPanel.add(addBookBtn);
            actionsPanel.add(addMemberBtn);
            
            add(actionsPanel, BorderLayout.SOUTH);
        }
        
        private JPanel createStatsCard(String title, String value, Color color) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(color);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setHorizontalAlignment(JLabel.CENTER);
            
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(valueLabel, BorderLayout.CENTER);
            
            return panel;
        }
    }
    
    /**
     * Books Panel
     */
    static class BooksPanel extends JPanel {
        private JTable bookTable;
        private DefaultTableModel tableModel;
        
        public BooksPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel("Book Management");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titlePanel.add(titleLabel, BorderLayout.WEST);
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JTextField searchField = new JTextField(15);
            JButton searchButton = new JButton("Search");
            
            searchPanel.add(new JLabel("Search: "));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            
            titlePanel.add(searchPanel, BorderLayout.EAST);
            add(titlePanel, BorderLayout.NORTH);
            
            // Book table
            String[] columnNames = {"ID", "Title", "Author", "Publisher", "Category", "Quantity", "Available"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add sample data
            addSampleBookData();
            
            bookTable = new JTable(tableModel);
            bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(bookTable);
            add(scrollPane, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton addButton = new JButton("Add Book");
            JButton editButton = new JButton("Edit Book");
            JButton deleteButton = new JButton("Delete Book");
            JButton refreshButton = new JButton("Refresh");
            
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void addSampleBookData() {
            Object[] row1 = {1, "Introduction to Java Programming", "Y. Daniel Liang", "Pearson", "Programming", 5, 5};
            Object[] row2 = {2, "Database System Concepts", "Abraham Silberschatz", "McGraw Hill", "Database", 3, 3};
            Object[] row3 = {3, "Clean Code", "Robert C. Martin", "Prentice Hall", "Software Engineering", 2, 2};
            Object[] row4 = {4, "The Pragmatic Programmer", "Andrew Hunt", "Addison-Wesley", "Software Engineering", 3, 2};
            Object[] row5 = {5, "Design Patterns", "Erich Gamma", "Addison-Wesley", "Software Engineering", 2, 1};
            
            tableModel.addRow(row1);
            tableModel.addRow(row2);
            tableModel.addRow(row3);
            tableModel.addRow(row4);
            tableModel.addRow(row5);
        }
    }
    
    /**
     * Return Books Panel
     */
    static class ReturnBooksPanel extends JPanel {
        private JTable issuedBooksTable;
        private DefaultTableModel tableModel;
        private JPanel returnDetailsPanel;
        private JLabel lblBookTitle;
        private JLabel lblMemberName;
        private JLabel lblIssueDate;
        private JLabel lblDueDate;
        private JLabel lblDaysLate;
        private JLabel lblFine;
        
        public ReturnBooksPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            JLabel titleLabel = new JLabel("Return Books");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titlePanel.add(titleLabel, BorderLayout.WEST);
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JTextField searchField = new JTextField(15);
            JButton searchButton = new JButton("Search");
            
            searchPanel.add(new JLabel("Search: "));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            
            titlePanel.add(searchPanel, BorderLayout.EAST);
            add(titlePanel, BorderLayout.NORTH);
            
            // Create main panel
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.7);
            
            // Table panel
            JPanel tablePanel = new JPanel(new BorderLayout());
            String[] columnNames = {"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Status"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add sample data
            addSampleIssuedBooks();
            
            issuedBooksTable = new JTable(tableModel);
            issuedBooksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            issuedBooksTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && issuedBooksTable.getSelectedRow() != -1) {
                    updateReturnDetails();
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton returnButton = new JButton("Return Book");
            returnButton.addActionListener(e -> {
                int selectedRow = issuedBooksTable.getSelectedRow();
                if (selectedRow != -1) {
                    returnBook(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Please select a book to return",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
            
            JButton refreshButton = new JButton("Refresh");
            
            buttonPanel.add(returnButton);
            buttonPanel.add(refreshButton);
            tablePanel.add(buttonPanel, BorderLayout.SOUTH);
            
            splitPane.setTopComponent(tablePanel);
            
            // Return details panel
            returnDetailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            returnDetailsPanel.setBorder(BorderFactory.createTitledBorder("Return Details"));
            
            returnDetailsPanel.add(new JLabel("Book Title:"));
            lblBookTitle = new JLabel("");
            returnDetailsPanel.add(lblBookTitle);
            
            returnDetailsPanel.add(new JLabel("Member Name:"));
            lblMemberName = new JLabel("");
            returnDetailsPanel.add(lblMemberName);
            
            returnDetailsPanel.add(new JLabel("Issue Date:"));
            lblIssueDate = new JLabel("");
            returnDetailsPanel.add(lblIssueDate);
            
            returnDetailsPanel.add(new JLabel("Due Date:"));
            lblDueDate = new JLabel("");
            returnDetailsPanel.add(lblDueDate);
            
            returnDetailsPanel.add(new JLabel("Days Late:"));
            lblDaysLate = new JLabel("");
            returnDetailsPanel.add(lblDaysLate);
            
            returnDetailsPanel.add(new JLabel("Fine Amount:"));
            lblFine = new JLabel("");
            returnDetailsPanel.add(lblFine);
            
            JPanel detailsContainer = new JPanel(new BorderLayout());
            detailsContainer.add(returnDetailsPanel, BorderLayout.CENTER);
            
            splitPane.setBottomComponent(detailsContainer);
            add(splitPane, BorderLayout.CENTER);
        }
        
        private void addSampleIssuedBooks() {
            Object[] row1 = {1, "Introduction to Java Programming", "John Smith", "2023-04-01", "2023-04-15", "Issued"};
            Object[] row2 = {2, "Design Patterns", "Jane Doe", "2023-04-05", "2023-04-19", "Issued"};
            Object[] row3 = {3, "The Pragmatic Programmer", "Mike Johnson", "2023-04-10", "2023-04-24", "Issued"};
            
            tableModel.addRow(row1);
            tableModel.addRow(row2);
            tableModel.addRow(row3);
        }
        
        private void updateReturnDetails() {
            int selectedRow = issuedBooksTable.getSelectedRow();
            if (selectedRow != -1) {
                String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
                String memberName = (String) tableModel.getValueAt(selectedRow, 2);
                String issueDate = (String) tableModel.getValueAt(selectedRow, 3);
                String dueDate = (String) tableModel.getValueAt(selectedRow, 4);
                
                // Calculate days late (simplified)
                int daysLate = 0;
                double fine = daysLate * 2.50;
                
                lblBookTitle.setText(bookTitle);
                lblMemberName.setText(memberName);
                lblIssueDate.setText(issueDate);
                lblDueDate.setText(dueDate);
                lblDaysLate.setText(String.valueOf(daysLate));
                lblFine.setText(String.format("$%.2f", fine));
            } else {
                clearReturnDetails();
            }
        }
        
        private void clearReturnDetails() {
            lblBookTitle.setText("");
            lblMemberName.setText("");
            lblIssueDate.setText("");
            lblDueDate.setText("");
            lblDaysLate.setText("");
            lblFine.setText("");
        }
        
        private void returnBook(int row) {
            String bookTitle = (String) tableModel.getValueAt(row, 1);
            String memberName = (String) tableModel.getValueAt(row, 2);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to return the book '" + bookTitle + "' issued to " + memberName + "?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Book returned successfully",
                    "Return Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                clearReturnDetails();
            }
        }
    }
} 