package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Ambulance extends JFrame {

    JTable table;
    JButton addBtn, updateBtn, deleteBtn, backBtn;
    JTextField nameField, genderField, carNameField, availabilityField, locationField;
    boolean rowSelected = false;

    Ambulance() {
        // ===== FRAME SETUP =====
        setTitle("Filostack - Ambulance Info");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(950, 70));

        JLabel title = new JLabel("Ambulance Information", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/ambulance.png"));
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
        JPanel formPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        formPanel.setBackground(Color.WHITE);

        nameField = new JTextField();
        genderField = new JTextField();
        carNameField = new JTextField();
        availabilityField = new JTextField();
        locationField = new JTextField();

        formPanel.add(labeledField("Name", nameField));
        formPanel.add(labeledField("Gender", genderField));
        formPanel.add(labeledField("Car Name", carNameField));
        formPanel.add(labeledField("Availability", availabilityField));
        formPanel.add(labeledField("Location", locationField));

        add(formPanel, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(950, 70));

        addBtn = createButton("Add");
        updateBtn = createButton("Update");
        deleteBtn = createButton("Delete");
        backBtn = createButton("Back");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        loadTableData();

        // ===== EVENT HANDLERS =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(table.getValueAt(row, 0).toString());
                    genderField.setText(table.getValueAt(row, 1).toString());
                    carNameField.setText(table.getValueAt(row, 2).toString());
                    availabilityField.setText(table.getValueAt(row, 3).toString());
                    locationField.setText(table.getValueAt(row, 4).toString());
                    rowSelected = true;
                }
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAmbulance();
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAmbulance();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAmbulance();
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
            String query = "SELECT name AS 'Name', gender AS 'Gender', car_name AS 'Car Name', available AS 'Availability', location AS 'Location' FROM ambulance";
            ResultSet rs = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
            rowSelected = false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void addAmbulance() {
        try {
            String name = nameField.getText().trim();
            String gender = genderField.getText().trim();
            String car = carNameField.getText().trim();
            String avail = availabilityField.getText().trim();
            String location = locationField.getText().trim();

            if (name.isEmpty() || gender.isEmpty() || car.isEmpty() || avail.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            conn c = new conn();
            String query = "INSERT INTO ambulance (name, gender, car_name, available, location) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, gender);
            ps.setString(3, car);
            ps.setString(4, avail);
            ps.setString(5, location);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ambulance added successfully!");
            loadTableData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding ambulance: " + e.getMessage());
        }
    }

    private void updateAmbulance() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a record to update!");
                return;
            }

            String gender = genderField.getText().trim();
            String car = carNameField.getText().trim();
            String avail = availabilityField.getText().trim();
            String location = locationField.getText().trim();

            conn c = new conn();
            String query = "UPDATE ambulance SET gender=?, car_name=?, available=?, location=? WHERE name=?";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, gender);
            ps.setString(2, car);
            ps.setString(3, avail);
            ps.setString(4, location);
            ps.setString(5, name);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ambulance updated successfully!");
            loadTableData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating ambulance: " + e.getMessage());
        }
    }

    private void deleteAmbulance() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a record to delete!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this record?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            conn c = new conn();
            String query = "DELETE FROM ambulance WHERE name=?";
            PreparedStatement ps = c.connection.prepareStatement(query);
            ps.setString(1, name);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Ambulance deleted successfully!");
            loadTableData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting ambulance: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Ambulance();
    }
}
