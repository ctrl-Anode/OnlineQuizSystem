package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ManageQuizzesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    
    private int instructorId;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;
    private JLabel quizCountLabel;

    public ManageQuizzesPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        loadQuizzes();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📋 Quiz Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Create, edit, and manage your quizzes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Right side - Quiz count
        quizCountLabel = new JLabel("0 Quizzes");
        quizCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quizCountLabel.setForeground(PRIMARY_COLOR);
        quizCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(quizCountLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // Create styled quiz list
        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        
        // Style the list
        quizList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        quizList.setBackground(CARD_COLOR);
        quizList.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        quizList.setSelectionForeground(TEXT_COLOR);
        quizList.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        quizList.setFixedCellHeight(50);
        
        // Custom cell renderer for better styling
        quizList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                
                if (!isSelected) {
                    setBackground(index % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                
                // Add quiz icon
                if (value != null) {
                    setText("📝 " + value.toString());
                }
                
                return c;
            }
        });

        // Container with card styling
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(CARD_COLOR);
        listContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header for the list
        JPanel listHeaderPanel = new JPanel(new BorderLayout());
        listHeaderPanel.setBackground(SECONDARY_COLOR);
        listHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel listHeaderLabel = new JLabel("Your Quizzes");
        listHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listHeaderLabel.setForeground(Color.WHITE);

        listHeaderPanel.add(listHeaderLabel, BorderLayout.WEST);
        listContainer.add(listHeaderPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        listContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listContainer, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Action buttons row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setOpaque(false);

        JButton createBtn = createStyledButton("➕ Create Quiz", SUCCESS_COLOR);
        JButton editBtn = createStyledButton("✏️ Edit Quiz", WARNING_COLOR);
        JButton deleteBtn = createStyledButton("🗑️ Delete Quiz", DANGER_COLOR);
        JButton manageQuestionsBtn = createStyledButton("❓ Manage Questions", PRIMARY_COLOR);

        actionPanel.add(createBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(manageQuestionsBtn);

        buttonPanel.add(actionPanel);

        // Button actions
        createBtn.addActionListener(e -> openQuizDialog(null));
        editBtn.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) openQuizDialog(extractQuizId(selected));
            else JOptionPane.showMessageDialog(this, "Select a quiz to edit.");
        });
        deleteBtn.addActionListener(e -> deleteQuiz());
        manageQuestionsBtn.addActionListener(e -> {
            String selected = quizList.getSelectedValue();
            if (selected != null) {
                int quizId = extractQuizId(selected);
                ManageQuestionsDialog dialog = new ManageQuestionsDialog(quizId);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setVisible(true); 
            } else {
                JOptionPane.showMessageDialog(this, "Select a quiz first.");
            }
        });

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 45));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadQuizzes() {
        quizListModel.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT quiz_id, title FROM quizzes WHERE created_by = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                quizListModel.addElement(rs.getInt("quiz_id") + ": " + rs.getString("title"));
                count++;
            }
            
            // Update quiz count
            quizCountLabel.setText(count + " Quiz" + (count != 1 ? "zes" : ""));
            
        } catch (Exception e) {
            showStyledMessage("Error loading quizzes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int extractQuizId(String text) {
        return Integer.parseInt(text.split(":")[0]);
    }

    private void openQuizDialog(Integer quizId) {
        // Create modern dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            quizId == null ? "Create New Quiz" : "Edit Quiz", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel(quizId == null ? "📝 Create New Quiz" : "✏️ Edit Quiz");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title field
        JLabel titleFieldLabel = new JLabel("Quiz Title:");
        titleFieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleFieldLabel.setForeground(TEXT_COLOR);

        JTextField titleField = new JTextField(20);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        titleField.setBackground(Color.WHITE);
        titleField.setPreferredSize(new Dimension(400, 40));

        // Description field
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(TEXT_COLOR);

        JTextArea descArea = new JTextArea(5, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        descArea.setBackground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScrollPane.setPreferredSize(new Dimension(400, 120));

        // Load existing data if editing
        if (quizId != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT title, description FROM quizzes WHERE quiz_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quizId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    titleField.setText(rs.getString("title"));
                    descArea.setText(rs.getString("description"));
                }
            } catch (Exception e) {
                showStyledMessage("Error loading quiz: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        contentPanel.add(titleFieldLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(titleField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(descScrollPane);

        dialog.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createStyledButton(quizId == null ? "Create" : "Update", SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            
            if (title.isEmpty()) {
                showStyledMessage("Quiz title cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                if (quizId == null) {
                    String sql = "INSERT INTO quizzes (title, description, created_by) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, title);
                    stmt.setString(2, desc);
                    stmt.setInt(3, instructorId);
                    stmt.executeUpdate();
                    showStyledMessage("Quiz created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String sql = "UPDATE quizzes SET title = ?, description = ? WHERE quiz_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, title);
                    stmt.setString(2, desc);
                    stmt.setInt(3, quizId);
                    stmt.executeUpdate();
                    showStyledMessage("Quiz updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                loadQuizzes();
                dialog.dispose();
            } catch (Exception ex) {
                showStyledMessage("Error saving quiz: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        footerPanel.add(cancelButton);
        footerPanel.add(saveButton);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteQuiz() {
        String selected = quizList.getSelectedValue();
        if (selected == null) {
            showStyledMessage("Please select a quiz to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quizId = extractQuizId(selected);
        String quizTitle = selected.substring(selected.indexOf(":") + 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the quiz:\n\"" + quizTitle + "\"?\n\nThis action cannot be undone.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quizId);
                stmt.executeUpdate();
                loadQuizzes();
                showStyledMessage("Quiz deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showStyledMessage("Error deleting quiz: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
