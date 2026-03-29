package com.wms.controllers;

import com.wms.dao.OrderDAO;
import com.wms.dao.ProductDAO;
import com.wms.models.Order;
import com.wms.models.OrderItem;
import com.wms.models.Product;
import com.wms.views.OrdersView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersController {
    
    private OrdersView view;
    private OrderDAO dao;
    private ProductDAO productDao;

    public OrdersController(OrdersView view) {
        this.view = view;
        this.dao = new OrderDAO();
        this.productDao = new ProductDAO();

        initController();
        loadOrders();
    }

    private void initController() {
        // Master Table Listeners
        view.getTblMaster().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.getTblMaster().getSelectedRow();
                if (selectedRow != -1) {
                    int orderId = (int) view.getModelMaster().getValueAt(selectedRow, 0);
                    loadOrderItems(orderId);
                } else {
                    view.getModelDetail().setRowCount(0); // Clear details if nothing selected
                }
            }
        });

        view.getBtnUpdateStatus().addActionListener(e -> {
            int selectedRow = view.getTblMaster().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Please select an order to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = (int) view.getModelMaster().getValueAt(selectedRow, 0);
            String currentStatus = (String) view.getModelMaster().getValueAt(selectedRow, 1);
            
            String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
            String newStatus = (String) JOptionPane.showInputDialog(view, "Select new status:", "Update Order Status", JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);
            
            if (newStatus != null && !newStatus.equals(currentStatus)) {
                if (dao.updateOrderStatus(orderId, newStatus)) {
                    JOptionPane.showMessageDialog(view, "Status updated successfully.");
                    loadOrders(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        view.getBtnDelete().addActionListener(e -> {
            int selectedRow = view.getTblMaster().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Please select an order to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = (int) view.getModelMaster().getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to permanently delete Order #" + orderId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deleteOrder(orderId)) {
                    JOptionPane.showMessageDialog(view, "Order deleted.");
                    loadOrders(); // Refresh
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to delete order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        view.getBtnCreate().addActionListener(e -> showCreateOrderDialog());
    }

    private void loadOrders() {
        DefaultTableModel model = view.getModelMaster();
        model.setRowCount(0);

        List<Order> orders = dao.getAllOrders();
        for (Order o : orders) {
            model.addRow(new Object[]{
                o.getId(),
                o.getStatus(),
                o.getOrderDate()
            });
        }
        
        if (model.getRowCount() > 0) {
            view.getTblMaster().setRowSelectionInterval(0, 0);
        } else {
            view.getModelDetail().setRowCount(0);
        }
    }

    private void loadOrderItems(int orderId) {
        DefaultTableModel model = view.getModelDetail();
        model.setRowCount(0);

        List<OrderItem> items = dao.getOrderItems(orderId);
        for (OrderItem item : items) {
            model.addRow(new Object[]{
                item.getProductSku(),
                item.getProductName(),
                item.getQuantity()
            });
        }
    }

    // --- Complex Modal for Creating Orders ---
    private void showCreateOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Create New Order", true);
        dialog.setSize(600, 500);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(view);

        List<Product> availableProducts = productDao.getAllProducts();
        
        // Cart Table setup
        String[] cartCols = {"Product ID", "SKU", "Name", "Quantity"};
        DefaultTableModel cartModel = new DefaultTableModel(null, cartCols) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tblCart = new JTable(cartModel);
        
        // Add Product Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> cmbProducts = new JComboBox<>();
        for (Product p : availableProducts) {
            cmbProducts.addItem("[" + p.getSku() + "] " + p.getName() + " (In Stock: " + p.getQuantity() + ")");
        }

        JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JButton btnAddToCart = new JButton("Add to Cart");

        formPanel.add(new JLabel("Select Product:"));
        formPanel.add(cmbProducts);
        formPanel.add(new JLabel("Quantity to Order:"));
        formPanel.add(spnQty);
        formPanel.add(new JLabel("")); // Spacer
        formPanel.add(btnAddToCart);

        // Add to Cart logic
        btnAddToCart.addActionListener(e -> {
            int selectedIndex = cmbProducts.getSelectedIndex();
            if (selectedIndex == -1) return;
            
            Product selectedProduct = availableProducts.get(selectedIndex);
            int qtyToOrder = (int) spnQty.getValue();

            if (qtyToOrder > selectedProduct.getQuantity()) {
                JOptionPane.showMessageDialog(dialog, "Not enough stock available for " + selectedProduct.getName(), "Out of Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // check if already in cart
            boolean exists = false;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int pid = (int) cartModel.getValueAt(i, 0);
                if (pid == selectedProduct.getId()) {
                    // Update qty
                    int currentQty = (int) cartModel.getValueAt(i, 3);
                    cartModel.setValueAt(currentQty + qtyToOrder, i, 3);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cartModel.addRow(new Object[]{
                    selectedProduct.getId(),
                    selectedProduct.getSku(),
                    selectedProduct.getName(),
                    qtyToOrder
                });
            }
            
            // Deduct from temporary list so multiple additions are checked against remaining stock
            selectedProduct.setQuantity(selectedProduct.getQuantity() - qtyToOrder);
            
            // Refresh combo string to show new stock
            cmbProducts.removeItemAt(selectedIndex);
            cmbProducts.insertItemAt("[" + selectedProduct.getSku() + "] " + selectedProduct.getName() + " (In Stock: " + selectedProduct.getQuantity() + ")", selectedIndex);
            cmbProducts.setSelectedIndex(selectedIndex);
        });

        // Bottom Submission
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Submit Order");
        JButton btnCancel = new JButton("Cancel");
        
        btnSubmit.addActionListener(e -> {
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(dialog, "Cannot submit an empty order.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<OrderItem> newItems = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int pid = (int) cartModel.getValueAt(i, 0);
                int qty = (int) cartModel.getValueAt(i, 3);
                OrderItem item = new OrderItem();
                item.setProductId(pid);
                item.setQuantity(qty);
                newItems.add(item);
            }

            // Save transaction
            if (dao.createOrderTransaction("Pending", newItems)) {
                JOptionPane.showMessageDialog(dialog, "Order placed successfully! Inventory has been automatically deducted.");
                dialog.dispose();
                loadOrders(); // Refresh master table
            } else {
                JOptionPane.showMessageDialog(dialog, "Transaction Failed. Please check stock levels.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnSubmit);
        bottomPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(tblCart), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
