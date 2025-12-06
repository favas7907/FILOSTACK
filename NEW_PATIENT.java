package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class NEW_PATIENT extends JFrame {

    JComboBox<String> idTypeComboBox;
    JTextField idNumberField, nameField, diseaseField, depositField;
    JRadioButton maleRadio, femaleRadio;
    Choice roomChoice;
    JLabel timeLabel;
    JButton addBtn, backBtn;

    public NEW_PATIENT() {
        setTitle("Filostack - New Patient");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 118, 210));
        headerPanel.setPreferredSize(new Dimension(850, 70));

        JLabel title = new JLabel("New Patient Registration", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/patient.png"));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(img));
            logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            headerPanel.add(logo, BorderLayout.WEST);
        } catch (Exception ignore) {}

        add(headerPanel, BorderLayout.NORTH);

        // 🔹 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        // Row 1 - ID Type
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idTypeLbl = new JLabel("ID Type:", JLabel.RIGHT);
        idTypeLbl.setFont(labelFont);
        formPanel.add(idTypeLbl, gbc);

        idTypeComboBox = new JComboBox<>(new String[]{"Aadhar Card", "Voter ID", "Driving License"});
        idTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(idTypeComboBox, gbc);

        // Row 2 - ID Number
        gbc.gridx = 0; gbc.gridy++;
        JLabel idNumLbl = new JLabel("ID Number:", JLabel.RIGHT);
        idNumLbl.setFont(labelFont);
        formPanel.add(idNumLbl, gbc);

        idNumberField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(idNumberField, gbc);

        // Row 3 - Name
        gbc.gridx = 0; gbc.gridy++;
        JLabel nameLbl = new JLabel("Name:", JLabel.RIGHT);
        nameLbl.setFont(labelFont);
        formPanel.add(nameLbl, gbc);

        nameField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Row 4 - Gender
        gbc.gridx = 0; gbc.gridy++;
        JLabel genderLbl = new JLabel("Gender:", JLabel.RIGHT);
        genderLbl.setFont(labelFont);
        formPanel.add(genderLbl, gbc);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderPanel.setBackground(Color.WHITE);
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setBackground(Color.WHITE);
        femaleRadio.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup();
        bg.add(maleRadio); bg.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        gbc.gridx = 1;
        formPanel.add(genderPanel, gbc);

        // Row 5 - Disease
        gbc.gridx = 0; gbc.gridy++;
        JLabel diseaseLbl = new JLabel("Disease:", JLabel.RIGHT);
        diseaseLbl.setFont(labelFont);
        formPanel.add(diseaseLbl, gbc);

        diseaseField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(diseaseField, gbc);

        // Row 6 - Room
        gbc.gridx = 0; gbc.gridy++;
        JLabel roomLbl = new JLabel("Room:", JLabel.RIGHT);
        roomLbl.setFont(labelFont);
        formPanel.add(roomLbl, gbc);

        roomChoice = new Choice();
        try {
            conn c = new conn();
            ResultSet rs = c.connection.createStatement().executeQuery(
                    "SELECT room_no FROM Room WHERE Availability='Available'");
            while (rs.next()) {
                roomChoice.add(rs.getString("room_no"));
            }
            rs.close();
            c.connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        gbc.gridx = 1;
        formPanel.add(roomChoice, gbc);

        // Row 7 - Time
        gbc.gridx = 0; gbc.gridy++;
        JLabel timeLbl = new JLabel("Time:", JLabel.RIGHT);
        timeLbl.setFont(labelFont);
        formPanel.add(timeLbl, gbc);

        timeLabel = new JLabel(new Date().toString());
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        formPanel.add(timeLabel, gbc);

        // Row 8 - Deposit
        gbc.gridx = 0; gbc.gridy++;
        JLabel depositLbl = new JLabel("Deposit:", JLabel.RIGHT);
        depositLbl.setFont(labelFont);
        formPanel.add(depositLbl, gbc);

        depositField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(depositField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 🔹 Button Panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 240, 240));
        btnPanel.setPreferredSize(new Dimension(850, 70));

        addBtn = new JButton("Add Patient");
        styleButton(addBtn);
        backBtn = new JButton("Back");
        styleButton(backBtn);

        btnPanel.add(addBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Action Listeners (Java 7 compatible)
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(25, 118, 210));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addPatient() {
        String idNum = idNumberField.getText().trim();
        String name = nameField.getText().trim();
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
        String disease = diseaseField.getText().trim();
        String room = roomChoice.getSelectedItem();
        String time = timeLabel.getText();
        String deposit = depositField.getText().trim();

        if (idNum.isEmpty() || name.isEmpty() || gender.isEmpty() || disease.isEmpty() || deposit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            conn c = new conn();
            String q = "INSERT INTO Patient_Info (number, name, gender, disease, Room_Number, Time, deposit) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = c.connection.prepareStatement(q);
            pst.setString(1, idNum);
            pst.setString(2, name);
            pst.setString(3, gender);
            pst.setString(4, disease);
            pst.setString(5, room);
            pst.setString(6, time);
            pst.setString(7, deposit);
            int rows = pst.executeUpdate();

            PreparedStatement pst2 = c.connection.prepareStatement(
                    "UPDATE Room SET Availability='Occupied' WHERE room_no=?");
            pst2.setString(1, room);
            pst2.executeUpdate();

            pst.close();
            pst2.close();
            c.connection.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Patient Added Successfully!");
                setVisible(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NEW_PATIENT();
    }
}
