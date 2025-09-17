package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ManageQuizzesPanel extends JPanel {
    private int instructorId;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;

    public ManageQuizzesPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Quizzes"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton createBtn = new JButton("Create Quiz");
        JButton editBtn = new JButton("Edit Quiz");
        JButton deleteBtn = new JButton("Delete Quiz");
        JButton manageQuestionsBtn = new JButton("Manage Questions");

        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(manageQuestionsBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadQuizzes();

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
            if (selected != null) new ManageQuestionsDialog(extractQuizId(selected));
            else JOptionPane.showMessageDialog(this, "Select a quiz first.");
        });
    }

    private void loadQuizzes() {
        quizListModel.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT quiz_id, title FROM quizzes WHERE created_by = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quizListModel.addElement(rs.getInt("quiz_id") + ": " + rs.getString("title"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading quizzes: " + e.getMessage());
        }
    }

    private int extractQuizId(String text) {
        return Integer.parseInt(text.split(":")[0]);
    }

    private void openQuizDialog(Integer quizId) {
        JTextField titleField = new JTextField(20);
        JTextArea descArea = new JTextArea(5, 20);

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
                JOptionPane.showMessageDialog(this, "Error loading quiz: " + e.getMessage());
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Title:"), BorderLayout.NORTH);
        panel.add(titleField, BorderLayout.CENTER);
        panel.add(new JScrollPane(descArea), BorderLayout.SOUTH);

        int option = JOptionPane.showConfirmDialog(this, panel,
                quizId == null ? "Create Quiz" : "Edit Quiz",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty.");
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
                } else {
                    String sql = "UPDATE quizzes SET title = ?, description = ? WHERE quiz_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, title);
                    stmt.setString(2, desc);
                    stmt.setInt(3, quizId);
                    stmt.executeUpdate();
                }
                loadQuizzes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving quiz: " + e.getMessage());
            }
        }
    }

    private void deleteQuiz() {
        String selected = quizList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a quiz to delete.");
            return;
        }
        int quizId = extractQuizId(selected);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this quiz?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, quizId);
                stmt.executeUpdate();
                loadQuizzes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting quiz: " + e.getMessage());
            }
        }
    }
}
