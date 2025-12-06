package hospital.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JTextField textField;
    JPasswordField jPasswordField;
    JButton b1, b2;

    Login() {
        setTitle("Filostack - Login");
        setSize(520, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setUndecorated(true);

        // Gradient background
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 200, 210),
                        0, getHeight(), Color.white);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bgPanel.setBounds(0, 0, 520, 450);
        bgPanel.setLayout(null);
        add(bgPanel);

        // Card panel
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(60, 40, 400, 370);   // taller card for logo space
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(109, 164, 170), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        bgPanel.add(card);

        // Bigger Logo
        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/login.png"));
        Image i1 = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(i1));
        logo.setBounds(140, 10, 120, 120);   // centered top
        card.add(logo);

        // Title (Filostack)
        JLabel title = new JLabel("Filostack");
        title.setBounds(0, 135, 400, 40);
        title.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 30));
        title.setForeground(new Color(50, 100, 120));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title);

        // Username Label
        JLabel namelabel = new JLabel("Username");
        namelabel.setBounds(50, 190, 100, 25);
        namelabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        namelabel.setForeground(new Color(80, 130, 140));
        card.add(namelabel);

        // Username Field
        textField = new JTextField();
        textField.setBounds(160, 190, 190, 28);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(new Color(240, 248, 255));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(109, 164, 170), 1, true),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        card.add(textField);

        // Password Label
        JLabel password = new JLabel("Password");
        password.setBounds(50, 230, 100, 25);
        password.setFont(new Font("Segoe UI", Font.BOLD, 14));
        password.setForeground(new Color(80, 130, 140));
        card.add(password);

        // Password Field
        jPasswordField = new JPasswordField();
        jPasswordField.setBounds(160, 230, 190, 28);
        jPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jPasswordField.setBackground(new Color(240, 248, 255));
        jPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(109, 164, 170), 1, true),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        card.add(jPasswordField);

        // Login Button
        b1 = new JButton("Login");
        b1.setBounds(85, 290, 110, 38);
        b1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b1.setBackground(new Color(109, 164, 170));
        b1.setForeground(Color.white);
        b1.setFocusPainted(false);
        b1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b1.setBorder(BorderFactory.createEmptyBorder());
        b1.addActionListener(this);
        b1.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b1.setBackground(new Color(80, 130, 140)); }
            public void mouseExited(MouseEvent e) { b1.setBackground(new Color(109, 164, 170)); }
        });
        card.add(b1);

        // Cancel Button
        b2 = new JButton("Cancel");
        b2.setBounds(215, 290, 110, 38);
        b2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b2.setBackground(new Color(220, 80, 80));
        b2.setForeground(Color.white);
        b2.setFocusPainted(false);
        b2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b2.setBorder(BorderFactory.createEmptyBorder());
        b2.addActionListener(this);
        b2.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b2.setBackground(new Color(180, 50, 50)); }
            public void mouseExited(MouseEvent e) { b2.setBackground(new Color(220, 80, 80)); }
        });
        card.add(b2);

        // Footer
        JLabel footer = new JLabel("© 2025 Filostack");
        footer.setBounds(0, 425, 520, 20);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(80, 120, 130));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        bgPanel.add(footer);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            String user = textField.getText().trim();
            String pass = new String(jPasswordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
                return;
            }

            try {
                conn c = new conn();
                String q = "SELECT * FROM login WHERE ID = ? AND PW = ?";
                PreparedStatement pst = c.connection.prepareStatement(q);
                pst.setString(1, user);
                pst.setString(2, pass);

                ResultSet resultSet = pst.executeQuery();

                if (resultSet.next()) {
                    new Reception();
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password.");
                }

                resultSet.close();
                pst.close();
                c.connection.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error connecting to database.");
                ex.printStackTrace();
            }

        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
