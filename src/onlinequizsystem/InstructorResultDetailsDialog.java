package onlinequizsystem;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorResultDetailsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    
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

        setTitle("📊 Result Details");
        setBounds(100, 100, 1000, 650);
        setLocationRelativeTo(null);
        setModal(true);
        setBackground(BACKGROUND_COLOR);
        getContentPane().setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel(studentName, quizTitle);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        contentPanel.setBorder(new EmptyBorder(20, 25, 10, 25));
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Create modern table
        createModernTable();
        
        // Table container with card styling
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_COLOR);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Table header
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(SECONDARY_COLOR);
        tableHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel tableHeaderLabel = new JLabel("📝 Question Analysis");
        tableHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableHeaderLabel.setForeground(Color.WHITE);

        tableHeaderPanel.add(tableHeaderLabel, BorderLayout.WEST);
        tableContainer.add(tableHeaderPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(CARD_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tableContainer, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        // Load results
        loadResultDetails(resultId);
    }

    private JPanel createHeaderPanel(String studentName, String quizTitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Left side - Title and info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 Student Result Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel studentLabel = new JLabel("👤 Student: " + studentName);
        studentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentLabel.setForeground(new Color(255, 255, 255, 180));
        studentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel quizLabel = new JLabel("📋 Quiz: " + quizTitle);
        quizLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quizLabel.setForeground(new Color(255, 255, 255, 180));
        quizLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(studentLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        titlePanel.add(quizLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private void createModernTable() {
        // Create table model with styled columns
        String[] columnNames = {"Question", "Available Options", "Student Answer", "Correct Answer"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        table = new JTable(model);
        
        // Style the table
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(60);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(CARD_COLOR);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // Question
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Available Options
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Student Answer
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Correct Answer

        // Custom cell renderer for better styling
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                
                // Format text based on column
                if (column == 0) { // Question
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setForeground(TEXT_COLOR);
                    setText("<html><div style='width: 280px;'>" + (value != null ? value.toString() : "") + "</div></html>");
                } else if (column == 1) { // Available Options
                    setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    setForeground(MUTED_COLOR);
                    setText("<html><div style='width: 180px;'>" + (value != null ? value.toString() : "") + "</div></html>");
                } else if (column == 2) { // Student Answer
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (value != null && !value.toString().equals("(No Answer)")) {
                        // Check if it matches correct answer
                        Object correctAnswer = table.getValueAt(row, 3);
                        if (correctAnswer != null && value.toString().equals(correctAnswer.toString())) {
                            setForeground(SUCCESS_COLOR);
                            setText("✅ " + value.toString());
                        } else {
                            setForeground(DANGER_COLOR);
                            setText("❌ " + value.toString());
                        }
                    } else {
                        setForeground(MUTED_COLOR);
                        setText("⚪ No Answer");
                    }
                } else if (column == 3) { // Correct Answer
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setForeground(SUCCESS_COLOR);
                    setText("✅ " + (value != null ? value.toString() : ""));
                }
                
                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new EmptyBorder(15, 25, 20, 25));

        // Left side - legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setOpaque(false);

        JLabel legendTitle = new JLabel("Legend: ");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendTitle.setForeground(MUTED_COLOR);

        JLabel correctLegend = new JLabel("✅ Correct Answer");
        correctLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        correctLegend.setForeground(SUCCESS_COLOR);

        JLabel wrongLegend = new JLabel("❌ Wrong Answer");
        wrongLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        wrongLegend.setForeground(DANGER_COLOR);

        JLabel noAnswerLegend = new JLabel("⚪ No Answer");
        noAnswerLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAnswerLegend.setForeground(MUTED_COLOR);

        legendPanel.add(legendTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(correctLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(wrongLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(noAnswerLegend);

        // Right side - close button
        JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
        closeButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(closeButton);

        footerPanel.add(legendPanel, BorderLayout.WEST);
        footerPanel.add(closeButton, BorderLayout.EAST);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    /**
     * Load quiz result details for this instructor
     */
    private void loadResultDetails(int resultId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT q.question_text, " +
                    "GROUP_CONCAT(DISTINCT o.option_text ORDER BY o.option_id SEPARATOR ', ') AS options_list, " +
                    "COALESCE(so.option_text, '(No Answer)') AS student_answer, " +
                    "COALESCE(co.option_text, 'No correct answer defined') AS correct_answer " +
                    "FROM results r " +
                    "JOIN quizzes qz ON r.quiz_id = qz.quiz_id " +
                    "JOIN questions q ON qz.quiz_id = q.quiz_id " +
                    "LEFT JOIN student_answers sa ON r.student_id = sa.student_id AND r.quiz_id = sa.quiz_id AND q.question_id = sa.question_id " +
                    "LEFT JOIN options o ON q.question_id = o.question_id " +
                    "LEFT JOIN options so ON sa.option_id = so.option_id " +
                    "LEFT JOIN options co ON q.question_id = co.question_id AND co.is_correct = 1 " +
                    "WHERE r.results_id = ? AND qz.created_by = ? " +
                    "GROUP BY q.question_id, q.question_text, so.option_text, co.option_text " +
                    "ORDER BY q.question_id";

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
                        rs.getString("student_answer"),
                        rs.getString("correct_answer")
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "You don't have permission to view this result.\n(It belongs to another instructor.)");
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading result details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
