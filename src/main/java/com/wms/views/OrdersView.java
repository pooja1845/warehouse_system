package com.wms.views;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class OrdersView extends JPanel {

    private JTable tblMaster, tblDetail;
    private DefaultTableModel modelMaster, modelDetail;
    private JButton btnCreate, btnUpdateStatus, btnDelete;
    private JSplitPane splitPane;

    public OrdersView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        // --- 1. Top Action Bar ---
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBar.setOpaque(false);

        btnCreate = createActionButton("Create New Order", new Color(33, 150, 243));
        btnUpdateStatus = createActionButton("Update Status", new Color(243, 156, 18));
        btnDelete = createActionButton("Delete Order", new Color(211, 47, 47));

        actionBar.add(btnCreate);
        actionBar.add(btnUpdateStatus);
        actionBar.add(btnDelete);
        
        add(actionBar, BorderLayout.NORTH);

        // --- 2. Master Table (Orders) ---
        String[] masterCols = {"Order ID", "Status", "Order Date"};
        modelMaster = new DefaultTableModel(null, masterCols) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblMaster = createStyledTable(modelMaster);
        
        JPanel masterPanel = new JPanel(new BorderLayout());
        masterPanel.setOpaque(false);
        JLabel lblMaster = new JLabel("  Orders List");
        lblMaster.setFont(new Font("Arial", Font.BOLD, 14));
        lblMaster.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        masterPanel.add(lblMaster, BorderLayout.NORTH);
        masterPanel.add(new JScrollPane(tblMaster), BorderLayout.CENTER);

        // --- 3. Detail Table (Order Items) ---
        String[] detailCols = {"SKU", "Product Name", "Quantity Ordered"};
        modelDetail = new DefaultTableModel(null, detailCols) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblDetail = createStyledTable(modelDetail);

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setOpaque(false);
        JLabel lblDetail = new JLabel("  Order Items (Line Details)");
        lblDetail.setFont(new Font("Arial", Font.BOLD, 14));
        lblDetail.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        detailPanel.add(lblDetail, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(tblDetail), BorderLayout.CENTER);

        // --- 4. Split Pane Configuration ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(300); // give top half 300px
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        // remove the ugly borders
        BasicSplitPaneUI ui = (BasicSplitPaneUI) splitPane.getUI();
        if (ui != null) ui.getDivider().setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(100, 110, 120));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        return table;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public DefaultTableModel getModelMaster() { return modelMaster; }
    public DefaultTableModel getModelDetail() { return modelDetail; }
    public JTable getTblMaster() { return tblMaster; }
    public JTable getTblDetail() { return tblDetail; }
    public JButton getBtnCreate() { return btnCreate; }
    public JButton getBtnUpdateStatus() { return btnUpdateStatus; }
    public JButton getBtnDelete() { return btnDelete; }
}
