package com.library.ui;

import com.library.database.BookDAO;
import com.library.database.BookIssueDAO;
import com.library.models.BookIssue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Panel for viewing overdue books
 */
public class OverdueBookPanel extends JPanel {
    private JTable overdueTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton notifyButton;
    
    private BookIssueDAO bookIssueDAO;
    
    /**
     * Constructor to initialize the panel
     */
    public OverdueBookPanel() {
        bookIssueDAO = new BookIssueDAO();
        initComponents();
        loadOverdueBooks();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Overdue Books");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Days Overdue", "Estimated Fine"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        overdueTable = new JTable(tableModel);
        overdueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        overdueTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        overdueTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        overdueTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        overdueTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        overdueTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        overdueTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        overdueTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        overdueTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(overdueTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        notifyButton = new JButton("Notify Selected Member");
        notifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyMember();
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(notifyButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load overdue books into the table
     */
    private void loadOverdueBooks() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all overdue books from the database
        List<BookIssue> overdueBooks = bookIssueDAO.getOverdueBooks();
        
        // Calculate days overdue and fine for each book
        LocalDate today = LocalDate.now();
        double finePerDay = 2.50;
        
        for (BookIssue issue : overdueBooks) {
            // Calculate days overdue
            LocalDate dueDate = issue.getDueDate().toLocalDate();
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
            
            // Calculate estimated fine
            double estimatedFine = daysOverdue * finePerDay;
            
            Object[] rowData = {
                issue.getIssueId(),
                issue.getBookTitle(),
                issue.getMemberName(),
                issue.getIssueDate(),
                issue.getDueDate(),
                daysOverdue,
                String.format("$%.2f", estimatedFine)
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Notify a member about their overdue book
     */
    private void notifyMember() {
        int selectedRow = overdueTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a book to send notification", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String memberName = (String) tableModel.getValueAt(selectedRow, 2);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String dueDate = tableModel.getValueAt(selectedRow, 4).toString();
        String daysOverdue = tableModel.getValueAt(selectedRow, 5).toString();
        String fine = (String) tableModel.getValueAt(selectedRow, 6);
        
        // Show notification dialog
        JOptionPane.showMessageDialog(this,
            "Notification sent to " + memberName + ":\n\n" +
            "Dear " + memberName + ",\n\n" +
            "This is a reminder that the book \"" + bookTitle + "\" was due on " + dueDate + ".\n" +
            "The book is currently " + daysOverdue + " days overdue with an estimated fine of " + fine + ".\n\n" +
            "Please return the book as soon as possible to avoid additional fines.\n\n" +
            "Thank you,\nLibrary Management System",
            "Notification Sent",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Refresh the data
     */
    public void refreshData() {
        loadOverdueBooks();
    }
} 