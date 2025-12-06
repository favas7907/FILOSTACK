package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Reception extends JFrame {

    public Reception() {
        setTitle("Filostack - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(21, 101, 192),
                        getWidth(), getHeight(), new Color(25, 118, 210));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1280, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(13, 71, 161)));

        JLabel title = new JLabel("Filostack Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        try {
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/hospital.png"));
            Image scaled = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(scaled));
            logo.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
            header.add(logo, BorderLayout.WEST);
        } catch (Exception ignore) {}

        add(header, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243),
                        0, getHeight(), new Color(13, 71, 161));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(270, getHeight()));
        sidebar.setLayout(new GridLayout(11, 1, 0, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        String[] btnNames = {
            "Add New Patient", "Room", "Department", "All Employee Info",
            "Patient Info", "Patient Discharge", "Update Patient Details",
            "Hospital Ambulance", "Search Room", "Logout"
        };

        final JButton[] buttons = new JButton[btnNames.length];
        final Color normalColor = new Color(25, 118, 210);
        final Color hoverColor = new Color(13, 71, 161);

        for (int i = 0; i < btnNames.length; i++) {
            buttons[i] = new JButton(btnNames[i]);
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 15));
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(normalColor);
            buttons[i].setOpaque(true);
            buttons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(21, 101, 192), 1),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
            ));
            buttons[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final JButton b = buttons[i];
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    b.setBackground(hoverColor);
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
                            BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
                public void mouseExited(MouseEvent e) {
                    b.setBackground(normalColor);
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(21, 101, 192), 1),
                            BorderFactory.createEmptyBorder(12, 20, 12, 20)
                    ));
                }
            });
            sidebar.add(buttons[i]);
        }

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel welcome = new JLabel("<html><center>Welcome to <b>Hospital Management System</b><br><br>" +
                "Use the navigation menu to access hospital modules,<br>" +
                "manage patients, employees, rooms, and departments.<br><br>" +
                "Your one-stop dashboard for hospital operations.</center></html>", JLabel.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        welcome.setForeground(new Color(70, 70, 70));

        try {
            ImageIcon banner = new ImageIcon(ClassLoader.getSystemResource("icons/dashboard_bg.png"));
            Image img = banner.getImage().getScaledInstance(460, 300, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);

            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            content.add(welcome, BorderLayout.NORTH);
            content.add(imageLabel, BorderLayout.CENTER);

            mainPanel.add(content, BorderLayout.CENTER);
        } catch (Exception e) {
            mainPanel.add(welcome, BorderLayout.CENTER);
        }

        add(mainPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTIONS =====
        buttons[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NEW_PATIENT();
            }
        });

        buttons[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Room();
            }
        });

        buttons[2].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Department();
            }
        });

        buttons[3].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Employee_info();
            }
        });

        buttons[4].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ALL_Patient_Info();
            }
        });

        buttons[5].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PatientDischarge();
            }
        });

        buttons[6].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new update_patient_details();
            }
        });

        buttons[7].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Ambulance();
            }
        });

        buttons[8].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchRoom();
            }
        });

        buttons[9].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to logout?",
                        "Logout Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    setVisible(false);
                    new Login();
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new Reception();
    }
}
