package com.library.ui;

import com.library.database.MemberDAO;
import com.library.models.Member;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for managing library members
 */
public class MemberPanel extends JPanel {
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private MemberDAO memberDAO;
    
    /**
     * Constructor to initialize the panel
     */
    public MemberPanel() {
        memberDAO = new MemberDAO();
        initComponents();
        loadMemberData();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Member Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMembers();
            }
        });
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        initTable();
        JScrollPane scrollPane = new JScrollPane(memberTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add Member");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        });
        
        editButton = new JButton("Edit Member");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMember();
            }
        });
        
        deleteButton = new JButton("Delete Member");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMember();
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
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address", "Member Type", "Join Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        memberTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        memberTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(7).setPreferredWidth(70);
    }
    
    /**
     * Load member data into the table
     */
    private void loadMemberData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all members from the database
        List<Member> members = memberDAO.getAllMembers();
        
        // Add members to the table model
        for (Member member : members) {
            Object[] rowData = {
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getMemberType(),
                member.getJoinDate(),
                member.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Search for members
     */
    private void searchMembers() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadMemberData(); // If search field is empty, load all members
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Search for members
        List<Member> members = memberDAO.searchMembers(searchTerm);
        
        // Add matching members to the table model
        for (Member member : members) {
            Object[] rowData = {
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getMemberType(),
                member.getJoinDate(),
                member.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Add a new member
     */
    private void addMember() {
        // Create and show the member dialog
        MemberDialog dialog = new MemberDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        // If a member was added, refresh the table
        if (dialog.isMemberSaved()) {
            refreshData();
        }
    }
    
    /**
     * Edit the selected member
     */
    private void editMember() {
        int selectedRow = memberTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a member to edit", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the member ID from the selected row
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Retrieve the member from the database
        Member member = memberDAO.getMemberById(memberId);
        
        if (member != null) {
            // Create and show the member dialog
            MemberDialog dialog = new MemberDialog(SwingUtilities.getWindowAncestor(this), member);
            dialog.setVisible(true);
            
            // If a member was edited, refresh the table
            if (dialog.isMemberSaved()) {
                refreshData();
            }
        }
    }
    
    /**
     * Delete the selected member
     */
    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a member to delete", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the member ID and name from the selected row
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        String memberName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the member '" + memberName + "'?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean deleted = memberDAO.deleteMember(memberId);
            
            if (deleted) {
                JOptionPane.showMessageDialog(this, 
                    "Member deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete member. The member may have books checked out.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refresh the member data
     */
    public void refreshData() {
        loadMemberData();
    }
    
    /**
     * Inner class for member dialog
     */
    private class MemberDialog extends JDialog {
        private JTextField nameField;
        private JTextField emailField;
        private JTextField phoneField;
        private JTextArea addressArea;
        private JComboBox<String> memberTypeCombo;
        private JComboBox<String> statusCombo;
        
        private Member member;
        private boolean memberSaved = false;
        
        /**
         * Constructor for add/edit member dialog
         * @param parent Parent window
         * @param member Member to edit, or null for a new member
         */
        public MemberDialog(Window parent, Member member) {
            super(parent, member == null ? "Add New Member" : "Edit Member", ModalityType.APPLICATION_MODAL);
            this.member = member;
            
            initDialog();
            
            if (member != null) {
                // Fill fields with member data
                nameField.setText(member.getName());
                emailField.setText(member.getEmail());
                phoneField.setText(member.getPhone());
                addressArea.setText(member.getAddress());
                memberTypeCombo.setSelectedItem(member.getMemberType());
                statusCombo.setSelectedItem(member.getStatus());
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
            
            JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 10));
            
            // Name
            formPanel.add(new JLabel("Name:"));
            nameField = new JTextField();
            formPanel.add(nameField);
            
            // Email
            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField();
            formPanel.add(emailField);
            
            // Phone
            formPanel.add(new JLabel("Phone:"));
            phoneField = new JTextField();
            formPanel.add(phoneField);
            
            // Member Type
            formPanel.add(new JLabel("Member Type:"));
            String[] memberTypes = {"Student", "Faculty", "Staff"};
            memberTypeCombo = new JComboBox<>(memberTypes);
            formPanel.add(memberTypeCombo);
            
            // Status
            formPanel.add(new JLabel("Status:"));
            String[] statusOptions = {"Active", "Inactive"};
            statusCombo = new JComboBox<>(statusOptions);
            formPanel.add(statusCombo);
            
            // Address (takes up 2 rows)
            formPanel.add(new JLabel("Address:"));
            addressArea = new JTextArea(3, 20);
            addressArea.setLineWrap(true);
            JScrollPane addressScrollPane = new JScrollPane(addressArea);
            formPanel.add(addressScrollPane);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // Buttons
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveMember();
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
         * Save the member
         */
        private void saveMember() {
            // Validate input
            if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                addressArea.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get values from fields
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressArea.getText().trim();
            String memberType = (String) memberTypeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            
            boolean success;
            
            if (member == null) {
                // Add new member
                Member newMember = new Member(
                    name, email, phone, address, memberType,
                    Date.valueOf(LocalDate.now()), status
                );
                success = memberDAO.addMember(newMember);
            } else {
                // Update existing member
                member.setName(name);
                member.setEmail(email);
                member.setPhone(phone);
                member.setAddress(address);
                member.setMemberType(memberType);
                member.setStatus(status);
                success = memberDAO.updateMember(member);
            }
            
            if (success) {
                memberSaved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error saving member. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Check if member was saved
         * @return true if member was saved, false otherwise
         */
        public boolean isMemberSaved() {
            return memberSaved;
        }
    }
} 