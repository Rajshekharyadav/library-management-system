package com.library.ui;

import com.library.database.BookDAO;
import com.library.database.BookIssueDAO;
import com.library.database.MemberDAO;
import com.library.models.User;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * Dashboard panel to show system statistics
 */
public class DashboardPanel extends JPanel {
    private User currentUser;
    
    // Statistics labels
    private JLabel lblTotalBooks;
    private JLabel lblTotalMembers;
    private JLabel lblBooksIssued;
    private JLabel lblOverdueBooks;
    
    /**
     * Constructor to initialize the dashboard
     * @param user Currently logged in user
     */
    public DashboardPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadStatistics();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome panel at the top
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);
        
        // Statistics panel in the center
        JPanel statsPanel = createStatisticsPanel();
        add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions panel at the bottom
        JPanel actionsPanel = createQuickActionsPanel();
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the welcome panel
     * @return Welcome panel
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.WEST);
        
        // Date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        JLabel dateLabel = new JLabel(dateFormat.format(new java.util.Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(dateLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create the statistics panel
     * @return Statistics panel
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        // Total Books
        JPanel booksPanel = createStatsCard("Total Books", "0", new Color(41, 128, 185));
        lblTotalBooks = (JLabel) booksPanel.getComponent(1);
        panel.add(booksPanel);
        
        // Total Members
        JPanel membersPanel = createStatsCard("Total Members", "0", new Color(39, 174, 96));
        lblTotalMembers = (JLabel) membersPanel.getComponent(1);
        panel.add(membersPanel);
        
        // Books Issued
        JPanel issuedPanel = createStatsCard("Books Issued", "0", new Color(142, 68, 173));
        lblBooksIssued = (JLabel) issuedPanel.getComponent(1);
        panel.add(issuedPanel);
        
        // Overdue Books
        JPanel overduePanel = createStatsCard("Overdue Books", "0", new Color(231, 76, 60));
        lblOverdueBooks = (JLabel) overduePanel.getComponent(1);
        panel.add(overduePanel);
        
        return panel;
    }
    
    /**
     * Create a statistics card
     * @param title Title of the card
     * @param value Initial value
     * @param color Background color
     * @return Panel with the card
     */
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
    
    /**
     * Create the quick actions panel
     * @return Quick actions panel
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Quick Actions", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        JButton issueBookBtn = new JButton("Issue Book");
        issueBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(DashboardPanel.this);
                mainFrame.setTitle("Library Management System - Issue Book");
            }
        });
        
        JButton returnBookBtn = new JButton("Return Book");
        returnBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(DashboardPanel.this);
                mainFrame.setTitle("Library Management System - Return Book");
            }
        });
        
        JButton addBookBtn = new JButton("Add Book");
        addBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(DashboardPanel.this);
                mainFrame.setTitle("Library Management System - Manage Books");
            }
        });
        
        JButton addMemberBtn = new JButton("Add Member");
        addMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(DashboardPanel.this);
                mainFrame.setTitle("Library Management System - Manage Members");
            }
        });
        
        panel.add(issueBookBtn);
        panel.add(returnBookBtn);
        panel.add(addBookBtn);
        panel.add(addMemberBtn);
        
        return panel;
    }
    
    /**
     * Load statistics from the database
     */
    private void loadStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int totalBooks = 0;
            private int totalMembers = 0;
            private int booksIssued = 0;
            private int overdueBooks = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                // Get total books
                BookDAO bookDAO = new BookDAO();
                totalBooks = bookDAO.getAllBooks().size();
                
                // Get total members
                MemberDAO memberDAO = new MemberDAO();
                totalMembers = memberDAO.getAllMembers().size();
                
                // Get books issued and overdue
                BookIssueDAO issueDAO = new BookIssueDAO();
                booksIssued = issueDAO.getAllBookIssues().size();
                overdueBooks = issueDAO.getOverdueBooks().size();
                
                return null;
            }
            
            @Override
            protected void done() {
                // Update the UI
                lblTotalBooks.setText(String.valueOf(totalBooks));
                lblTotalMembers.setText(String.valueOf(totalMembers));
                lblBooksIssued.setText(String.valueOf(booksIssued));
                lblOverdueBooks.setText(String.valueOf(overdueBooks));
            }
        };
        
        worker.execute();
    }
} 