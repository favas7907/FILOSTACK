package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Department extends JFrame {

    JTable table;
    JButton addBtn, updateBtn, deleteBtn, clearBtn, backBtn;
    JTextField idField, nameField, headField, phoneField, emailField;
    boolean rowSelected = false;

    Department() {
        // ===== FRAME SETUP =====
        setTitle("Filostack - Department Info");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(950, 70));

        JLabel title = new JLabel("Department Information", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/hospital.png"));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(img));
            logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            headerPanel.add(logo, BorderLayout.WEST);
        } catch (Exception ignore) {}

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        table = new JTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    comp.setBackground(new Color(187, 222, 251));
                }
                return comp;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(13, 71, 161));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridLayout(2, 5, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        formPanel.setBackground(Color.WHITE);

        idField = new JTextField();
        idField.setEditable(false);
        idField.setFocusable(false);  // Prevent typing or focus
        nameField = new JTextField();
        headField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        formPanel.add(labeledField("Dept ID (Auto)", idField));
        formPanel.add(labeledField("Dept Name", nameField));
        formPanel.add(labeledField("Head", headField));
        formPanel.add(labeledField("Phone", phoneField));
        formPanel.add(labeledField("Email", emailField));

        add(formPanel, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(950, 70));

        addBtn = createButton("Add");
        updateBtn = createButton("Update");
        deleteBtn = createButton("Delete");
        clearBtn = createButton("Clear");
        backBtn = createButton("Back");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        loadTableData();

        // ===== EVENT HANDLERS =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();

                // Unselect same row on second click
                if (rowSelected && row != -1 && idField.getText().equals(table.getValueAt(row, 0).toString())) {
                    table.clearSelection();
                    clearFields();
                    rowSelected = false;
                    return;
                }

                // Populate fields with selected row data
                if (row != -1) {
                    idField.setText(table.getValueAt(row, 0).toString());
                    nameField.setText(table.getValueAt(row, 1).toString());
                    headField.setText(table.getValueAt(row, 2).toString());
                    phoneField.setText(table.getValueAt(row, 3).toString());
                    emailField.setText(table.getValueAt(row, 4).toString());
                    rowSelected = true;
                }
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDepartment();
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDepartment();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDepartment();
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
                table.clearSelection();
                rowSelected = false;
            }
        });

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setVisible(true);
    }

    // ===== HELPER METHODS =====
    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadTableData() {
        try {
            conn c = new conn();
            String query = "SELECT dept_id AS 'Dept ID', department_name AS 'Department', " +
                           "department_head AS 'Head of Department', phone AS 'Phone Number', " +
                           "email AS 'Contact Email' FROM department";
            ResultSet rs = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
            rowSelected = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private String generateNextID() {
        try {
            conn c = new conn();
            String query = "SELECT COUNT(*) AS total FROM department";
            ResultSet rs = c.statement.executeQuery(query);
            if (rs.next()) {
                int next = rs.getInt("total") + 1;
                return String.format("D%02d", next);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating ID: " + e.getMessage());
        }
        return "D00";
    }

    private void addDepartment() {
        try {
            String id = generateNextID();
            String name = nameField.getText().trim();
            String head = headField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || head.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            conn c = new conn();
            String query = "INSERT INTO department (dept_id, department_name, department_head, phone, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, head);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Department added successfully!");
            loadTableData();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding department: " + e.getMessage());
        }
    }

    private void updateDepartment() {
        try {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a department to update!");
                return;
            }

            String name = nameField.getText().trim();
            String head = headField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            conn c = new conn();
            String query = "UPDATE department SET department_name=?, department_head=?, phone=?, email=? WHERE dept_id=?";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, head);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Department updated successfully!");
            loadTableData();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating department: " + e.getMessage());
        }
    }

    private void deleteDepartment() {
        try {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a department to delete!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this department?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            conn c = new conn();
            String query = "DELETE FROM department WHERE dept_id=?";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Department deleted successfully!");
            loadTableData();
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting department: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        headField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    public static void main(String[] args) {
        new Department();
    }
}
