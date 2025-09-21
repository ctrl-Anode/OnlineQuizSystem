package onlinequizsystem;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ManageQuizzesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private int instructorId;
    private DefaultListModel<String> quizListModel;
    private JList<String> quizList;

    public ManageQuizzesPanel(int instructorId) {
        this.instructorId = instructorId;
        setPreferredSize(new Dimension(904, 531));
        setLayout(new BorderLayout());
        setBackground(new Color(245, 222, 179)); 
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(205, 133, 63)); 
        titlePanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        JLabel titleLabel = new JLabel("Manage Quizzes", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        quizList.setFont(new Font("Arial", Font.PLAIN, 14));
        quizList.setFixedCellHeight(30);
        quizList.setBackground(new Color(255, 250, 240));
        quizList.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBackground(new Color(233, 150, 122));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Quizzes"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(205, 133, 63));

        JButton createBtn = createStyledButton("Create Quiz");
        JButton editBtn = createStyledButton("Edit Quiz");
        JButton deleteBtn = createStyledButton("Delete Quiz");
        JButton manageQuestionsBtn = createStyledButton("Manage Questions");

        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(manageQuestionsBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        loadQuizzes();

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
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(new Color(255, 250, 240)); 
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
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

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));

        int option = JOptionPane.showConfirmDialog(this, formPanel,
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
