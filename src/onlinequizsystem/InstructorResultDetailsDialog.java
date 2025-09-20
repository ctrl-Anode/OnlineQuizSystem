package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private DefaultTableModel model;
    private int instructorId;

    /**
     * Launch the application (for testing only)
     */
    public static void main(String[] args) {
        try {
            InstructorResultDetailsDialog dialog = new InstructorResultDetailsDialog(
                    1, "John Doe", "Sample Quiz", 2
            );
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public InstructorResultDetailsDialog(int resultId, String studentName, String quizTitle, int instructorId) {
        this.instructorId = instructorId;

        setTitle("Result Details - " + studentName + " | Quiz: " + quizTitle);
        setBounds(100, 100, 800, 500);
        setLocationRelativeTo(null);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Table model
        model = new DefaultTableModel(new String[]{
                "Question", "Option Choices", "Student Answer", "Correct Answer"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPane.add(closeButton);
        getRootPane().setDefaultButton(closeButton);

        // Load results
        loadResultDetails(resultId);
    }

    /**
     * Load quiz result details for this instructor
     */
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
            stmt.setInt(2, instructorId); // enforce ownership
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            boolean found = false;
            while (rs.next()) {
                found = true;
                model.addRow(new Object[]{
                        rs.getString("question_text"),
                        rs.getString("options_list"),
                        rs.getString("student_answer") != null ? rs.getString("student_answer") : "(No Answer)",
                        rs.getString("correct_answer")
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
