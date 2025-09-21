package onlinequizsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class InstructorResultsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    
    private JTable table;
    private DefaultTableModel model;
    private int instructorId;
    private JLabel resultsCountLabel;

    public InstructorResultsPanel(int instructorId) {
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

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        // Load initial data
        loadResults();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 Quiz Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and analyze student quiz performance");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Right side - Results count
        resultsCountLabel = new JLabel("0 Results");
        resultsCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultsCountLabel.setForeground(PRIMARY_COLOR);
        resultsCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(resultsCountLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

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

        JLabel tableHeaderLabel = new JLabel("📋 Student Results Overview");
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

        return contentPanel;
    }

    private void createModernTable() {
        // Create table model with styled columns
        String[] columnNames = {"ID", "Student Name", "Quiz Title", "Score", "Date Taken"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        table = new JTable(model);
        
        // Style the table
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
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
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Student Name
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Quiz Title
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Score
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Date Taken

        // Custom cell renderer for better styling
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (column == 0) { // ID column
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setForeground(PRIMARY_COLOR);
                } else if (column == 1) { // Student Name
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 2) { // Quiz Title
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(SECONDARY_COLOR);
                } else if (column == 3) { // Score
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    
                    if (value != null) {
                        String scoreText = value.toString();
                        if (scoreText.contains("/")) {
                            String[] parts = scoreText.split("/");
                            if (parts.length == 2) {
                                try {
                                    int score = Integer.parseInt(parts[0].trim());
                                    int total = Integer.parseInt(parts[1].trim());
                                    double percentage = (double) score / total * 100;
                                    
                                    if (percentage >= 90) setForeground(SUCCESS_COLOR);
                                    else if (percentage >= 70) setForeground(WARNING_COLOR);
                                    else setForeground(DANGER_COLOR);
                                } catch (NumberFormatException e) {
                                    setForeground(TEXT_COLOR);
                                }
                            }
                        } else {
                            setForeground(TEXT_COLOR);
                        }
                    }
                } else if (column == 4) { // Date Taken
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MUTED_COLOR);
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
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
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left side - info
        JLabel infoLabel = new JLabel("💡 Double-click a row to view detailed results");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(MUTED_COLOR);

        // Right side - action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        JButton viewDetailsButton = createStyledButton("👁 View Details", SUCCESS_COLOR);

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);

        // Button actions
        refreshButton.addActionListener(e -> {
            loadResults();
            showStyledMessage("Results refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });
        
        viewDetailsButton.addActionListener(e -> openResultDetails());

        // Add double-click functionality to table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openResultDetails();
                }
            }
        });

        footerPanel.add(infoLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

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
        button.setPreferredSize(new Dimension(150, 45));

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

    private void loadResults() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.results_id, u.full_name, q.title, r.score, r.taken_at, " +
                         "(SELECT COUNT(*) FROM questions WHERE quiz_id = r.quiz_id) as total_questions " +
                         "FROM results r " +
                         "JOIN users u ON r.student_id = u.id " +
                         "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                         "WHERE u.role = 'Student' AND q.created_by = ? " +
                         "ORDER BY r.taken_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);
            int count = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            
            while (rs.next()) {
                int score = rs.getInt("score");
                int totalQuestions = rs.getInt("total_questions");
                String scoreDisplay = score + "/" + totalQuestions;
                
                String formattedDate = dateFormat.format(rs.getTimestamp("taken_at"));
                
                model.addRow(new Object[]{
                        rs.getInt("results_id"),
                        rs.getString("full_name"),
                        rs.getString("title"),
                        scoreDisplay,
                        formattedDate
                });
                count++;
            }
            
            // Update results count
            resultsCountLabel.setText(count + " Result" + (count != 1 ? "s" : ""));
            
        } catch (Exception e) {
            showStyledMessage("Error loading results: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openResultDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showStyledMessage("Please select a result to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resultId = (int) model.getValueAt(row, 0);
        String studentName = (String) model.getValueAt(row, 1);
        String quizTitle = (String) model.getValueAt(row, 2);

        InstructorResultDetailsDialog dialog =
                new InstructorResultDetailsDialog(resultId, studentName, quizTitle, instructorId);
        dialog.setVisible(true);
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
