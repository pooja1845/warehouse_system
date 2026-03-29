package com.wms.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ShipmentsView extends JPanel {

    private JTable tblShipments;
    private DefaultTableModel modelShipments;
    private JButton btnCreate, btnUpdateStatus;

    public ShipmentsView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        // --- 1. Top Action Bar ---
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBar.setOpaque(false);

        btnCreate = createActionButton("Create Shipment", new Color(33, 150, 243));
        btnUpdateStatus = createActionButton("Update Status", new Color(243, 156, 18));

        actionBar.add(btnCreate);
        actionBar.add(btnUpdateStatus);
        
        add(actionBar, BorderLayout.NORTH);

        // --- 2. Table ---
        String[] cols = {"Shipment ID", "Order ID", "Status", "Tracking Number", "Dispatch Date"};
        modelShipments = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        tblShipments = new JTable(modelShipments);
        tblShipments.setRowHeight(35);
        tblShipments.setShowGrid(false);
        tblShipments.setShowHorizontalLines(true);
        tblShipments.setGridColor(new Color(230, 230, 230));
        tblShipments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = tblShipments.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(100, 110, 120));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        JLabel lblHeader = new JLabel("  Shipments Manifest");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 14));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        tablePanel.add(lblHeader, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(tblShipments);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
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

    public DefaultTableModel getModelShipments() { return modelShipments; }
    public JTable getTblShipments() { return tblShipments; }
    public JButton getBtnCreate() { return btnCreate; }
    public JButton getBtnUpdateStatus() { return btnUpdateStatus; }
}
