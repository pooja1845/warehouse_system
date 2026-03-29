package com.wms.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class InboundView extends JPanel {

    private JTable tblLogs;
    private DefaultTableModel modelLogs;
    private JButton btnReceive, btnAddSupplier;

    public InboundView() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        // --- Top bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Inbound Receiving");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(33, 33, 33));
        topBar.add(title, BorderLayout.WEST);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setOpaque(false);

        btnReceive    = createButton("Receive Stock",   new Color(39, 174, 96));
        btnAddSupplier = createButton("Add Supplier",   new Color(25, 118, 210));

        btnBar.add(btnAddSupplier);
        btnBar.add(btnReceive);
        topBar.add(btnBar, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- Log Table ---
        String[] cols = {"Log ID", "SKU", "Product Name", "Supplier", "Qty Received", "Date Received"};
        modelLogs = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblLogs = new JTable(modelLogs);
        tblLogs.setRowHeight(35);
        tblLogs.setShowGrid(false);
        tblLogs.setShowHorizontalLines(true);
        tblLogs.setGridColor(new Color(230, 230, 230));
        tblLogs.setFont(new Font("Arial", Font.PLAIN, 13));
        tblLogs.setSelectionBackground(new Color(232, 240, 254));

        JTableHeader header = tblLogs.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(80, 80, 80));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scroll = new JScrollPane(tblLogs);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTableTitle = new JLabel("  Inbound Log History");
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 0));
        tableCard.add(lblTableTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
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

    public DefaultTableModel getModelLogs()   { return modelLogs; }
    public JTable getTblLogs()                { return tblLogs; }
    public JButton getBtnReceive()            { return btnReceive; }
    public JButton getBtnAddSupplier()        { return btnAddSupplier; }
}
