package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class update_patient_details extends JFrame {

    JTable table;
    JButton updateBtn, deleteBtn, backBtn;
    DefaultTableModel model;

    public update_patient_details() {
        setTitle("Filostack - Update Patients");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("Update Patient Information", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        table = new JTable() {
            public boolean isCellEditable(int row, int column) {
                // Make Room_Number, deposit, and gender editable (columns 4,6,2)
                return column == 2 || column == 4 || column == 6;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 14));
        th.setBackground(new Color(13, 71, 161));
        th.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(950, 60));

        updateBtn = styledButton("Update");
        deleteBtn = styledButton("Delete");
        backBtn = styledButton("Back");

        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Load data
        loadTableData();

        // Button actions
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePatient();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePatient();
            }
        });

        setVisible(true);
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    private void loadTableData() {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery("SELECT * FROM patient_info");
            model = (DefaultTableModel) DbUtils.resultSetToTableModel(rs);
            table.setModel(model);
            c.connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

private void updatePatient() {
    if (table.isEditing()) {
        table.getCellEditor().stopCellEditing(); // commit edited value
    }

    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "⚠ Select a patient first!");
        return;
    }

    try {
        String number = table.getValueAt(row, 0).toString().trim();
        String room = table.getValueAt(row, 4).toString().trim();
        String deposit = table.getValueAt(row, 6).toString().trim();
        String gender = table.getValueAt(row, 2).toString().trim();

        // Validate Room_Number
        try {
            Integer.parseInt(room);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Room number must be numeric!");
            return;
        }

        // Normalize gender
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("m")) {
            gender = "Male";
        } else if (gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("f")) {
            gender = "Female";
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gender must be Male or Female!");
            return;
        }

        // Update database
        conn c = new conn();
        String q = "UPDATE patient_info SET Room_Number=?, deposit=?, gender=? WHERE number=?";
        PreparedStatement ps = c.connection.prepareStatement(q);
        ps.setString(1, room);
        ps.setString(2, deposit); // store as string
        ps.setString(3, gender);
        ps.setString(4, number);

        int updated = ps.executeUpdate();
        ps.close();
        c.connection.close();

        if (updated > 0) {
            JOptionPane.showMessageDialog(this, "✅ Patient details updated successfully!");
            loadTableData(); // refresh table
        } else {
            JOptionPane.showMessageDialog(this, "⚠ Update failed! Patient not found.");
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "❌ Error while updating: " + ex.getMessage());
        ex.printStackTrace();
    }
}

    private void deletePatient() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Select a patient to delete!");
            return;
        }
        try {
            String number = table.getValueAt(row, 0).toString();
            conn c = new conn();
            String q = "DELETE FROM patient_info WHERE number=?";
            PreparedStatement ps = c.connection.prepareStatement(q);
            ps.setString(1, number);

            int deleted = ps.executeUpdate();
            ps.close();
            c.connection.close();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "🗑 Patient deleted successfully!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Delete failed! Patient not found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error while deleting: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new update_patient_details();
    }
}
