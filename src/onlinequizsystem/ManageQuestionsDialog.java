package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class ManageQuestionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	//private final JPanel contentPanel = new JPanel();

    private int quizId;

    private DefaultListModel<String> questionListModel;
    private JList<String> questionList;
    

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

        setTitle("Manage Questions for Quiz ID: " + quizId);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Questions list
        questionListModel = new DefaultListModel<>();
        questionList = new JList<>(questionListModel);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(questionList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Questions"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Question");
        JButton editButton = new JButton("Edit Question");
        JButton deleteButton = new JButton("Delete Question");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadQuestions();

        // Actions
        addButton.addActionListener(e -> openQuestionDialog(null));
        editButton.addActionListener(e -> {
            String selected = questionList.getSelectedValue();
            if (selected != null) {
                openQuestionDialog(extractQuestionId(selected));
            } else {
                JOptionPane.showMessageDialog(this, "Select a question to edit.");
            }
        });
        deleteButton.addActionListener(e -> deleteQuestion());

        setVisible(true);
    }

    private void loadQuestions() {
        questionListModel.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT question_id, question_text FROM questions WHERE quiz_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questionListModel.addElement(rs.getInt("question_id") + ": " + rs.getString("question_text"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage());
        }
    }

    private int extractQuestionId(String questionText) {
        return Integer.parseInt(questionText.split(":")[0]);
    }

    private void openQuestionDialog(Integer questionId) {
        // Main dialog panel
        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Question text
        JTextArea questionTextArea = new JTextArea(5, 40);
        JScrollPane questionScroll = new JScrollPane(questionTextArea);

        JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
        questionPanel.add(new JLabel("Question Text:"), BorderLayout.NORTH);
        questionPanel.add(questionScroll, BorderLayout.CENTER);

        // Question type
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{
                "multiple_choice", "true_false", "short_answer"
        });
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel("Question Type:"));
        typePanel.add(typeComboBox);
        questionPanel.add(typePanel, BorderLayout.SOUTH);

        dialogPanel.add(questionPanel, BorderLayout.NORTH);

        // Options
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        JScrollPane optionScroll = new JScrollPane(optionPanel);
        optionScroll.setPreferredSize(new Dimension(700, 200));
        dialogPanel.add(optionScroll, BorderLayout.CENTER);

        ArrayList<JTextField> optionFields = new ArrayList<>();
        ArrayList<JCheckBox> correctBoxes = new ArrayList<>();

        // If editing, load existing
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

                String optSql = "SELECT option_text, is_correct FROM options WHERE question_id = ?";
                PreparedStatement optStmt = conn.prepareStatement(optSql);
                optStmt.setInt(1, questionId);
                ResultSet optRs = optStmt.executeQuery();
                while (optRs.next()) {
                    JTextField optField = new JTextField(optRs.getString("option_text"), 30);
                    JCheckBox correctBox = new JCheckBox("Correct", optRs.getBoolean("is_correct"));
                    optionFields.add(optField);
                    correctBoxes.add(correctBox);
                    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    p.add(optField);
                    p.add(correctBox);
                    optionPanel.add(p);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading question: " + e.getMessage());
            }
        }

        // Add option button
        JButton addOptionButton = new JButton("Add Option");
        addOptionButton.addActionListener((ActionEvent e) -> {
            JTextField optField = new JTextField(30);
            JCheckBox correctBox = new JCheckBox("Correct");
            optionFields.add(optField);
            correctBoxes.add(correctBox);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.add(optField);
            p.add(correctBox);
            optionPanel.add(p);
            optionPanel.revalidate();
        });
        dialogPanel.add(addOptionButton, BorderLayout.SOUTH);

        // Show dialog
        int option = JOptionPane.showConfirmDialog(
                this,
                dialogPanel,
                questionId == null ? "Add Question" : "Edit Question",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            saveQuestion(questionId, questionTextArea.getText(), 
                         typeComboBox.getSelectedItem().toString(), 
                         optionFields, correctBoxes);
        }
    }

    private void saveQuestion(Integer questionId, String text, String type,
                              ArrayList<JTextField> optionFields, ArrayList<JCheckBox> correctBoxes) {
        if (text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question text cannot be empty.");
            return;
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving question: " + e.getMessage());
        }
    }

    private void deleteQuestion() {
        String selected = questionList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a question to delete.");
            return;
        }

        int questionId = extractQuestionId(selected);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this question?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM questions WHERE question_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, questionId);
                stmt.executeUpdate();
                loadQuestions();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting question: " + e.getMessage());
            }
        }
    }
}
