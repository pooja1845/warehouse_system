package com.wms.views;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsView extends JPanel {

    // --- Change Password fields ---
    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnChangePassword;

    // --- Warehouse Info fields ---
    private JTextField txtWarehouseName;
    private JTextField txtWarehouseAddress;
    private JTextField txtContactEmail;
    private JTextField txtContactPhone;
    private JButton btnSaveInfo;

    // --- DB Info (read-only display) ---
    private JLabel lblDbStatus;
    private JButton btnTestConnection;

    // --- Low Stock Threshold ---
    private JSpinner spnDefaultMinStock;
    private JButton btnSaveThreshold;

    public SettingsView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(33, 33, 33));
        add(title, BorderLayout.NORTH);

        // Outer scroll pane wrapping all settings cards
        JPanel settingsContent = new JPanel();
        settingsContent.setLayout(new BoxLayout(settingsContent, BoxLayout.Y_AXIS));
        settingsContent.setOpaque(false);
        settingsContent.add(buildWarehouseInfoCard());
        settingsContent.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsContent.add(buildChangePasswordCard());
        settingsContent.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsContent.add(buildLowStockThresholdCard());
        settingsContent.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsContent.add(buildDatabaseInfoCard());

        JScrollPane scroll = new JScrollPane(settingsContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // ------------------------------------------------
    // Card 1: Warehouse Information
    // ------------------------------------------------
    private JPanel buildWarehouseInfoCard() {
        JPanel card = createCard("Warehouse Information");
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        txtWarehouseName    = new JTextField("My Warehouse");
        txtWarehouseAddress = new JTextField("123 Storage Lane, Logistics Park");
        txtContactEmail     = new JTextField("admin@warehouse.com");
        txtContactPhone     = new JTextField("+91 98765 43210");
        btnSaveInfo         = createButton("Save Information", new Color(39, 174, 96));

        addRow(card, gbc, "Warehouse Name:", txtWarehouseName, 0);
        addRow(card, gbc, "Address:",        txtWarehouseAddress, 1);
        addRow(card, gbc, "Contact Email:",  txtContactEmail, 2);
        addRow(card, gbc, "Contact Phone:",  txtContactPhone, 3);

        gbc.gridy = 4; gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        card.add(btnSaveInfo, gbc);
        return card;
    }

    // ------------------------------------------------
    // Card 2: Change Password
    // ------------------------------------------------
    private JPanel buildChangePasswordCard() {
        JPanel card = createCard("Change Admin Password");
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        txtCurrentPassword = new JPasswordField();
        txtNewPassword     = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        btnChangePassword  = createButton("Update Password", new Color(25, 118, 210));

        addRow(card, gbc, "Current Password:", txtCurrentPassword, 0);
        addRow(card, gbc, "New Password:",     txtNewPassword, 1);
        addRow(card, gbc, "Confirm Password:", txtConfirmPassword, 2);

        gbc.gridy = 3; gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        card.add(btnChangePassword, gbc);
        return card;
    }

    // ------------------------------------------------
    // Card 3: Low Stock Default Threshold
    // ------------------------------------------------
    private JPanel buildLowStockThresholdCard() {
        JPanel card = createCard("Inventory Alerts");
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        spnDefaultMinStock = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
        btnSaveThreshold   = createButton("Save Threshold", new Color(243, 156, 18));

        addRow(card, gbc, "Default Min Stock Level:", spnDefaultMinStock, 0);

        JLabel hint = new JLabel("  This default is applied when adding new products via the Inventory module.");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(hint, gbc);

        gbc.gridy = 2; gbc.gridx = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        card.add(btnSaveThreshold, gbc);
        return card;
    }

    // ------------------------------------------------
    // Card 4: Database Info (read-only)
    // ------------------------------------------------
    private JPanel buildDatabaseInfoCard() {
        JPanel card = createCard("Database Connection");
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        JLabel lblHost = new JLabel("localhost:3306 / wms_db");
        lblHost.setFont(new Font("Arial", Font.PLAIN, 13));

        lblDbStatus = new JLabel("● Connected");
        lblDbStatus.setFont(new Font("Arial", Font.BOLD, 13));
        lblDbStatus.setForeground(new Color(27, 94, 32));

        btnTestConnection = createButton("Test Connection", new Color(149, 165, 166));

        addLabelRow(card, gbc, "Host / Database:", lblHost, 0);
        addLabelRow(card, gbc, "Status:", lblDbStatus, 1);

        gbc.gridy = 2; gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        card.add(btnTestConnection, gbc);
        return card;
    }

    // ------------------------------------------------
    // Helpers
    // ------------------------------------------------
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "  " + title + "  "
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        border.setTitleColor(new Color(25, 118, 210));
        card.setBorder(BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        return card;
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void addLabelRow(JPanel panel, GridBagConstraints gbc, String labelText, JLabel valueLabel, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(valueLabel, gbc);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- Getters ---
    public String getCurrentPassword()  { return new String(txtCurrentPassword.getPassword()); }
    public String getNewPassword()      { return new String(txtNewPassword.getPassword()); }
    public String getConfirmPassword()  { return new String(txtConfirmPassword.getPassword()); }
    public void clearPasswordFields()   { txtCurrentPassword.setText(""); txtNewPassword.setText(""); txtConfirmPassword.setText(""); }

    public String getWarehouseName()    { return txtWarehouseName.getText().trim(); }
    public String getWarehouseAddress() { return txtWarehouseAddress.getText().trim(); }
    public String getContactEmail()     { return txtContactEmail.getText().trim(); }
    public String getContactPhone()     { return txtContactPhone.getText().trim(); }

    public int getDefaultMinStock()     { return (int) spnDefaultMinStock.getValue(); }

    public void setDbStatus(boolean connected) {
        if (connected) {
            lblDbStatus.setText("● Connected");
            lblDbStatus.setForeground(new Color(27, 94, 32));
        } else {
            lblDbStatus.setText("● Disconnected");
            lblDbStatus.setForeground(new Color(183, 28, 28));
        }
    }

    public JButton getBtnChangePassword()  { return btnChangePassword; }
    public JButton getBtnSaveInfo()        { return btnSaveInfo; }
    public JButton getBtnSaveThreshold()   { return btnSaveThreshold; }
    public JButton getBtnTestConnection()  { return btnTestConnection; }
}
