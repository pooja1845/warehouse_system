package com.wms.views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class InventoryView extends JPanel {

    private JTextField txtSearch;
    private JButton btnSearch, btnAdd, btnEdit, btnDelete, btnRefresh;
    private JTable tblProducts;
    private DefaultTableModel tableModel;

    public InventoryView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
    }

    private void initUI() {
        // Top Action Bar
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setOpaque(false);
        
        btnAdd = createActionButton("Add Product", new Color(39, 174, 96));
        btnEdit = createActionButton("Edit Selected", new Color(243, 156, 18));
        btnDelete = createActionButton("Delete Selected", new Color(211, 47, 47));
        btnRefresh = createActionButton("Refresh", new Color(149, 165, 166));

        leftActions.add(btnAdd);
        leftActions.add(btnEdit);
        leftActions.add(btnDelete);
        leftActions.add(btnRefresh);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);
        
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search SKU, Name, or Category");
        btnSearch = createActionButton("Search", new Color(25, 118, 210));
        
        rightActions.add(txtSearch);
        rightActions.add(btnSearch);

        actionBar.add(leftActions, BorderLayout.WEST);
        actionBar.add(rightActions, BorderLayout.EAST);

        add(actionBar, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"ID", "SKU", "Name", "Quantity", "Category", "Location", "Min Stock"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProducts = new JTable(tableModel);
        tblProducts.setRowHeight(35);
        tblProducts.setShowGrid(false);
        tblProducts.setShowHorizontalLines(true);
        tblProducts.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = tblProducts.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(100, 110, 120));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Custom Renderer for low stock highlighting
        LowStockRenderer renderer = new LowStockRenderer();
        for (int i = 0; i < tblProducts.getColumnCount(); i++) {
            tblProducts.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
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

    // Custom cell renderer to highlight row if quantity <= minStock
    class LowStockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                try {
                    int quantity = Integer.parseInt(table.getValueAt(row, 3).toString());
                    int minStock = Integer.parseInt(table.getValueAt(row, 6).toString());
                    if (quantity <= minStock) {
                        c.setBackground(new Color(255, 235, 238)); // Light red background
                        c.setForeground(new Color(211, 47, 47)); // Dark red text
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            return c;
        }
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return tblProducts; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnEdit() { return btnEdit; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnSearch() { return btnSearch; }
    public JTextField getTxtSearch() { return txtSearch; }
}
