package com.wms.views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Map;

public class ReportsView extends JPanel {

    private JComboBox<String> cmbReportType;
    private JButton btnGenerateReport;
    private JButton btnExportCSV;
    private JTable tblReport;
    private DefaultTableModel tableModel;
    private JPanel kpiSummaryPanel;

    public static final String REPORT_INVENTORY    = "Inventory Summary";
    public static final String REPORT_ORDERS       = "Orders Summary";
    public static final String REPORT_SHIPMENTS    = "Shipments Summary";
    public static final String REPORT_LOW_STOCK    = "Low Stock Alerts";

    public ReportsView() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        // --- Top: Title + Controls ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Reports & Analytics");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 33, 33));
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);

        cmbReportType = new JComboBox<>(new String[]{
            REPORT_INVENTORY, REPORT_ORDERS, REPORT_SHIPMENTS, REPORT_LOW_STOCK
        });
        cmbReportType.setPreferredSize(new Dimension(200, 35));
        cmbReportType.setFont(new Font("Arial", Font.PLAIN, 13));

        btnGenerateReport = createButton("Generate Report", new Color(25, 118, 210));
        btnExportCSV      = createButton("Export CSV", new Color(39, 174, 96));

        controlPanel.add(new JLabel("Report Type: "));
        controlPanel.add(cmbReportType);
        controlPanel.add(btnGenerateReport);
        controlPanel.add(btnExportCSV);

        topPanel.add(controlPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Middle: KPI cards row (populated dynamically) ---
        kpiSummaryPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        kpiSummaryPanel.setOpaque(false);
        kpiSummaryPanel.setPreferredSize(new Dimension(0, 90));
        add(kpiSummaryPanel, BorderLayout.CENTER);

        // --- Bottom: Report Table ---
        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tblReport = new JTable(tableModel);
        tblReport.setRowHeight(34);
        tblReport.setShowGrid(false);
        tblReport.setShowHorizontalLines(true);
        tblReport.setGridColor(new Color(230, 230, 230));
        tblReport.setFont(new Font("Arial", Font.PLAIN, 13));
        tblReport.setSelectionBackground(new Color(232, 240, 254));

        JTableHeader header = tblReport.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(80, 80, 80));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scrollPane = new JScrollPane(tblReport);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tableCard.add(scrollPane);

        add(tableCard, BorderLayout.SOUTH);
        // Make SOUTH take up remaining space
        setLayout(new BorderLayout(0, 15));
        add(topPanel, BorderLayout.NORTH);
        add(kpiSummaryPanel, BorderLayout.CENTER);
        add(tableCard, BorderLayout.SOUTH);

        // Force the table area to grow with the window
        tableCard.setPreferredSize(new Dimension(0, 420));
    }

    public void updateKPICards(Map<String, Integer> kpiData) {
        kpiSummaryPanel.removeAll();
        Color[] colors = {
            new Color(25, 118, 210), new Color(244, 67, 54),
            new Color(243, 156, 18), new Color(39, 174, 96),
            new Color(142, 68, 173)
        };
        int i = 0;
        for (Map.Entry<String, Integer> entry : kpiData.entrySet()) {
            Color c = colors[i % colors.length];
            kpiSummaryPanel.add(createKPICard(entry.getKey(), String.valueOf(entry.getValue()), c));
            i++;
        }
        kpiSummaryPanel.revalidate();
        kpiSummaryPanel.repaint();
    }

    private JPanel createKPICard(String title, String value, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 26));
        lblValue.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel("<html>" + title + "</html>");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(255, 255, 255, 200));

        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        return card;
    }

    public void setReportData(String[] columns, java.util.List<Object[]> rows, String statusColumn) {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        for (String col : columns) tableModel.addColumn(col);
        for (Object[] row : rows) tableModel.addRow(row);

        // Apply status-color renderer if a status column is identified
        if (statusColumn != null) {
            int statusIndex = -1;
            for (int c = 0; c < tblReport.getColumnCount(); c++) {
                if (tblReport.getColumnName(c).equalsIgnoreCase(statusColumn)) {
                    statusIndex = c;
                    break;
                }
            }
            if (statusIndex >= 0) {
                tblReport.getColumnModel().getColumn(statusIndex).setCellRenderer(new StatusRenderer());
            }
        }
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- Getters ---
    public JComboBox<String> getCmbReportType() { return cmbReportType; }
    public JButton getBtnGenerate()    { return btnGenerateReport; }
    public JButton getBtnExportCSV()   { return btnExportCSV; }
    public JTable getTable()           { return tblReport; }
    public DefaultTableModel getTableModel() { return tableModel; }

    // --- Status color renderer ---
    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (!isSelected && value != null) {
                String s = value.toString();
                switch (s) {
                    case "Shipped":
                    case "Delivered":
                    case "OK":
                    case "In Transit":
                        c.setBackground(new Color(232, 245, 233));
                        c.setForeground(new Color(27, 94, 32));
                        break;
                    case "Pending":
                    case "LOW STOCK":
                        c.setBackground(new Color(255, 243, 224));
                        c.setForeground(new Color(230, 81, 0));
                        break;
                    case "Cancelled":
                    case "Exception":
                        c.setBackground(new Color(255, 235, 238));
                        c.setForeground(new Color(183, 28, 28));
                        break;
                    case "Processing":
                        c.setBackground(new Color(232, 240, 254));
                        c.setForeground(new Color(13, 71, 161));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            } else if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            return c;
        }
    }
}
