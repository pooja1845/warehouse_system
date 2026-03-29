package com.wms.controllers;

import com.wms.dao.ShipmentDAO;
import com.wms.models.Order;
import com.wms.models.Shipment;
import com.wms.views.ShipmentsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShipmentsController {
    
    private ShipmentsView view;
    private ShipmentDAO dao;

    public ShipmentsController(ShipmentsView view) {
        this.view = view;
        this.dao = new ShipmentDAO();

        initController();
        loadShipments();
    }

    private void initController() {
        view.getBtnUpdateStatus().addActionListener(e -> {
            int selectedRow = view.getTblShipments().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Please select a shipment to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int shipmentId = (int) view.getModelShipments().getValueAt(selectedRow, 0);
            String currentStatus = (String) view.getModelShipments().getValueAt(selectedRow, 2);
            
            String[] statuses = {"Pending", "In Transit", "Out for Delivery", "Delivered", "Exception"};
            String newStatus = (String) JOptionPane.showInputDialog(view, "Select new status:", "Update Shipment Status", JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);
            
            if (newStatus != null && !newStatus.equals(currentStatus)) {
                if (dao.updateShipmentStatus(shipmentId, newStatus)) {
                    JOptionPane.showMessageDialog(view, "Status updated successfully.");
                    loadShipments(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        view.getBtnCreate().addActionListener(e -> showCreateShipmentDialog());
    }

    private void loadShipments() {
        DefaultTableModel model = view.getModelShipments();
        model.setRowCount(0);

        List<Shipment> shipments = dao.getAllShipments();
        for (Shipment s : shipments) {
            model.addRow(new Object[]{
                s.getId(),
                "ORD-" + s.getOrderId(), // Format beautifully
                s.getStatus(),
                s.getTrackingNumber(),
                s.getShipDate()
            });
        }
    }

    private void showCreateShipmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Create Shipment", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(view);

        List<Order> eligibleOrders = dao.getOrdersReadyForShipment();
        if (eligibleOrders.isEmpty()) {
            JOptionPane.showMessageDialog(view, "There are currently no Pending or Processing Orders ready to be shipped.", "No Orders Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> cmbOrders = new JComboBox<>();
        for (Order o : eligibleOrders) {
            cmbOrders.addItem("Order ID: " + o.getId() + " - " + o.getStatus() + " (" + o.getOrderDate() + ")");
        }

        JTextField txtTracking = new JTextField();

        formPanel.add(new JLabel("Select Order:"));
        formPanel.add(cmbOrders);
        formPanel.add(new JLabel("Tracking Number (*):"));
        formPanel.add(txtTracking);

        // Bottom Submission
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Dispatch Order");
        JButton btnCancel = new JButton("Cancel");
        
        btnSubmit.addActionListener(e -> {
            int selectedIndex = cmbOrders.getSelectedIndex();
            if (selectedIndex == -1) return;
            
            int orderId = eligibleOrders.get(selectedIndex).getId();
            String tracking = txtTracking.getText().trim();

            if (tracking.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tracking Number is mandatory.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save transaction
            if (dao.createShipmentTransaction(orderId, tracking)) {
                JOptionPane.showMessageDialog(dialog, "Shipment Dispatched successfully!\nThe Order status has automatically changed to 'Shipped'.");
                dialog.dispose();
                loadShipments(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(dialog, "Database transaction failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnSubmit);
        bottomPanel.add(btnCancel);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
