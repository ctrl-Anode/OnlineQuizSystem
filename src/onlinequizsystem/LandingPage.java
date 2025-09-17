package onlinequizsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPage extends JPanel {

    private Main mainFrame;

    public LandingPage(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250)); // soft background

        // Title
        JLabel title = new JLabel("Welcome to Online Quiz System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        add(title, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel subtitle = new JLabel("Test your knowledge and track your progress", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40))); // spacing

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 40, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));

        JButton loginBtn = createStyledButton("Login", new Color(52, 152, 219));
        JButton registerBtn = createStyledButton("Register", new Color(46, 204, 113));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Online Quiz System", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(new Color(120, 120, 120));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(footer, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> mainFrame.showPanel(Main.LOGIN_PANEL));
        registerBtn.addActionListener(e -> mainFrame.showPanel(Main.REGISTER_PANEL));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded look
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
        button.setContentAreaFilled(true);

        return button;
    }
}
