package hospital.management.system;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class SearchRoom extends JFrame implements ActionListener {

    JTable table;
    Choice choice;
    JButton searchBtn, removeBtn, updateBtn, backBtn;

    public SearchRoom() {
        // Frame setup
        setTitle("Filostack - Search Room");
        setSize(900, 600);
        setLocation(400, 200);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= Header =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(900, 70));

        JLabel title = new JLabel("Search Room", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // ================= Filter Panel =================
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(240, 240, 240));
        filterPanel.setPreferredSize(new Dimension(900, 60));

        JLabel statusLbl = new JLabel("Filter By Status: ");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        filterPanel.add(statusLbl);

        choice = new Choice();
        choice.add("All");
        choice.add("Available");
        choice.add("Occupied");
        filterPanel.add(choice);

        searchBtn = new JButton("Search");
        styleButton(searchBtn);
        filterPanel.add(searchBtn);

        add(filterPanel, BorderLayout.SOUTH);

        // ================= Table =================
        table = new JTable() {
            // 🔒 Make table non-editable (prevents double-click updates)
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(13, 71, 161));
        tableHeader.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ================= Button Panel =================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setPreferredSize(new Dimension(900, 60));

        updateBtn = new JButton("Update Room");
        styleButton(updateBtn);
        buttonPanel.add(updateBtn);

        removeBtn = new JButton("Remove Room");
        styleButton(removeBtn);
        buttonPanel.add(removeBtn);

        backBtn = new JButton("Back");
        styleButton(backBtn);
        buttonPanel.add(backBtn);

        add(buttonPanel, BorderLayout.PAGE_END);

        // ================= Actions =================
        loadTable("SELECT room_no AS 'Room No', Availability, Price, Bed_Type AS 'Bed Type' FROM room");

        searchBtn.addActionListener(this);
        removeBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        backBtn.addActionListener(this);

        setVisible(true);
    }

    // ================= Helper: Style Button =================
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
    }

    // ================= Load Data =================
    private void loadTable(String query) {
        try {
            conn c = new conn();
            ResultSet rs = c.statement.executeQuery(query);
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    // ================= Remove Room =================
    private void removeRoom() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a room to remove!");
            return;
        }

        String roomNo = table.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Room " + roomNo + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn c = new conn();
                String query = "DELETE FROM room WHERE room_no='" + roomNo + "'";
                c.statement.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                loadTable("SELECT room_no AS 'Room No', Availability, Price, Bed_Type AS 'Bed Type' FROM room");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    // ================= Update Room =================
    private void updateRoom() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a room to update!");
            return;
        }

        String roomNo = table.getValueAt(row, 0).toString();
        String currentAvail = table.getValueAt(row, 1).toString();
        String currentPrice = table.getValueAt(row, 2).toString();
        String currentBed = table.getValueAt(row, 3).toString();

        JTextField priceField = new JTextField(currentPrice);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] availOptions = {"Available", "Occupied"};
        JComboBox<String> availBox = new JComboBox<>(availOptions);
        availBox.setSelectedItem(currentAvail);
        availBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] bedOptions = {"Single", "Double", "Suite"};
        JComboBox<String> bedBox = new JComboBox<>(bedOptions);
        bedBox.setSelectedItem(currentBed);
        bedBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel panel = new JPanel(new GridLayout(3, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Availability:"));
        panel.add(availBox);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Bed Type:"));
        panel.add(bedBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Update Room " + roomNo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newAvail = availBox.getSelectedItem().toString();
                String newPrice = priceField.getText().trim();
                String newBed = bedBox.getSelectedItem().toString();

                double priceVal;
                try {
                    priceVal = Double.parseDouble(newPrice);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "⚠ Please enter a valid numeric price!");
                    return;
                }

                conn c = new conn();
                String query = "UPDATE room SET Availability='" + newAvail +
                        "', Price='" + priceVal +
                        "', Bed_Type='" + newBed +
                        "' WHERE room_no='" + roomNo + "'";
                c.statement.executeUpdate(query);

                JOptionPane.showMessageDialog(this, "✅ Room updated successfully!");
                loadTable("SELECT room_no AS 'Room No', Availability, Price, Bed_Type AS 'Bed Type' FROM room");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    // ================= Centralized Event Handling =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            String filter = choice.getSelectedItem();
            if (filter.equals("All")) {
                loadTable("SELECT room_no AS 'Room No', Availability, Price, Bed_Type AS 'Bed Type' FROM room");
            } else {
                loadTable("SELECT room_no AS 'Room No', Availability, Price, Bed_Type AS 'Bed Type' FROM room WHERE Availability='" + filter + "'");
            }
        } else if (e.getSource() == removeBtn) {
            removeRoom();
        } else if (e.getSource() == updateBtn) {
            updateRoom();
        } else if (e.getSource() == backBtn) {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new SearchRoom();
    }
}
