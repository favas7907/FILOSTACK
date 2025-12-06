package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Employee_info extends JFrame {

    JTable table;
    JButton backBtn, searchBtn, addBtn, updateBtn, deleteBtn;
    JTextField searchField;

    Employee_info() {
        // Frame setup
        setTitle("Filostack - Employee Information");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("All Employee Information", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(new Color(240, 240, 240));
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBtn = new JButton("Go");
        styleButton(searchBtn);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(13, 71, 161));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        loadTableData("SELECT * FROM emp_info");

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(950, 60));

        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        backBtn = new JButton("Back");

        styleButton(addBtn);
        styleButton(updateBtn);
        styleButton(deleteBtn);
        styleButton(backBtn);

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText().trim();
                if (keyword.isEmpty()) loadTableData("SELECT * FROM emp_info");
                else searchEmployees(keyword);
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEmployeeForm(null);
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(Employee_info.this, "Select an employee to update.");
                    return;
                }
                String empId = table.getValueAt(row, 0).toString();
                String name = table.getValueAt(row, 1).toString();
                String role = table.getValueAt(row, 2).toString();
                String dept = table.getValueAt(row, 3).toString();
                String phone = table.getValueAt(row, 4).toString();
                String email = table.getValueAt(row, 5).toString();
                openEmployeeForm(new String[]{empId, name, role, dept, phone, email});
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25, 118, 210));
            }
        });
    }

    private void loadTableData(String query) {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
            renameHeaders();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void renameHeaders() {
        TableColumnModel col = table.getColumnModel();
        if (col.getColumnCount() >= 6) {
            col.getColumn(0).setHeaderValue("Employee ID");
            col.getColumn(1).setHeaderValue("Full Name");
            col.getColumn(2).setHeaderValue("Job Role");
            col.getColumn(3).setHeaderValue("Department");
            col.getColumn(4).setHeaderValue("Phone Number");
            col.getColumn(5).setHeaderValue("Email Address");
            table.getTableHeader().repaint();
        }
    }

    private void searchEmployees(String keyword) {
        try {
            conn c = new conn();
            String sql = "SELECT * FROM emp_info WHERE emp_id LIKE ? OR name LIKE ? OR role LIKE ? OR department LIKE ? OR phone LIKE ? OR email LIKE ?";
            PreparedStatement ps = c.connection.prepareStatement(sql);
            for (int i = 1; i <= 6; i++) ps.setString(i, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            renameHeaders();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void openEmployeeForm(String[] empData) {
        JDialog dialog = new JDialog(this, empData == null ? "Add Employee" : "Update Employee", true);
        dialog.setSize(550, 450); // 🔹 Bigger form window
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel(empData == null ? "Add New Employee" : "Update Employee Details", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(new Color(25, 118, 210));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(heading, gbc);

        gbc.gridwidth = 1;

        JTextField nameField = new JTextField(empData == null ? "" : empData[1], 25); // 🔹 wider
        JTextField roleField = new JTextField(empData == null ? "" : empData[2], 25);
        JTextField deptField = new JTextField(empData == null ? "" : empData[3], 25);
        JTextField phoneField = new JTextField(empData == null ? "" : empData[4], 25);
        JTextField emailField = new JTextField(empData == null ? "" : empData[5], 25);

        addFormRow(formPanel, gbc, 1, "Full Name:", nameField);
        addFormRow(formPanel, gbc, 2, "Job Role:", roleField);
        addFormRow(formPanel, gbc, 3, "Department:", deptField);
        addFormRow(formPanel, gbc, 4, "Phone:", phoneField);
        addFormRow(formPanel, gbc, 5, "Email:", emailField);

        JButton saveBtn = new JButton(empData == null ? "Add" : "Update");
        JButton cancelBtn = new JButton("Cancel");
        styleButton(saveBtn);
        styleButton(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(saveBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(cancelBtn, gbc);

        dialog.add(formPanel);

        // Save Action
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    conn c = new conn();
                    if (empData == null) {
                        ResultSet rs = c.statement.executeQuery("SELECT emp_id FROM emp_info ORDER BY emp_id DESC LIMIT 1");
                        String newId = "E001";
                        if (rs.next()) {
                            int last = Integer.parseInt(rs.getString("emp_id").substring(1));
                            newId = "E" + String.format("%03d", last + 1);
                        }
                        String sql = "INSERT INTO emp_info VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement ps = c.connection.prepareStatement(sql);
                        ps.setString(1, newId);
                        ps.setString(2, nameField.getText());
                        ps.setString(3, roleField.getText());
                        ps.setString(4, deptField.getText());
                        ps.setString(5, phoneField.getText());
                        ps.setString(6, emailField.getText());
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Employee Added!");
                    } else {
                        String sql = "UPDATE emp_info SET name=?, role=?, department=?, phone=?, email=? WHERE emp_id=?";
                        PreparedStatement ps = c.connection.prepareStatement(sql);
                        ps.setString(1, nameField.getText());
                        ps.setString(2, roleField.getText());
                        ps.setString(3, deptField.getText());
                        ps.setString(4, phoneField.getText());
                        ps.setString(5, emailField.getText());
                        ps.setString(6, empData[0]);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Employee Updated!");
                    }
                    loadTableData("SELECT * FROM emp_info");
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.gridy = y;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35)); // 🔹 much wider and taller
        panel.add(field, gbc);
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete.");
            return;
        }
        String empId = table.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Employee " + empId + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn c = new conn();
                String sql = "DELETE FROM emp_info WHERE emp_id=?";
                PreparedStatement ps = c.connection.prepareStatement(sql);
                ps.setString(1, empId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee Deleted!");
                loadTableData("SELECT * FROM emp_info");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Employee_info();
    }
}
