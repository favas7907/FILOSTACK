package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Room extends JFrame {

    JTable table;
    JButton backBtn, addBtn;
    JTextField roomNoField, priceField;
    JComboBox<String> availabilityBox, bedTypeBox;

    public Room() {
        setTitle("Filostack - Room Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= Header Panel =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("Room Management", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/room.png"));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(img));
            logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            headerPanel.add(logo, BorderLayout.WEST);
        } catch (Exception ignore) {}

        add(headerPanel, BorderLayout.NORTH);

        // ================= Table Panel =================
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        table = new JTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return comp;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(13, 71, 161));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(100, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        loadTableData("SELECT * FROM Room");
        add(tablePanel, BorderLayout.CENTER);

        // ================= Input + Buttons Panel =================
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(250, 250, 250));
        inputPanel.setPreferredSize(new Dimension(1000, 130));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("Room No:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(l1, gbc);

        roomNoField = new JTextField(10);
        roomNoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        inputPanel.add(roomNoField, gbc);

        JLabel l2 = new JLabel("Availability:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2;
        inputPanel.add(l2, gbc);

        availabilityBox = new JComboBox<>(new String[]{"Available", "Occupied"});
        availabilityBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 3;
        inputPanel.add(availabilityBox, gbc);

        JLabel l3 = new JLabel("Price:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(l3, gbc);

        priceField = new JTextField(10);
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        inputPanel.add(priceField, gbc);

        JLabel l4 = new JLabel("Bed Type:");
        l4.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2;
        inputPanel.add(l4, gbc);

        bedTypeBox = new JComboBox<>(new String[]{"Single", "Double"});
        bedTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 3;
        inputPanel.add(bedTypeBox, gbc);

        addBtn = new JButton("Add Room");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addBtn.setBackground(new Color(25, 118, 210));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        gbc.gridx = 4; gbc.gridy = 0;
        inputPanel.add(addBtn, gbc);

        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backBtn.setBackground(new Color(244, 67, 54));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        gbc.gridx = 4; gbc.gridy = 1;
        inputPanel.add(backBtn, gbc);

        add(inputPanel, BorderLayout.SOUTH);

        // ================= Actions =================
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String roomNo = roomNoField.getText().trim();
                String price = priceField.getText().trim();
                String availability = (String) availabilityBox.getSelectedItem();
                String bedType = (String) bedTypeBox.getSelectedItem();

                if (roomNo.isEmpty() || price.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields!");
                    return;
                }

                try {
                    conn c = new conn();

                    // Check if room already exists
                    String checkQuery = "SELECT COUNT(*) FROM Room WHERE room_no = ?";
                    PreparedStatement checkPs = c.connection.prepareStatement(checkQuery);
                    checkPs.setString(1, roomNo);
                    ResultSet rs = checkPs.executeQuery();

                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }

                    if (count > 0) {
                        JOptionPane.showMessageDialog(null, "Room number " + roomNo + " already exists!");
                        return;
                    }

                    // Insert new room
                    String query = "INSERT INTO Room (room_no, Availability, Price, Bed_Type) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = c.connection.prepareStatement(query);
                    ps.setString(1, roomNo);
                    ps.setString(2, availability);
                    ps.setString(3, price);
                    ps.setString(4, bedType);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Room Added Successfully!");
                    loadTableData("SELECT * FROM Room");
                    roomNoField.setText("");
                    priceField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    // ================= Load Room Data =================
    private void loadTableData(String query) {
        try {
            conn c = new conn();
            try (ResultSet resultSet = c.statement.executeQuery(query)) {
                table.setModel(DbUtils.resultSetToTableModel(resultSet));

                // Rename headers
                TableColumnModel columnModel = table.getColumnModel();
                if (columnModel.getColumnCount() >= 4) {
                    columnModel.getColumn(0).setHeaderValue("Room No");
                    columnModel.getColumn(1).setHeaderValue("Availability");
                    columnModel.getColumn(2).setHeaderValue("Price");
                    columnModel.getColumn(3).setHeaderValue("Bed Type");
                    table.getTableHeader().repaint();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Room();
    }
}
