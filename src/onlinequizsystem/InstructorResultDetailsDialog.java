package onlinequizsystem;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class InstructorResultDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private DefaultTableModel model;
    private int instructorId;

    public InstructorResultDetailsDialog(int resultId, String studentName, String quizTitle, int instructorId) {
        this.instructorId = instructorId;

        setTitle("Result Details - " + studentName + " | Quiz: " + quizTitle);
        setBounds(100, 100, 900, 550);
        setLocationRelativeTo(null);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(205, 133, 63));
        titlePanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel titleLabel = new JLabel("Student Answers for " + quizTitle, JLabel.LEFT);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        getContentPane().add(titlePanel, BorderLayout.NORTH);

        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBackground(new Color(245, 222, 179));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        model = new DefaultTableModel(new String[]{
                "Question", "Option Choices", "Student Answer", "Correct Answer"
        }, 0);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(210, 180, 140));
        table.setBackground(new Color(255, 250, 240));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(255, 250, 240));
        table.getTableHeader().setForeground(Color.BLACK);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.setBackground(new Color(245, 222, 179));

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(255, 250, 240));
        closeButton.setFont(new Font("Arial", Font.PLAIN, 13));
        closeButton.addActionListener(e -> dispose());
        buttonPane.add(closeButton);

        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(closeButton);

        loadResultDetails(resultId);
    }

    private void loadResultDetails(int resultId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT q.question_text, " +
                    "GROUP_CONCAT(DISTINCT o.option_text ORDER BY o.option_id SEPARATOR ', ') AS options_list, " +
                    "so.option_text AS student_answer, " +
                    "co.option_text AS correct_answer " +
                    "FROM results r " +
                    "JOIN quizzes qz ON r.quiz_id = qz.quiz_id " +
                    "JOIN student_answers sa ON r.student_id = sa.student_id AND r.quiz_id = sa.quiz_id " +
                    "JOIN questions q ON sa.question_id = q.question_id " +
                    "LEFT JOIN options o ON q.question_id = o.question_id " +
                    "LEFT JOIN options so ON sa.option_id = so.option_id " +
                    "LEFT JOIN options co ON q.question_id = co.question_id AND co.is_correct = 1 " +
                    "WHERE r.results_id = ? AND qz.created_by = ? " +
                    "GROUP BY q.question_id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, resultId);
            stmt.setInt(2, instructorId); 
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            boolean found = false;

            while (rs.next()) {
                found = true;

                String studentAnswer = rs.getString("student_answer");
                String correctAnswer = rs.getString("correct_answer");

                model.addRow(new Object[]{
                        rs.getString("question_text"),
                        rs.getString("options_list"),
                        studentAnswer != null ? studentAnswer : "(No Answer)",
                        correctAnswer
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "You don’t have permission to view this result.\n(It belongs to another instructor.)");
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading result details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
