package hospital.management.system;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class PatientDischarge extends JFrame {

    private JComboBox<String> patientChoice;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JLabel outTimeLabel;
    private JButton checkBtn, dischargeBtn, backBtn, showAllBtn;

    public PatientDischarge() {
        setTitle("Filostack - Patient Discharge");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 136, 229));
        headerPanel.setPreferredSize(new Dimension(950, 70));

        JLabel title = new JLabel(" Patient Discharge", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // 🔹 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        // Row 1 - Patient ID Search Box
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel patientIdLbl = new JLabel("Patient ID:", JLabel.RIGHT);
        patientIdLbl.setFont(labelFont);
        formPanel.add(patientIdLbl, gbc);

        patientChoice = new JComboBox<>();
        patientChoice.setEditable(true);
        patientChoice.setPreferredSize(new Dimension(200, 28));
        loadPatientIDs();
        gbc.gridx = 1;
        formPanel.add(patientChoice, gbc);

        // Row 2 - Table
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Patient ID", "Name", "Room", "Disease", "In Time"});
        patientTable = new JTable(tableModel);

        // Style Table
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        patientTable.setRowHeight(28);
        patientTable.setSelectionBackground(new Color(200, 230, 255));

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(850, 250));
        formPanel.add(scrollPane, gbc);

        // Row 3 - Out Time
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel outLbl = new JLabel("Out Time:", JLabel.RIGHT);
        outLbl.setFont(labelFont);
        formPanel.add(outLbl, gbc);

        outTimeLabel = new JLabel(new Date().toString());
        outTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(outTimeLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 🔹 Button Panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.setPreferredSize(new Dimension(950, 70));

        checkBtn = createStyledButton("Check Details");
        dischargeBtn = createStyledButton("Discharge");
        showAllBtn = createStyledButton("Show All Patients");
        backBtn = createStyledButton("Back");

        btnPanel.add(checkBtn);
        btnPanel.add(dischargeBtn);
        btnPanel.add(showAllBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        checkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkPatient();
            }
        });

        dischargeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dischargePatient();
            }
        });

        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAllPatients();
            }
        });

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Live refresh OutTime when combo changes
        patientChoice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outTimeLabel.setText(new Date().toString());
            }
        });

        // Load all patients initially
        loadAllPatients();

        setVisible(true);
    }

    // 🔹 Load Patient IDs
    private void loadPatientIDs() {
        try {
            conn c = new conn();
            ResultSet rs = c.connection.createStatement().executeQuery("SELECT number FROM patient_info");
            patientChoice.removeAllItems();
            while (rs.next()) {
                patientChoice.addItem(rs.getString("number"));
            }
            rs.close();
            c.connection.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patient IDs: " + ex.getMessage());
        }
    }

    // 🔹 Load all patients
    private void loadAllPatients() {
        try {
            conn c = new conn();
            ResultSet rs = c.connection.createStatement().executeQuery(
                    "SELECT number, name, Room_Number, disease, Time FROM patient_info");

            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("number"),
                        rs.getString("name"),
                        rs.getString("Room_Number"),
                        rs.getString("disease"),
                        rs.getString("Time")
                });
            }

            rs.close();
            c.connection.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + ex.getMessage());
        }
    }

    // 🔹 Check details
    private void checkPatient() {
        String pid = (String) patientChoice.getSelectedItem();
        if (pid == null || pid.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID.");
            return;
        }
        try {
            conn c = new conn();
            PreparedStatement pst = c.connection.prepareStatement(
                    "SELECT number, name, Room_Number, disease, Time FROM patient_info WHERE number=?");
            pst.setString(1, pid);
            ResultSet rs = pst.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("number"),
                        rs.getString("name"),
                        rs.getString("Room_Number"),
                        rs.getString("disease"),
                        rs.getString("Time")
                });
            }

            rs.close();
            pst.close();
            c.connection.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error checking patient: " + ex.getMessage());
        }
    }

    // 🔹 Discharge patient (just delete from patient_info + free room)
    private void dischargePatient() {
        String pid = (String) patientChoice.getSelectedItem();
        if (pid == null || pid.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to discharge Patient " + pid + "?",
                "Confirm Discharge", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            conn c = new conn();

            // Get room no
            PreparedStatement pst1 = c.connection.prepareStatement(
                    "SELECT Room_Number FROM patient_info WHERE number=?");
            pst1.setString(1, pid);
            ResultSet rs = pst1.executeQuery();

            String roomNo = "";
            if (rs.next()) roomNo = rs.getString("Room_Number");
            rs.close();
            pst1.close();

            // Delete from active patients
            PreparedStatement pst = c.connection.prepareStatement(
                    "DELETE FROM patient_info WHERE number=?");
            pst.setString(1, pid);
            pst.executeUpdate();

            // Free room
            if (!roomNo.isEmpty()) {
                PreparedStatement pst2 = c.connection.prepareStatement(
                        "UPDATE room SET Availability='Available' WHERE room_no=?");
                pst2.setString(1, roomNo);
                pst2.executeUpdate();
                pst2.close();
            }

            pst.close();
            c.connection.close();

            JOptionPane.showMessageDialog(this, "Patient discharged successfully!");

            // Refresh
            loadPatientIDs();
            loadAllPatients();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error discharging patient: " + ex.getMessage());
        }
    }

    // 🔹 Styled Button
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(33, 150, 243));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        new PatientDischarge();
    }
}
