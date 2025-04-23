package com.library.ui;

import com.library.database.BookDAO;
import com.library.database.BookIssueDAO;
import com.library.database.MemberDAO;
import com.library.models.Book;
import com.library.models.BookIssue;
import com.library.models.Member;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Panel for managing book returns
 */
public class ReturnBookPanel extends JPanel {
    private JTable issuedBooksTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearchTerm;
    private JButton btnSearch;
    private JButton btnReturn;
    private JButton btnRefresh;
    private JPanel returnDetailsPanel;
    private JLabel lblBookTitle;
    private JLabel lblMemberName;
    private JLabel lblIssueDate;
    private JLabel lblDueDate;
    private JLabel lblDaysLate;
    private JLabel lblFine;
    
    private BookIssueDAO bookIssueDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private double finePerDay = 2.50; // Fine amount per day
    
    /**
     * Constructor to initialize the panel
     */
    public ReturnBookPanel() {
        bookIssueDAO = new BookIssueDAO();
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        initComponents();
        loadIssuedBooks();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Return Books");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        txtSearchTerm = new JTextField(20);
        searchPanel.add(txtSearchTerm);
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchIssuedBooks();
            }
        });
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);
        
        // Create main panel (split into table and details)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Issue ID", "Book ID", "Book Title", "Member ID", "Member Name", "Issue Date", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
        btnReturn = new JButton("Return Book");
        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
        btnReturn.setEnabled(false);
        
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnRefresh);
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
    
    /**
     * Load all issued books
     */
    private void loadIssuedBooks() {
        // Clear existing data
        tableModel.setRowCount(0);
        btnReturn.setEnabled(false);
        
        // Load issued books
        List<BookIssue> issuedBooks = bookIssueDAO.getAllBookIssues();
        
        for (BookIssue issue : issuedBooks) {
            // Only show books that are still issued
            if (issue.getStatus().equals("Issued")) {
                Book book = bookDAO.getBookById(issue.getBookId());
                Member member = memberDAO.getMemberById(issue.getMemberId());
                
                if (book != null && member != null) {
                    Object[] row = {
                        issue.getIssueId(),
                        book.getBookId(),
                        book.getTitle(),
                        member.getMemberId(),
                        member.getName(),
                        issue.getIssueDate(),
                        issue.getDueDate(),
                        issue.getStatus()
                    };
                    tableModel.addRow(row);
                }
            }
        }
        
        clearReturnDetails();
    }
    
    /**
     * Search for issued books
     */
    private void searchIssuedBooks() {
        String searchTerm = txtSearchTerm.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadIssuedBooks();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        btnReturn.setEnabled(false);
        
        // Load issued books
        List<BookIssue> issuedBooks = bookIssueDAO.getAllBookIssues();
        
        for (BookIssue issue : issuedBooks) {
            // Only show books that are still issued
            if (issue.getStatus().equals("Issued")) {
                Book book = bookDAO.getBookById(issue.getBookId());
                Member member = memberDAO.getMemberById(issue.getMemberId());
                
                if (book != null && member != null) {
                    // Check if search term matches book title, member name, or IDs
                    boolean matches = 
                        book.getTitle().toLowerCase().contains(searchTerm) ||
                        member.getName().toLowerCase().contains(searchTerm) ||
                        String.valueOf(issue.getIssueId()).contains(searchTerm) ||
                        String.valueOf(book.getBookId()).contains(searchTerm) ||
                        String.valueOf(member.getMemberId()).contains(searchTerm);
                    
                    if (matches) {
                        Object[] row = {
                            issue.getIssueId(),
                            book.getBookId(),
                            book.getTitle(),
                            member.getMemberId(),
                            member.getName(),
                            issue.getIssueDate(),
                            issue.getDueDate(),
                            issue.getStatus()
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        }
        
        clearReturnDetails();
    }
    
    /**
     * Update return details when a row is selected
     */
    private void updateReturnDetails() {
        int selectedRow = issuedBooksTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookTitle = (String) tableModel.getValueAt(selectedRow, 2);
            String memberName = (String) tableModel.getValueAt(selectedRow, 4);
            Date issueDate = (Date) tableModel.getValueAt(selectedRow, 5);
            Date dueDate = (Date) tableModel.getValueAt(selectedRow, 6);
            
            // Calculate days late
            LocalDate today = LocalDate.now();
            LocalDate dueDateLocal = dueDate.toLocalDate();
            long daysLate = 0;
            
            if (today.isAfter(dueDateLocal)) {
                daysLate = ChronoUnit.DAYS.between(dueDateLocal, today);
            }
            
            // Calculate fine
            double fine = daysLate * finePerDay;
            
            // Update labels
            lblBookTitle.setText(bookTitle);
            lblMemberName.setText(memberName);
            lblIssueDate.setText(issueDate.toString());
            lblDueDate.setText(dueDate.toString());
            lblDaysLate.setText(String.valueOf(daysLate));
            lblFine.setText(String.format("$%.2f", fine));
            
            btnReturn.setEnabled(true);
        } else {
            clearReturnDetails();
        }
    }
    
    /**
     * Clear return details
     */
    private void clearReturnDetails() {
        lblBookTitle.setText("");
        lblMemberName.setText("");
        lblIssueDate.setText("");
        lblDueDate.setText("");
        lblDaysLate.setText("");
        lblFine.setText("");
        btnReturn.setEnabled(false);
    }
    
    /**
     * Return a book
     */
    private void returnBook() {
        int selectedRow = issuedBooksTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        int bookId = (int) tableModel.getValueAt(selectedRow, 1);
        
        // Get days late and fine
        long daysLate = Long.parseLong(lblDaysLate.getText());
        double fine = daysLate * finePerDay;
        
        // Show confirmation dialog with fine information
        String message = "Do you want to return this book?";
        if (daysLate > 0) {
            message = String.format("This book is %d days late. A fine of $%.2f will be applied. Proceed with return?", 
                daysLate, fine);
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm Return",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Update book issue status to "Returned"
            boolean success = bookIssueDAO.updateBookIssueStatus(issueId, "Returned");
            
            if (success) {
                // Update book availability
                bookDAO.updateBookAvailability(bookId, 1);
                
                // Show success message
                if (daysLate > 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        String.format("Book returned successfully. Fine collected: $%.2f", fine),
                        "Return Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Book returned successfully.",
                        "Return Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                
                // Refresh data
                refreshData();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Error returning book. Please try again.",
                    "Return Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    /**
     * Refresh the data
     */
    public void refreshData() {
        loadIssuedBooks();
        txtSearchTerm.setText("");
    }
} 