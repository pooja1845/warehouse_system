package com.wms.controllers;

import com.wms.dao.InboundDAO;
import com.wms.models.Product;
import com.wms.views.InboundView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InboundController {

    private InboundView view;
    private InboundDAO dao;

    public InboundController(InboundView view) {
        this.view = view;
        this.dao  = new InboundDAO();
        initController();
        loadLogs();
    }

    private void initController() {
        // Receive Stock button
        view.getBtnReceive().addActionListener(e -> showReceiveDialog());

        // Add Supplier button
        view.getBtnAddSupplier().addActionListener(e -> showAddSupplierDialog());
    }

    private void loadLogs() {
        DefaultTableModel model = view.getModelLogs();
        model.setRowCount(0);
        for (Object[] row : dao.getAllInboundLogs()) {
            model.addRow(row);
        }
    }

    // -----------------------------------------------
    // Dialog: Receive Stock
    // -----------------------------------------------
    private void showReceiveDialog() {
        List<Product> products   = dao.getAllProducts();
        List<Object[]> suppliers = dao.getAllSuppliers();

        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No products found. Please add products in the Inventory section first.", "No Products", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (suppliers.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No suppliers found. Please add a supplier first using the 'Add Supplier' button.", "No Suppliers", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Receive Inbound Stock", true);
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(view);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Product dropdown
        JComboBox<String> cmbProduct = new JComboBox<>();
        for (Product p : products) {
            cmbProduct.addItem("[" + p.getSku() + "] " + p.getName() + "  (Stock: " + p.getQuantity() + ")");
        }

        // Supplier dropdown
        JComboBox<String> cmbSupplier = new JComboBox<>();
        for (Object[] s : suppliers) {
            cmbSupplier.addItem(s[0] + " — " + s[1]);
        }

        // Quantity spinner
        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));

        form.add(new JLabel("Product:"));
        form.add(cmbProduct);
        form.add(new JLabel("Supplier:"));
        form.add(cmbSupplier);
        form.add(new JLabel("Quantity Received:"));
        form.add(spnQty);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = new JButton("Receive");
        JButton btnCancel = new JButton("Cancel");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            int productIndex  = cmbProduct.getSelectedIndex();
            int supplierIndex = cmbSupplier.getSelectedIndex();
            int qty           = (int) spnQty.getValue();

            int productId  = products.get(productIndex).getId();
            int supplierId = (int) ((Object[]) suppliers.get(supplierIndex))[0];

            if (dao.receiveStockTransaction(productId, supplierId, qty)) {
                JOptionPane.showMessageDialog(dialog,
                    "Stock received successfully!\n" +
                    products.get(productIndex).getName() + " → +" + qty + " units added to inventory.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadLogs(); // Refresh log table
            } else {
                JOptionPane.showMessageDialog(dialog, "Transaction failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttons.add(btnSave);
        buttons.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // -----------------------------------------------
    // Dialog: Add Supplier
    // -----------------------------------------------
    private void showAddSupplierDialog() {
        JTextField txtName    = new JTextField();
        JTextField txtContact = new JTextField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.add(new JLabel("Supplier Name:"));  form.add(txtName);
        form.add(new JLabel("Contact Info:"));   form.add(txtContact);

        int result = JOptionPane.showConfirmDialog(view, form, "Add New Supplier", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Supplier Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dao.addSupplier(name, txtContact.getText().trim())) {
                JOptionPane.showMessageDialog(view, "Supplier '" + name + "' added successfully!");
            } else {
                JOptionPane.showMessageDialog(view, "Failed to add supplier.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
