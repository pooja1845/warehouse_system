package com.wms.views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardView extends JPanel {

    private JLabel lblTotalStock, lblPendingOrders, lblTodayShipments, lblLowStock;
    private JPanel boxTotalStock, boxPendingOrders, boxShipments, boxLowStock;

    private DefaultCategoryDataset barDataset;
    private DefaultPieDataset<String> ringDataset;

    private JTable tblInbound, tblTasks, tblLowStock;
    private DefaultTableModel modelInbound, modelTasks, modelLowStock;
    private JPanel recentActivityPanel;

    public DashboardView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TOP: KPI Grid
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setOpaque(false);

        boxTotalStock = createKPIBox("Total Stock", "0", "Items", new Color(25, 118, 210)); 
        lblTotalStock = (JLabel) boxTotalStock.getClientProperty("valueLabel");

        boxPendingOrders = createKPIBox("Orders Pending", "0", "Orders", new Color(245, 124, 0));
        lblPendingOrders = (JLabel) boxPendingOrders.getClientProperty("valueLabel");

        boxShipments = createKPIBox("Shipments Today", "0", "Shipments", new Color(56, 142, 60));
        lblTodayShipments = (JLabel) boxShipments.getClientProperty("valueLabel");

        boxLowStock = createKPIBox("Low Stock Alerts", "0", "Alerts", new Color(211, 47, 47));
        lblLowStock = (JLabel) boxLowStock.getClientProperty("valueLabel");

        kpiPanel.add(boxTotalStock);
        kpiPanel.add(boxPendingOrders);
        kpiPanel.add(boxShipments);
        kpiPanel.add(boxLowStock);

        add(kpiPanel, BorderLayout.NORTH);

        // CENTER: Content Panel
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setOpaque(false);

        // Middle: Charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setPreferredSize(new Dimension(0, 320)); // Increased height for better chart visibility
        
        chartsPanel.add(createBarChartPanel());
        chartsPanel.add(createRingChartPanel());

        mainContentPanel.add(chartsPanel, BorderLayout.NORTH);

        // Bottom: Data Tables Grid (2 cols, 2 rows)
        JPanel tablesPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        tablesPanel.setOpaque(false);

        tablesPanel.add(createInboundTablePanel());
        tablesPanel.add(createTasksTablePanel());
        tablesPanel.add(createLowStockTablePanel());
        tablesPanel.add(createActivityPanel());

        mainContentPanel.add(tablesPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createKPIBox(String title, String value, String unit, Color bgColor) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(bgColor);
        box.setPreferredSize(new Dimension(200, 100));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valuePanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 38));

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setForeground(new Color(255, 255, 255, 200));
        unitLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        valuePanel.add(valueLabel);
        valuePanel.add(unitLabel);

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(valuePanel, BorderLayout.SOUTH);

        box.putClientProperty("valueLabel", valueLabel);

        box.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { box.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { box.setBackground(bgColor); }
        });

        return box;
    }

    private JPanel createBarChartPanel() {
        barDataset = new DefaultCategoryDataset();
        // Data will be populated from DB via updateBarChart()

        JFreeChart chart = ChartFactory.createBarChart("Inventory Overview", "", "Quantity", barDataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(25, 118, 210));
        chart.getCategoryPlot().getDomainAxis().setMaximumCategoryLabelWidthRatio(0.5f);

        ChartPanel cp = new ChartPanel(chart);
        return wrapInCard(cp);
    }

    private JPanel createRingChartPanel() {
        ringDataset = new DefaultPieDataset<>();
        // Data will be populated from DB via updateRingChart()

        JFreeChart chart = ChartFactory.createRingChart("Order Status", ringDataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.35);
        plot.setInteriorGap(0.04);
        plot.setSectionPaint("Pending",    new Color(244, 67, 54));
        plot.setSectionPaint("Processing", new Color(255, 193, 7));
        plot.setSectionPaint("Shipped",    new Color(76, 175, 80));
        plot.setSectionPaint("Delivered",  new Color(25, 118, 210));
        plot.setSectionPaint("Cancelled",  new Color(158, 158, 158));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}", new DecimalFormat("0"), new DecimalFormat("0%")));
        plot.setSimpleLabels(true);

        ChartPanel cp = new ChartPanel(chart);
        return wrapInCard(cp);
    }

    private JPanel createInboundTablePanel() {
        String[] columns = {"ID", "Product", "Supplier", "Qty Received", "Date Received"};
        modelInbound = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInbound = createStyledTable(modelInbound);

        JPanel p = wrapInCard(new JScrollPane(tblInbound));
        p.add(new JLabel("  Inbound Shipments"), BorderLayout.NORTH);
        p.getComponent(0).setFont(new Font("Arial", Font.BOLD, 16));
        return p;
    }

    private JPanel createTasksTablePanel() {
        String[] columns = {"Order ID", "Status", "Date", "Items"};
        modelTasks = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTasks = createStyledTable(modelTasks);
        tblTasks.getColumnModel().getColumn(1).setCellRenderer(new BadgeRenderer());

        JPanel p = wrapInCard(new JScrollPane(tblTasks));
        p.add(new JLabel("  Picking Tasks (Active Orders)"), BorderLayout.NORTH);
        p.getComponent(0).setFont(new Font("Arial", Font.BOLD, 16));
        return p;
    }

    private JPanel createLowStockTablePanel() {
        String[] columns = {"SKU", "Product", "Qty", "Min Stock"};
        modelLowStock = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLowStock = createStyledTable(modelLowStock);

        JPanel p = wrapInCard(new JScrollPane(tblLowStock));
        p.add(new JLabel("  Low Stock Items"), BorderLayout.NORTH);
        p.getComponent(0).setFont(new Font("Arial", Font.BOLD, 16));
        return p;
    }

    private JPanel createActivityPanel() {
        recentActivityPanel = new JPanel();
        recentActivityPanel.setLayout(new BoxLayout(recentActivityPanel, BoxLayout.Y_AXIS));
        recentActivityPanel.setBackground(Color.WHITE);
        // Rows will be added dynamically via updateRecentActivity()

        JPanel p = wrapInCard(new JScrollPane(recentActivityPanel));
        p.add(new JLabel("  Recent Activity"), BorderLayout.NORTH);
        p.getComponent(0).setFont(new Font("Arial", Font.BOLD, 16));
        return p;
    }

    /**
     * Replaces the Recent Activity feed with live events.
     * Each entry is a String[2]: {message, type}
     * type → "order", "shipment", "inbound", "stock", "default"
     */
    public void updateRecentActivity(java.util.List<String[]> events) {
        recentActivityPanel.removeAll();
        if (events.isEmpty()) {
            recentActivityPanel.add(createActivityRow("No recent activity yet.", new Color(158, 158, 158)));
        } else {
            for (String[] event : events) {
                String message = event[0];
                String type    = event.length > 1 ? event[1] : "default";
                Color dotColor;
                switch (type) {
                    case "order":    dotColor = new Color(33, 150, 243);  break; // blue
                    case "shipment": dotColor = new Color(76, 175, 80);   break; // green
                    case "inbound":  dotColor = new Color(39, 174, 96);   break; // teal
                    case "stock":    dotColor = new Color(244, 67, 54);   break; // red (low stock)
                    default:         dotColor = new Color(158, 158, 158); break; // grey
                }
                recentActivityPanel.add(createActivityRow(message, dotColor));
            }
        }
        recentActivityPanel.revalidate();
        recentActivityPanel.repaint();
    }


    private JPanel createActivityRow(String text, Color dotColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        row.setBackground(Color.WHITE);
        
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(dotColor);
                g.fillOval(0, 0, 10, 10);
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setOpaque(false);
        
        row.add(dot);
        row.add(new JLabel(text));
        return row;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredSize().width, 35 * 6));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(100, 110, 120));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        return table;
    }

    // kept for backward compat (charts still use static data)
    private JTable createStyledTable(Object[][] data, String[] columns) {
        DefaultTableModel m = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        return createStyledTable(m);
    }

    private JPanel wrapInCard(JComponent comp) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        wrapper.add(comp, BorderLayout.CENTER);
        return wrapper;
    }

    class BadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            
            String text = value.toString();
            if (text.equals("In Transit") || text.equals("Received") || text.equals("Shipped")) lbl.setBackground(new Color(76, 175, 80));
            else if (text.equals("Pending") || text.equals("High") || text.equals("Processing")) lbl.setBackground(new Color(245, 124, 0));
            else if (text.equals("Medium")) lbl.setBackground(new Color(76, 175, 80)); // Greenish for medium? or maybe yellow
            else if (text.equals("Low")) lbl.setBackground(new Color(33, 150, 243));
            
            if (!text.isEmpty()) {
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Arial", Font.BOLD, 11));
            }
            
            // To make it look like a badge, we'd ideally render a rounded rectangle, but setting background is a decent Swing approximation.
            return lbl;
        }
    }

    public void updateKPIs(String totalStock, String pendingOrders, String shipments, String lowStock) {
        lblTotalStock.setText(totalStock);
        lblPendingOrders.setText(pendingOrders);
        lblTodayShipments.setText(shipments);
        lblLowStock.setText(lowStock);
    }

    public void updateChart(Map<String, Integer> categoryData) {
        // Reserved for future live chart update
    }

    /** Updates bar chart with real category → quantity data from DB */
    public void updateBarChart(Map<String, Integer> categoryData) {
        barDataset.clear();
        if (categoryData.isEmpty()) {
            barDataset.addValue(0, "Items", "No Data");
        } else {
            for (Map.Entry<String, Integer> entry : categoryData.entrySet()) {
                barDataset.addValue(entry.getValue(), "Items", entry.getKey());
            }
        }
    }

    /** Updates ring chart with real order status → count data from DB */
    public void updateRingChart(Map<String, Integer> statusData) {
        ringDataset.clear();
        if (statusData.isEmpty()) {
            ringDataset.setValue("No Orders", 1);
        } else {
            for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
                if (entry.getValue() > 0) { // only show statuses that exist
                    ringDataset.setValue(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    // --- Live table update methods called by DashboardController ---
    public void updateInboundTable(java.util.List<Object[]> rows) {
        modelInbound.setRowCount(0);
        for (Object[] row : rows) modelInbound.addRow(row);
    }

    public void updatePickingTasksTable(java.util.List<Object[]> rows) {
        modelTasks.setRowCount(0);
        for (Object[] row : rows) modelTasks.addRow(row);
    }

    public void updateLowStockTable(java.util.List<Object[]> rows) {
        modelLowStock.setRowCount(0);
        for (Object[] row : rows) modelLowStock.addRow(row);
    }

    public JPanel getBoxTotalStock() { return boxTotalStock; }
    public JPanel getBoxPendingOrders() { return boxPendingOrders; }
    public JPanel getBoxShipments() { return boxShipments; }
    public JPanel getBoxLowStock() { return boxLowStock; }
}
