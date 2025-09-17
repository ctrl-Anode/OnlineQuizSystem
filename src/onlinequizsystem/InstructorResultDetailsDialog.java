package onlinequizsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultDetailsDialog extends JDialog {
    private JTable table;
    private DefaultTableModel model;

    public InstructorResultDetailsDialog(int resultId, String studentName, String quizTitle) {
        setTitle("Result Details - " + studentName + " | Quiz: " + quizTitle);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setModal(true);

        model = new DefaultTableModel(new String[]{
                "Question", "Option Choices", "Student Answer", "Correct Answer"
        }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadResultDetails(resultId);
    }

    private void loadResultDetails(int resultId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT q.question_text, " +
                         "GROUP_CONCAT(o.option_text SEPARATOR ', ') AS options_list, " +
                         "sa.option_id AS student_option, " +
                         "so.option_text AS student_answer, " +
                         "co.option_text AS correct_answer " +
                         "FROM results r " +
                         "JOIN student_answers sa ON r.student_id = sa.student_id AND r.quiz_id = sa.quiz_id " +
                         "JOIN questions q ON sa.question_id = q.question_id " +
                         "LEFT JOIN options o ON q.question_id = o.question_id " +
                         "LEFT JOIN options so ON sa.option_id = so.option_id " +
                         "LEFT JOIN options co ON q.question_id = co.question_id AND co.is_correct = 1 " +
                         "WHERE r.results_id = ? " +
                         "GROUP BY q.question_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, resultId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("question_text"),
                        rs.getString("options_list"),
                        rs.getString("student_answer") != null ? rs.getString("student_answer") : "(No Answer)",
                        rs.getString("correct_answer")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading result details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
