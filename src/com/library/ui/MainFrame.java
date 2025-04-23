package com.library.ui;

import com.library.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application frame with menu and content area
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    
    // Menu items
    private JMenuItem miBooks;
    private JMenuItem miMembers;
    private JMenuItem miIssueBook;
    private JMenuItem miReturnBook;
    private JMenuItem miOverdueBooks;
    private JMenuItem miUserManagement;
    private JMenuItem miLogout;
    private JMenuItem miExit;
    
    // Panels
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
    private IssueBookPanel issueBookPanel;
    private ReturnBookPanel returnBookPanel;
    private OverdueBookPanel overdueBookPanel;
    private UserPanel userPanel;
    
    /**
     * Constructor to initialize the main frame
     * @param user Currently logged in user
     */
    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
        showDashboard();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Set frame properties
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create content panel with CardLayout
        contentPanel = new JPanel(new CardLayout());
        
        // Initialize panels but don't add them yet
        bookPanel = new BookPanel();
        memberPanel = new MemberPanel();
        issueBookPanel = new IssueBookPanel();
        returnBookPanel = new ReturnBookPanel();
        overdueBookPanel = new OverdueBookPanel();
        
        // Only add user management panel if user is Administrator
        if (currentUser.getRole().equals("Administrator")) {
            userPanel = new UserPanel();
        }
        
        // Add panels to content panel
        contentPanel.add(new DashboardPanel(currentUser), "dashboard");
        contentPanel.add(bookPanel, "books");
        contentPanel.add(memberPanel, "members");
        contentPanel.add(issueBookPanel, "issueBook");
        contentPanel.add(returnBookPanel, "returnBook");
        contentPanel.add(overdueBookPanel, "overdueBooks");
        
        if (userPanel != null) {
            contentPanel.add(userPanel, "users");
        }
        
        // Add content panel to the frame
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // Create and add status bar
        JPanel statusBar = createStatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Create the menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        
        miLogout = new JMenuItem("Logout");
        miLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        miExit = new JMenuItem("Exit");
        miExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        fileMenu.add(miLogout);
        fileMenu.addSeparator();
        fileMenu.add(miExit);
        
        // Operations Menu
        JMenu operationsMenu = new JMenu("Operations");
        
        miBooks = new JMenuItem("Manage Books");
        miBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBooksPanel();
            }
        });
        
        miMembers = new JMenuItem("Manage Members");
        miMembers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMembersPanel();
            }
        });
        
        miIssueBook = new JMenuItem("Issue Book");
        miIssueBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIssueBookPanel();
            }
        });
        
        miReturnBook = new JMenuItem("Return Book");
        miReturnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReturnBookPanel();
            }
        });
        
        miOverdueBooks = new JMenuItem("Overdue Books");
        miOverdueBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOverdueBooksPanel();
            }
        });
        
        operationsMenu.add(miBooks);
        operationsMenu.add(miMembers);
        operationsMenu.addSeparator();
        operationsMenu.add(miIssueBook);
        operationsMenu.add(miReturnBook);
        operationsMenu.add(miOverdueBooks);
        
        // Admin Menu (only for administrators)
        JMenu adminMenu = null;
        if (currentUser.getRole().equals("Administrator")) {
            adminMenu = new JMenu("Administration");
            
            miUserManagement = new JMenuItem("User Management");
            miUserManagement.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showUserManagementPanel();
                }
            });
            
            adminMenu.add(miUserManagement);
        }
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        
        helpMenu.add(miAbout);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
        if (adminMenu != null) {
            menuBar.add(adminMenu);
        }
        menuBar.add(helpMenu);
        
        // Set the menu bar
        setJMenuBar(menuBar);
    }
    
    /**
     * Create the status bar
     * @return Status bar panel
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("  Logged in as: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("Library Management System v1.0  ");
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Show the dashboard panel
     */
    private void showDashboard() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "dashboard");
    }
    
    /**
     * Show the books management panel
     */
    private void showBooksPanel() {
        bookPanel.refreshData();
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "books");
    }
    
    /**
     * Show the members management panel
     */
    private void showMembersPanel() {
        memberPanel.refreshData();
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "members");
    }
    
    /**
     * Show the issue book panel
     */
    private void showIssueBookPanel() {
        issueBookPanel.refreshData();
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "issueBook");
    }
    
    /**
     * Show the return book panel
     */
    private void showReturnBookPanel() {
        returnBookPanel.refreshData();
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "returnBook");
    }
    
    /**
     * Show the overdue books panel
     */
    private void showOverdueBooksPanel() {
        overdueBookPanel.refreshData();
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "overdueBooks");
    }
    
    /**
     * Show the user management panel (admin only)
     */
    private void showUserManagementPanel() {
        if (userPanel != null) {
            userPanel.refreshData();
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "users");
        }
    }
    
    /**
     * Show the about dialog
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Library Management System v1.0\n" +
            "Developed by: [Your Name]\n" +
            "© 2023 All rights reserved",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Logout the current user
     */
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
} 