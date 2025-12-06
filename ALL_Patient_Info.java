package hospital.management.system;

import net.proteanit.sql.DbUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ALL_Patient_Info extends JFrame {

    JTable table;
    JButton backBtn, searchBtn;
    JTextField searchField;

    ALL_Patient_Info() {
        // Frame setup
        setTitle("Filostack - All Patients");
        setSize(1000, 600);
        setLocationRelativeTo(null);   // Center screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(1000, 70));

        JLabel title = new JLabel("All Patient Information", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/hospital.png"));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(img));
            logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            headerPanel.add(logo, BorderLayout.WEST);
        } catch (Exception ignore) {
            // ignore if no icon
        }

        add(headerPanel, BorderLayout.NORTH);

        // 🔍 Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(new Color(240, 240, 240));

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        searchBtn = new JButton("Go");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setBackground(new Color(25, 118, 210));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        table = new JTable() {
            // Zebra striping effect
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

        loadTableData("SELECT * FROM Patient_Info"); // Load all initially
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(950, 60));

        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backBtn.setBackground(new Color(25, 118, 210));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // 🔙 Back Button Action
        backBtn.addActionListener(_ -> setVisible(false));

        // 🔎 Search Button Action
        searchBtn.addActionListener(_ -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadTableData("SELECT * FROM Patient_Info");
            } else {
                searchPatients(keyword);
            }
        });

        setVisible(true);
    }

    // Load data into JTable
    private void loadTableData(String query) {
        try {
            conn c = new conn();
            try (ResultSet resultSet = c.statement.executeQuery(query)) {
                table.setModel(DbUtils.resultSetToTableModel(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dynamic search across ALL columns
    private void searchPatients(String keyword) {
        try {
            conn c = new conn();

            // Get column names dynamically
            DatabaseMetaData meta = c.connection.getMetaData();
            try (ResultSet rsCols = meta.getColumns(null, null, "Patient_Info", null)) {
                ArrayList<String> columns = new ArrayList<>();
                while (rsCols.next()) {
                    columns.add(rsCols.getString("COLUMN_NAME"));
                }

                // Build query dynamically
                StringBuilder sql = new StringBuilder("SELECT * FROM Patient_Info WHERE ");
                for (int i = 0; i < columns.size(); i++) {
                    sql.append(columns.get(i)).append(" LIKE ?");
                    if (i < columns.size() - 1) {
                        sql.append(" OR ");
                    }
                }

                try (PreparedStatement ps = c.connection.prepareStatement(sql.toString())) {
                    for (int i = 1; i <= columns.size(); i++) {
                        ps.setString(i, "%" + keyword + "%");
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        table.setModel(DbUtils.resultSetToTableModel(rs));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ALL_Patient_Info();
    }
}
