package com.wms.controllers;

import com.wms.dao.ProductDAO;
import com.wms.models.Product;
import com.wms.views.InventoryView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryController {
    
    private InventoryView view;
    private ProductDAO dao;

    public InventoryController(InventoryView view) {
        this.view = view;
        this.dao = new ProductDAO();

        initController();
        loadData(dao.getAllProducts());
    }

    private void initController() {
        view.getBtnAdd().addActionListener(e -> showProductDialog(null));
        
        view.getBtnEdit().addActionListener(e -> {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Please select a product to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String sku = (String) view.getTableModel().getValueAt(selectedRow, 1);
            String name = (String) view.getTableModel().getValueAt(selectedRow, 2);
            int qty = (int) view.getTableModel().getValueAt(selectedRow, 3);
            String category = (String) view.getTableModel().getValueAt(selectedRow, 4);
            String loc = (String) view.getTableModel().getValueAt(selectedRow, 5);
            int minStock = (int) view.getTableModel().getValueAt(selectedRow, 6);

            Product p = new Product(id, sku, name, qty, category, loc, minStock);
            showProductDialog(p);
        });

        view.getBtnDelete().addActionListener(e -> {
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Please select a product to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) view.getTableModel().getValueAt(selectedRow, 0);
            String name = (String) view.getTableModel().getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to delete product: " + name + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(view, "Product deleted successfully.");
                    loadData(dao.getAllProducts());
                } else {
                    JOptionPane.showMessageDialog(view, "Error deleting product.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        view.getBtnRefresh().addActionListener(e -> {
            view.getTxtSearch().setText("");
            loadData(dao.getAllProducts());
        });

        view.getBtnSearch().addActionListener(e -> {
            String q = view.getTxtSearch().getText().trim();
            if (q.isEmpty()) {
                loadData(dao.getAllProducts());
            } else {
                loadData(dao.searchProducts(q));
            }
        });
    }

    private void loadData(List<Product> products) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0); // clear table

        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getQuantity(),
                p.getCategory(),
                p.getLocation(),
                p.getMinStockLevel()
            });
        }
    }

    private void showProductDialog(Product productToEdit) {
        boolean isEdit = (productToEdit != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), isEdit ? "Edit Product" : "Add New Product", true);
        dialog.setSize(400, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(view);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtSku = new JTextField();
        JTextField txtName = new JTextField();
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
        JTextField txtCategory = new JTextField();
        JTextField txtLocation = new JTextField();
        JSpinner spnMinStock = new JSpinner(new SpinnerNumberModel(10, 0, 100000, 1));

        if (isEdit) {
            txtSku.setText(productToEdit.getSku());
            txtName.setText(productToEdit.getName());
            spnQty.setValue(productToEdit.getQuantity());
            txtCategory.setText(productToEdit.getCategory());
            txtLocation.setText(productToEdit.getLocation());
            spnMinStock.setValue(productToEdit.getMinStockLevel());
            txtSku.setEditable(false); // typically SKU shouldn't change
        }

        panel.add(new JLabel("SKU:")); panel.add(txtSku);
        panel.add(new JLabel("Product Name:")); panel.add(txtName);
        panel.add(new JLabel("Quantity:")); panel.add(spnQty);
        panel.add(new JLabel("Category:")); panel.add(txtCategory);
        panel.add(new JLabel("Location:")); panel.add(txtLocation);
        panel.add(new JLabel("Min Stock Level:")); panel.add(spnMinStock);

        dialog.add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            String sku = txtSku.getText().trim();
            String name = txtName.getText().trim();
            String cat = txtCategory.getText().trim();
            String loc = txtLocation.getText().trim();
            int qty = (int) spnQty.getValue();
            int minStock = (int) spnMinStock.getValue();

            if (sku.isEmpty() || name.isEmpty() || cat.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields (SKU, Name, Category).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product p = new Product();
            p.setSku(sku);
            p.setName(name);
            p.setQuantity(qty);
            p.setCategory(cat);
            p.setLocation(loc);
            p.setMinStockLevel(minStock);

            boolean success;
            if (isEdit) {
                p.setId(productToEdit.getId());
                success = dao.updateProduct(p);
            } else {
                success = dao.addProduct(p);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Product saved successfully!");
                dialog.dispose();
                loadData(dao.getAllProducts());
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to save product. Ensure SKU is unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnSave);
        bottomPanel.add(btnCancel);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
