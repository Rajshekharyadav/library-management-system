package com.library.ui;

import com.library.database.BookDAO;
import com.library.models.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for managing books in the library
 */
public class BookPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private BookDAO bookDAO;
    
    /**
     * Constructor to initialize the panel
     */
    public BookPanel() {
        bookDAO = new BookDAO();
        initComponents();
        loadBookData();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Book Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        initTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add Book");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        
        editButton = new JButton("Edit Book");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editBook();
            }
        });
        
        deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
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
     * Initialize the table
     */
    private void initTable() {
        String[] columnNames = {"ID", "Title", "Author", "Publisher", "ISBN", "Category", "Quantity", "Available", "Added Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        bookTable.getColumnModel().getColumn(8).setPreferredWidth(100);
    }
    
    /**
     * Load book data into the table
     */
    private void loadBookData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all books from the database
        List<Book> books = bookDAO.getAllBooks();
        
        // Add books to the table model
        for (Book book : books) {
            Object[] rowData = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getIsbn(),
                book.getCategory(),
                book.getQuantity(),
                book.getAvailable(),
                book.getAddedDate()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Search for books
     */
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadBookData(); // If search field is empty, load all books
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Search for books
        List<Book> books = bookDAO.searchBooks(searchTerm);
        
        // Add matching books to the table model
        for (Book book : books) {
            Object[] rowData = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getIsbn(),
                book.getCategory(),
                book.getQuantity(),
                book.getAvailable(),
                book.getAddedDate()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Add a new book
     */
    private void addBook() {
        // Create and show the book dialog
        BookDialog dialog = new BookDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        // If a book was added, refresh the table
        if (dialog.isBookSaved()) {
            refreshData();
        }
    }
    
    /**
     * Edit the selected book
     */
    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a book to edit", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the book ID from the selected row
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Retrieve the book from the database
        Book book = bookDAO.getBookById(bookId);
        
        if (book != null) {
            // Create and show the book dialog
            BookDialog dialog = new BookDialog(SwingUtilities.getWindowAncestor(this), book);
            dialog.setVisible(true);
            
            // If a book was edited, refresh the table
            if (dialog.isBookSaved()) {
                refreshData();
            }
        }
    }
    
    /**
     * Delete the selected book
     */
    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a book to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the book ID and title from the selected row
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the book '" + bookTitle + "'?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean deleted = bookDAO.deleteBook(bookId);
            
            if (deleted) {
                JOptionPane.showMessageDialog(this, 
                    "Book deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete book. The book may be currently issued to a member.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh the book data
     */
    public void refreshData() {
        loadBookData();
    }
    
    /**
     * Inner class for book dialog
     */
    private class BookDialog extends JDialog {
        private JTextField titleField;
        private JTextField authorField;
        private JTextField publisherField;
        private JTextField isbnField;
        private JTextField categoryField;
        private JSpinner quantitySpinner;
        private JSpinner availableSpinner;
        
        private Book book;
        private boolean bookSaved = false;
        
        /**
         * Constructor for add/edit book dialog
         * @param parent Parent window
         * @param book Book to edit, or null for a new book
         */
        public BookDialog(Window parent, Book book) {
            super(parent, book == null ? "Add New Book" : "Edit Book", ModalityType.APPLICATION_MODAL);
            this.book = book;
            
            initDialog();
            
            if (book != null) {
                // Fill fields with book data
                titleField.setText(book.getTitle());
                authorField.setText(book.getAuthor());
                publisherField.setText(book.getPublisher());
                isbnField.setText(book.getIsbn());
                categoryField.setText(book.getCategory());
                quantitySpinner.setValue(book.getQuantity());
                availableSpinner.setValue(book.getAvailable());
            }
        }
        
        /**
         * Initialize dialog components
         */
        private void initDialog() {
            setSize(400, 400);
            setLocationRelativeTo(getOwner());
            setResizable(false);
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 10));
            
            // Title
            formPanel.add(new JLabel("Title:"));
            titleField = new JTextField();
            formPanel.add(titleField);
            
            // Author
            formPanel.add(new JLabel("Author:"));
            authorField = new JTextField();
            formPanel.add(authorField);
            
            // Publisher
            formPanel.add(new JLabel("Publisher:"));
            publisherField = new JTextField();
            formPanel.add(publisherField);
            
            // ISBN
            formPanel.add(new JLabel("ISBN:"));
            isbnField = new JTextField();
            formPanel.add(isbnField);
            
            // Category
            formPanel.add(new JLabel("Category:"));
            categoryField = new JTextField();
            formPanel.add(categoryField);
            
            // Quantity
            formPanel.add(new JLabel("Quantity:"));
            SpinnerNumberModel quantityModel = new SpinnerNumberModel(1, 1, 100, 1);
            quantitySpinner = new JSpinner(quantityModel);
            formPanel.add(quantitySpinner);
            
            // Available
            formPanel.add(new JLabel("Available:"));
            SpinnerNumberModel availableModel = new SpinnerNumberModel(1, 0, 100, 1);
            availableSpinner = new JSpinner(availableModel);
            formPanel.add(availableSpinner);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // Buttons
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveBook();
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
         * Save the book
         */
        private void saveBook() {
            // Validate input
            if (titleField.getText().trim().isEmpty() ||
                authorField.getText().trim().isEmpty() ||
                publisherField.getText().trim().isEmpty() ||
                isbnField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate quantity and available
            int quantity = (int) quantitySpinner.getValue();
            int available = (int) availableSpinner.getValue();
            
            if (available > quantity) {
                JOptionPane.showMessageDialog(this,
                    "Available books cannot exceed total quantity",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get values from fields
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            String isbn = isbnField.getText().trim();
            String category = categoryField.getText().trim();
            
            boolean success;
            
            if (book == null) {
                // Add new book
                Book newBook = new Book(
                    title, author, publisher, isbn, category,
                    quantity, available, Date.valueOf(LocalDate.now())
                );
                success = bookDAO.addBook(newBook);
            } else {
                // Update existing book
                book.setTitle(title);
                book.setAuthor(author);
                book.setPublisher(publisher);
                book.setIsbn(isbn);
                book.setCategory(category);
                book.setQuantity(quantity);
                book.setAvailable(available);
                success = bookDAO.updateBook(book);
            }
            
            if (success) {
                bookSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error saving book. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Check if book was saved
         * @return true if book was saved, false otherwise
         */
        public boolean isBookSaved() {
            return bookSaved;
        }
    }
} 