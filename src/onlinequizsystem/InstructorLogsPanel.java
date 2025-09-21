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

public class InstructorLogsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(247, 249, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color MUTED_COLOR = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private int instructorId;
    private JLabel logsCountLabel;

    public InstructorLogsPanel(int instructorId) {
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

        loadLogs();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Left side - Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 Quiz Activity Logs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Monitor student quiz activities and performance");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // Right side - Logs count
        logsCountLabel = new JLabel("0 Activities");
        logsCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logsCountLabel.setForeground(PRIMARY_COLOR);
        logsCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(logsCountLabel, BorderLayout.EAST);

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

        JLabel tableHeaderLabel = new JLabel("📋 Recent Quiz Activities");
        tableHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableHeaderLabel.setForeground(Color.WHITE);

        tableHeaderPanel.add(tableHeaderLabel, BorderLayout.WEST);
        tableContainer.add(tableHeaderPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(logsTable);
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
        String[] columnNames = {"Student Name", "Email", "Quiz Title", "Action", "Timestamp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        logsTable = new JTable(tableModel);
        
        // Style the table
        logsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logsTable.setRowHeight(45);
        logsTable.setGridColor(BORDER_COLOR);
        logsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        logsTable.setSelectionForeground(TEXT_COLOR);
        logsTable.setBackground(CARD_COLOR);
        logsTable.setShowVerticalLines(true);
        logsTable.setShowHorizontalLines(true);
        logsTable.setIntercellSpacing(new Dimension(1, 1));
        logsTable.setFillsViewportHeight(true);

        // Style the header
        JTableHeader header = logsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Set column widths
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Student Name
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Email
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Quiz Title
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Action
        logsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Timestamp

        // Custom cell renderer for better styling
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(TEXT_COLOR);
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (column == 0) { // Student Name
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(TEXT_COLOR);
                } else if (column == 1) { // Email
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MUTED_COLOR);
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                } else if (column == 2) { // Quiz Title
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(SECONDARY_COLOR);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else if (column == 3) { // Action
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    
                    if (value != null) {
                        String action = value.toString().toLowerCase();
                        if (action.contains("started")) {
                            setForeground(INFO_COLOR);
                            setText("▶️ Started");
                        } else if (action.contains("submitted")) {
                            setForeground(SUCCESS_COLOR);
                            setText("✅ Submitted");
                        } else if (action.contains("cancelled")) {
                            setForeground(DANGER_COLOR);
                            setText("❌ Cancelled");
                        } else {
                            setForeground(WARNING_COLOR);
                            setText("⚠️ " + value.toString());
                        }
                    }
                } else if (column == 4) { // Timestamp
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
        for (int i = 0; i < logsTable.getColumnCount(); i++) {
            logsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left side - legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setOpaque(false);

        JLabel legendTitle = new JLabel("Legend: ");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendTitle.setForeground(MUTED_COLOR);

        JLabel startedLegend = new JLabel("▶️ Started");
        startedLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        startedLegend.setForeground(INFO_COLOR);

        JLabel submittedLegend = new JLabel("✅ Submitted");
        submittedLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        submittedLegend.setForeground(SUCCESS_COLOR);

        JLabel cancelledLegend = new JLabel("❌ Cancelled");
        cancelledLegend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelledLegend.setForeground(DANGER_COLOR);

        legendPanel.add(legendTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(startedLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(submittedLegend);
        legendPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        legendPanel.add(cancelledLegend);

        // Right side - refresh button
        JButton refreshButton = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            loadLogs();
            showStyledMessage("Activity logs refreshed successfully!", "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        footerPanel.add(legendPanel, BorderLayout.WEST);
        footerPanel.add(refreshButton, BorderLayout.EAST);

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
        button.setPreferredSize(new Dimension(120, 45));

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

    /** Load quiz activity logs (only for quizzes created by this instructor) */
    private void loadLogs() {
        tableModel.setRowCount(0); // clear previous

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.full_name, u.email, q.title, l.action, l.timestamp " +
                         "FROM quiz_logs l " +
                         "JOIN users u ON l.user_id = u.id " +
                         "JOIN quizzes q ON l.quiz_id = q.quiz_id " +
                         "WHERE q.created_by = ? " +   // filter by instructor
                         "ORDER BY l.timestamp DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);  // only this instructor's quizzes
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            
            while (rs.next()) {
                String formattedTimestamp = dateFormat.format(rs.getTimestamp("timestamp"));
                
                tableModel.addRow(new Object[]{
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("title"),
                        rs.getString("action"),
                        formattedTimestamp
                });
                count++;
            }
            
            // Update logs count
            logsCountLabel.setText(count + " Activit" + (count == 1 ? "y" : "ies"));
            
        } catch (Exception e) {
            showStyledMessage("Error loading quiz logs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
