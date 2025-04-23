package com.library.ui;

import com.library.database.BookDAO;
import com.library.database.BookIssueDAO;
import com.library.database.MemberDAO;
import com.library.models.Book;
import com.library.models.BookIssue;
import com.library.models.Member;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for issuing books to members
 */
public class IssueBookPanel extends JPanel {
    private JComboBox<ComboItem<Book>> bookCombo;
    private JComboBox<ComboItem<Member>> memberCombo;
    private JLabel lblBookId;
    private JLabel lblBookTitle;
    private JLabel lblBookAuthor;
    private JLabel lblBookAvailable;
    private JLabel lblMemberId;
    private JLabel lblMemberName;
    private JLabel lblMemberType;
    private JLabel lblMemberStatus;
    private JTextField txtIssueDate;
    private JTextField txtDueDate;
    private JButton issueButton;
    private JButton clearButton;
    
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private BookIssueDAO bookIssueDAO;
    
    /**
     * Constructor to initialize the panel
     */
    public IssueBookPanel() {
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        bookIssueDAO = new BookIssueDAO();
        initComponents();
        loadBookAndMemberData();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Issue Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Left panel for book and member selection
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Book and Member Selection"));
        
        JPanel selectionPanel = new JPanel(new GridLayout(5, 2, 5, 15));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Book selection
        selectionPanel.add(new JLabel("Select Book:"));
        bookCombo = new JComboBox<>();
        bookCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBookDetails();
            }
        });
        selectionPanel.add(bookCombo);
        
        // Member selection
        selectionPanel.add(new JLabel("Select Member:"));
        memberCombo = new JComboBox<>();
        memberCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMemberDetails();
            }
        });
        selectionPanel.add(memberCombo);
        
        // Issue Date
        selectionPanel.add(new JLabel("Issue Date:"));
        txtIssueDate = new JTextField(Date.valueOf(LocalDate.now()).toString());
        txtIssueDate.setEditable(false);
        selectionPanel.add(txtIssueDate);
        
        // Due Date (14 days from today by default)
        selectionPanel.add(new JLabel("Due Date:"));
        txtDueDate = new JTextField(Date.valueOf(LocalDate.now().plusDays(14)).toString());
        selectionPanel.add(txtDueDate);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        issueButton = new JButton("Issue Book");
        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                issueBook();
            }
        });
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        buttonPanel.add(issueButton);
        buttonPanel.add(clearButton);
        
        selectionPanel.add(new JLabel(""));
        selectionPanel.add(buttonPanel);
        
        leftPanel.add(selectionPanel, BorderLayout.CENTER);
        
        // Right panel for details
        JPanel rightPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Book details panel
        JPanel bookDetailsPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        bookDetailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        bookDetailsPanel.add(new JLabel("Book ID:"));
        lblBookId = new JLabel("");
        bookDetailsPanel.add(lblBookId);
        
        bookDetailsPanel.add(new JLabel("Title:"));
        lblBookTitle = new JLabel("");
        bookDetailsPanel.add(lblBookTitle);
        
        bookDetailsPanel.add(new JLabel("Author:"));
        lblBookAuthor = new JLabel("");
        bookDetailsPanel.add(lblBookAuthor);
        
        bookDetailsPanel.add(new JLabel("Available:"));
        lblBookAvailable = new JLabel("");
        bookDetailsPanel.add(lblBookAvailable);
        
        tabbedPane.addTab("Book Details", bookDetailsPanel);
        
        // Member details panel
        JPanel memberDetailsPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        memberDetailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        memberDetailsPanel.add(new JLabel("Member ID:"));
        lblMemberId = new JLabel("");
        memberDetailsPanel.add(lblMemberId);
        
        memberDetailsPanel.add(new JLabel("Name:"));
        lblMemberName = new JLabel("");
        memberDetailsPanel.add(lblMemberName);
        
        memberDetailsPanel.add(new JLabel("Member Type:"));
        lblMemberType = new JLabel("");
        memberDetailsPanel.add(lblMemberType);
        
        memberDetailsPanel.add(new JLabel("Status:"));
        lblMemberStatus = new JLabel("");
        memberDetailsPanel.add(lblMemberStatus);
        
        tabbedPane.addTab("Member Details", memberDetailsPanel);
        
        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Load book and member data for combo boxes
     */
    private void loadBookAndMemberData() {
        // Clear existing data
        bookCombo.removeAllItems();
        memberCombo.removeAllItems();
        
        // Load books
        List<Book> books = bookDAO.getAllBooks();
        for (Book book : books) {
            // Only add books that are available
            if (book.getAvailable() > 0) {
                bookCombo.addItem(new ComboItem<>(book, book.getTitle()));
            }
        }
        
        // Load members
        List<Member> members = memberDAO.getAllMembers();
        for (Member member : members) {
            // Only add active members
            if (member.getStatus().equals("Active")) {
                memberCombo.addItem(new ComboItem<>(member, member.getName()));
            }
        }
        
        // Update details if items are available
        if (bookCombo.getItemCount() > 0) {
            updateBookDetails();
        }
        
        if (memberCombo.getItemCount() > 0) {
            updateMemberDetails();
        }
    }
    
    /**
     * Update book details when a book is selected
     */
    private void updateBookDetails() {
        ComboItem<Book> selectedItem = (ComboItem<Book>) bookCombo.getSelectedItem();
        if (selectedItem != null) {
            Book book = selectedItem.getValue();
            lblBookId.setText(String.valueOf(book.getBookId()));
            lblBookTitle.setText(book.getTitle());
            lblBookAuthor.setText(book.getAuthor());
            lblBookAvailable.setText(String.valueOf(book.getAvailable()));
        } else {
            lblBookId.setText("");
            lblBookTitle.setText("");
            lblBookAuthor.setText("");
            lblBookAvailable.setText("");
        }
    }
    
    /**
     * Update member details when a member is selected
     */
    private void updateMemberDetails() {
        ComboItem<Member> selectedItem = (ComboItem<Member>) memberCombo.getSelectedItem();
        if (selectedItem != null) {
            Member member = selectedItem.getValue();
            lblMemberId.setText(String.valueOf(member.getMemberId()));
            lblMemberName.setText(member.getName());
            lblMemberType.setText(member.getMemberType());
            lblMemberStatus.setText(member.getStatus());
        } else {
            lblMemberId.setText("");
            lblMemberName.setText("");
            lblMemberType.setText("");
            lblMemberStatus.setText("");
        }
    }
    
    /**
     * Issue a book to a member
     */
    private void issueBook() {
        // Validate selections
        ComboItem<Book> selectedBook = (ComboItem<Book>) bookCombo.getSelectedItem();
        ComboItem<Member> selectedMember = (ComboItem<Member>) memberCombo.getSelectedItem();
        
        if (selectedBook == null || selectedMember == null) {
            JOptionPane.showMessageDialog(this,
                "Please select both a book and a member",
                "Selection Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate due date
        Date dueDate;
        try {
            dueDate = Date.valueOf(txtDueDate.getText());
            LocalDate issueDateLocal = LocalDate.parse(txtIssueDate.getText());
            LocalDate dueDateLocal = dueDate.toLocalDate();
            
            if (dueDateLocal.isBefore(issueDateLocal) || dueDateLocal.isEqual(issueDateLocal)) {
                JOptionPane.showMessageDialog(this,
                    "Due date must be after the issue date",
                    "Date Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Invalid due date format. Please use yyyy-MM-dd",
                "Date Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create book issue
        Book book = selectedBook.getValue();
        Member member = selectedMember.getValue();
        
        BookIssue bookIssue = new BookIssue(
            book.getBookId(),
            member.getMemberId(),
            Date.valueOf(txtIssueDate.getText()),
            dueDate,
            "Issued"
        );
        
        // Save to database
        boolean success = bookIssueDAO.addBookIssue(bookIssue);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Book issued successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error issuing book. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        bookCombo.setSelectedIndex(bookCombo.getItemCount() > 0 ? 0 : -1);
        memberCombo.setSelectedIndex(memberCombo.getItemCount() > 0 ? 0 : -1);
        txtIssueDate.setText(Date.valueOf(LocalDate.now()).toString());
        txtDueDate.setText(Date.valueOf(LocalDate.now().plusDays(14)).toString());
    }
    
    /**
     * Refresh the data
     */
    public void refreshData() {
        loadBookAndMemberData();
        clearForm();
    }
    
    /**
     * Utility class for combo box items with value and display text
     * @param <T> Type of the value
     */
    private class ComboItem<T> {
        private T value;
        private String displayText;
        
        public ComboItem(T value, String displayText) {
            this.value = value;
            this.displayText = displayText;
        }
        
        public T getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
} 