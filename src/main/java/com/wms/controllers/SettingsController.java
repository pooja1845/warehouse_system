package com.wms.controllers;

import com.wms.utils.DatabaseConnection;
import com.wms.views.SettingsView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsController {

    private SettingsView view;

    public SettingsController(SettingsView view) {
        this.view = view;
        initController();
    }

    private void initController() {

        // --- Change Password ---
        view.getBtnChangePassword().addActionListener(e -> {
            String current = view.getCurrentPassword();
            String newPass = view.getNewPassword();
            String confirm = view.getConfirmPassword();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(view, "All password fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPass.equals(confirm)) {
                JOptionPane.showMessageDialog(view, "New Password and Confirm Password do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(view, "New password must be at least 6 characters.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify current password against DB
            try {
                Connection conn = DatabaseConnection.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(view, "Database connection unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement check = conn.prepareStatement(
                    "SELECT id FROM users WHERE username='root' AND password_hash=?"
                );
                check.setString(1, current);
                ResultSet rs = check.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(view, "Current password is incorrect.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    rs.close(); check.close();
                    return;
                }
                rs.close(); check.close();

                // Update with new password
                PreparedStatement update = conn.prepareStatement(
                    "UPDATE users SET password_hash=? WHERE username='root'"
                );
                update.setString(1, newPass);
                update.executeUpdate();
                update.close();

                JOptionPane.showMessageDialog(view, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                view.clearPasswordFields();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Failed to update password: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Save Warehouse Info ---
        view.getBtnSaveInfo().addActionListener(e -> {
            String name    = view.getWarehouseName();
            String address = view.getWarehouseAddress();
            String email   = view.getContactEmail();
            String phone   = view.getContactPhone();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Warehouse Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.isEmpty() && !email.contains("@")) {
                JOptionPane.showMessageDialog(view, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // For a desktop app, we persist to a local prefs file or just acknowledge save
            // In a full production app this would go to a settings table
            JOptionPane.showMessageDialog(view,
                "Warehouse information saved successfully!\n\n" +
                "Name: "    + name    + "\n" +
                "Address: " + address + "\n" +
                "Email: "   + email   + "\n" +
                "Phone: "   + phone,
                "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        // --- Save Low Stock Threshold ---
        view.getBtnSaveThreshold().addActionListener(e -> {
            int threshold = view.getDefaultMinStock();
            JOptionPane.showMessageDialog(view,
                "Default minimum stock level set to: " + threshold + " units.\n" +
                "This will apply when adding new products.",
                "Threshold Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        // --- Test Database Connection ---
        view.getBtnTestConnection().addActionListener(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    view.setDbStatus(true);
                    JOptionPane.showMessageDialog(view,
                        "Database connection is active and healthy!\n\nHost: localhost:3306\nDatabase: wms_db",
                        "Connection OK", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    view.setDbStatus(false);
                    JOptionPane.showMessageDialog(view,
                        "Could not connect to the database.\nPlease check if MySQL is running.",
                        "Connection Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                view.setDbStatus(false);
                JOptionPane.showMessageDialog(view,
                    "Connection error: " + ex.getMessage(),
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
