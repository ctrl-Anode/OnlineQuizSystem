package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

public class ManageQuestionsDialog extends JDialog {
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

    private int quizId;
    private DefaultListModel<String> questionListModel;
    private JList<String> questionList;
    private JLabel questionCountLabel;

    /**
     * Launch for testing
     */
    public static void main(String[] args) {
        try {
            ManageQuestionsDialog dialog = new ManageQuestionsDialog(1);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog
     */

    public ManageQuestionsDialog(int quizId) {
        this.quizId = quizId;

        setTitle("📝 Manage Questions");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setModal(true);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        getContentPane().setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = createContentPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        loadQuestions();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📝 Question Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Quiz ID: " + quizId);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Right side - Question count
        questionCountLabel = new JLabel("0 Questions");
        questionCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        questionCountLabel.setForeground(Color.WHITE);
        questionCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(questionCountLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 25, 10, 25));

        // Create styled question list
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        
        // Style the list
        questionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionList.setBackground(CARD_COLOR);
        questionList.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        questionList.setSelectionForeground(TEXT_COLOR);
        questionList.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        questionList.setFixedCellHeight(60);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom cell renderer for better styling
        questionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                
                if (!isSelected) {
                    setBackground(index % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                
                // Format the display text
                if (value != null) {
                    String text = value.toString();
                    if (text.contains(":")) {
                        String[] parts = text.split(":", 2);
                        setText("<html><div style='width: 700px;'><b>Q" + parts[0] + ":</b> " + parts[1].trim() + "</div></html>");
                    }
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

        JLabel listHeaderLabel = new JLabel("Questions");
        listHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listHeaderLabel.setForeground(Color.WHITE);

        listHeaderPanel.add(listHeaderLabel, BorderLayout.WEST);
        listContainer.add(listHeaderPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(questionList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        listContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listContainer, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("➕ Add Question", SUCCESS_COLOR);
        JButton editButton = createStyledButton("✏️ Edit Question", WARNING_COLOR);
        JButton deleteButton = createStyledButton("🗑️ Delete Question", DANGER_COLOR);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Close button
        JButton closeButton = createStyledButton("✖️ Close", SECONDARY_COLOR);
        closeButton.addActionListener(e -> dispose());

        // Actions
        addButton.addActionListener(e -> openQuestionDialog(null));
        editButton.addActionListener(e -> {
            String selected = questionList.getSelectedValue();
            if (selected != null) {
                openQuestionDialog(extractQuestionId(selected));
            } else {
                showStyledMessage("Please select a question to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> deleteQuestion());

        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        footerPanel.add(closeButton, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 45));

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

    private void loadQuestions() {
        questionListModel.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT question_id, question_text FROM questions WHERE quiz_id = ? ORDER BY question_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                questionListModel.addElement(rs.getInt("question_id") + ": " + rs.getString("question_text"));
                count++;
            }
            
            // Update question count
            questionCountLabel.setText(count + " Question" + (count != 1 ? "s" : ""));
            
        } catch (Exception e) {
            showStyledMessage("Error loading questions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int extractQuestionId(String questionText) {
        return Integer.parseInt(questionText.split(":")[0]);
    }

    private void openQuestionDialog(Integer questionId) {
        // Create modern dialog
        JDialog dialog = new JDialog(this, questionId == null ? "Add New Question" : "Edit Question", true);
        dialog.setSize(750, 650);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel(questionId == null ? "➕ Add New Question" : "✏️ Edit Question");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Main content panel with proper layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Question text section
        JPanel questionSection = new JPanel();
        questionSection.setLayout(new BoxLayout(questionSection, BoxLayout.Y_AXIS));
        questionSection.setOpaque(false);
        questionSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel questionLabel = new JLabel("Question Text:");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea questionTextArea = new JTextArea(4, 50);
        questionTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionTextArea.setBackground(Color.WHITE);
        questionTextArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);

        JScrollPane questionScroll = new JScrollPane(questionTextArea);
        questionScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        questionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScroll.setMaximumSize(new Dimension(650, 120));
        questionScroll.setPreferredSize(new Dimension(650, 120));

        questionSection.add(questionLabel);
        questionSection.add(Box.createRigidArea(new Dimension(0, 8)));
        questionSection.add(questionScroll);

        // Question type section
        JPanel typeSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typeSection.setOpaque(false);
        typeSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeSection.setMaximumSize(new Dimension(650, 40));

        JLabel typeLabel = new JLabel("Question Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(TEXT_COLOR);

        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{
                "multiple_choice", "true_false", "short_answer"
        });
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeComboBox.setBackground(Color.WHITE);
        typeComboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        typeSection.add(typeLabel);
        typeSection.add(Box.createRigidArea(new Dimension(15, 0)));
        typeSection.add(typeComboBox);

        // Options section with proper layout
        JPanel optionContainer = new JPanel();
        optionContainer.setLayout(new BoxLayout(optionContainer, BoxLayout.Y_AXIS));
        optionContainer.setOpaque(false);
        optionContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel optionsLabel = new JLabel("Answer Options:");
        optionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        optionsLabel.setForeground(TEXT_COLOR);
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.setBackground(new Color(249, 250, 251));
        optionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane optionScroll = new JScrollPane(optionPanel);
        optionScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        optionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionScroll.setMaximumSize(new Dimension(650, 200));
        optionScroll.setPreferredSize(new Dimension(650, 200));
        optionScroll.getViewport().setBackground(new Color(249, 250, 251));

        optionContainer.add(optionsLabel);
        optionContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        optionContainer.add(optionScroll);

        // Add all sections to main panel with proper spacing
        mainPanel.add(questionSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(typeSection);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(optionContainer);

        ArrayList<JTextField> optionFields = new ArrayList<>();
        ArrayList<JCheckBox> correctBoxes = new ArrayList<>();

        // Load existing data if editing
        if (questionId != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT question_text, question_type FROM questions WHERE question_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, questionId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    questionTextArea.setText(rs.getString("question_text"));
                    typeComboBox.setSelectedItem(rs.getString("question_type"));
                }

                String optSql = "SELECT option_text, is_correct FROM options WHERE question_id = ? ORDER BY option_id";
                PreparedStatement optStmt = conn.prepareStatement(optSql);
                optStmt.setInt(1, questionId);
                ResultSet optRs = optStmt.executeQuery();
                while (optRs.next()) {
                    JTextField optField = new JTextField(optRs.getString("option_text"));
                    optField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    optField.setPreferredSize(new Dimension(450, 35));
                    
                    JCheckBox correctBox = new JCheckBox("Correct Answer", optRs.getBoolean("is_correct"));
                    correctBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    correctBox.setForeground(SUCCESS_COLOR);
                    
                    optionFields.add(optField);
                    correctBoxes.add(correctBox);
                    
                    JPanel p = createOptionPanel(optField, correctBox);
                    optionPanel.add(p);
                }
            } catch (Exception e) {
                showStyledMessage("Error loading question: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        dialog.add(mainPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        JButton addOptionButton = createStyledButton("+ Add Option", PRIMARY_COLOR);
        addOptionButton.addActionListener((ActionEvent e) -> {
            JTextField optField = new JTextField();
            optField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            optField.setPreferredSize(new Dimension(450, 35));
            
            JCheckBox correctBox = new JCheckBox("Correct Answer");
            correctBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
            correctBox.setForeground(SUCCESS_COLOR);
            
            optionFields.add(optField);
            correctBoxes.add(correctBox);
            
            JPanel p = createOptionPanel(optField, correctBox);
            optionPanel.add(p);
            optionPanel.revalidate();
            optionPanel.repaint();
        });

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonGroup.setOpaque(false);

        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createStyledButton(questionId == null ? "Create" : "Update", SUCCESS_COLOR);
        saveButton.addActionListener(e -> {
            if (saveQuestion(questionId, questionTextArea.getText(), 
                           typeComboBox.getSelectedItem().toString(), 
                           optionFields, correctBoxes)) {
                dialog.dispose();
            }
        });

        buttonGroup.add(cancelButton);
        buttonGroup.add(saveButton);

        footerPanel.add(addOptionButton, BorderLayout.WEST);
        footerPanel.add(buttonGroup, BorderLayout.EAST);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createOptionPanel(JTextField optField, JCheckBox correctBox) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        p.setMaximumSize(new Dimension(600, 45));
        p.setPreferredSize(new Dimension(600, 45));
        
        optField.setPreferredSize(new Dimension(450, 35));
        correctBox.setPreferredSize(new Dimension(120, 35));
        
        p.add(optField, BorderLayout.CENTER);
        p.add(correctBox, BorderLayout.EAST);
        return p;
    }

    private boolean saveQuestion(Integer questionId, String text, String type,
                              ArrayList<JTextField> optionFields, ArrayList<JCheckBox> correctBoxes) {
        if (text.trim().isEmpty()) {
            showStyledMessage("Question text cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int qId;
            if (questionId == null) {
                String sql = "INSERT INTO questions (quiz_id, question_text, question_type) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, quizId);
                stmt.setString(2, text.trim());
                stmt.setString(3, type);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                qId = keys.getInt(1);
                showStyledMessage("Question created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sql = "UPDATE questions SET question_text = ?, question_type = ? WHERE question_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, text.trim());
                stmt.setString(2, type);
                stmt.setInt(3, questionId);
                stmt.executeUpdate();
                qId = questionId;

                String delSql = "DELETE FROM options WHERE question_id = ?";
                PreparedStatement delStmt = conn.prepareStatement(delSql);
                delStmt.setInt(1, questionId);
                delStmt.executeUpdate();
                showStyledMessage("Question updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if ("multiple_choice".equals(type) || "true_false".equals(type)) {
                for (int i = 0; i < optionFields.size(); i++) {
                    String optText = optionFields.get(i).getText().trim();
                    boolean correct = correctBoxes.get(i).isSelected();
                    if (!optText.isEmpty()) {
                        String insSql = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
                        PreparedStatement insStmt = conn.prepareStatement(insSql);
                        insStmt.setInt(1, qId);
                        insStmt.setString(2, optText);
                        insStmt.setBoolean(3, correct);
                        insStmt.executeUpdate();
                    }
                }
            }

            loadQuestions();
            return true;
        } catch (Exception e) {
            showStyledMessage("Error saving question: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteQuestion() {
        String selected = questionList.getSelectedValue();
        if (selected == null) {
            showStyledMessage("Please select a question to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int questionId = extractQuestionId(selected);
        String questionText = selected.substring(selected.indexOf(":") + 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this question?\n\n\"" + questionText + "\"\n\nThis action cannot be undone.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM questions WHERE question_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, questionId);
                stmt.executeUpdate();
                loadQuestions();
                showStyledMessage("Question deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showStyledMessage("Error deleting question: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
