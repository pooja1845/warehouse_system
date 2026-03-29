package com.wms.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JButton btnDashboard;
    private JButton btnInventory;
    private JButton btnInbound;
    private JButton btnOrders;
    private JButton btnShipments;
    private JButton btnReports;
    private JButton btnSettings;
    private JButton btnLogout;

    public MainFrame() {
        setTitle("Warehouse Management System - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 246, 250)); // Light gray background for body

        // Top Navigation Panel
        JPanel topNavPanel = new JPanel(new BorderLayout());
        topNavPanel.setBackground(new Color(255, 255, 255));
        topNavPanel.setPreferredSize(new Dimension(getWidth(), 60));
        topNavPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Left Logo + Nav Tabs
        JPanel leftNavLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftNavLayout.setOpaque(false);

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        logoPanel.setBackground(new Color(25, 118, 210)); // WMS Blue
        logoPanel.setPreferredSize(new Dimension(150, 60));
        JLabel lblLogo = new JLabel("WMS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 22));
        logoPanel.add(lblLogo);
        leftNavLayout.add(logoPanel);

        // Tabs
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        tabsPanel.setOpaque(false);
        
        btnDashboard = createNavButton("Dashboard", true);
        btnInventory = createNavButton("Inventory", false);
        btnInbound   = createNavButton("Inbound", false);
        btnOrders    = createNavButton("Orders", false);
        btnShipments = createNavButton("Shipments", false);
        btnReports   = createNavButton("Reports", false);
        btnSettings  = createNavButton("Settings", false);

        tabsPanel.add(btnDashboard);
        tabsPanel.add(btnInventory);
        tabsPanel.add(btnInbound);
        tabsPanel.add(btnOrders);
        tabsPanel.add(btnShipments);
        tabsPanel.add(btnReports);
        tabsPanel.add(btnSettings);

        leftNavLayout.add(tabsPanel);

        // Right side (Search, Notifications, Profile placeholder)
        JPanel rightNavLayout = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rightNavLayout.setOpaque(false);

        JTextField txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search...");
        rightNavLayout.add(txtSearch);

        btnLogout = createNavButton("Logout", false);
        btnLogout.setForeground(new Color(211, 47, 47));
        rightNavLayout.add(btnLogout);

        topNavPanel.add(leftNavLayout, BorderLayout.WEST);
        topNavPanel.add(rightNavLayout, BorderLayout.EAST);

        add(topNavPanel, BorderLayout.NORTH);

        // Content Panel (CardLayout for switching views)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isActive) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setForeground(new Color(25, 118, 210));
            // Simulate active underline using border
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(25, 118, 210)));
        } else {
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setForeground(new Color(100, 100, 100));
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0)); // keep height same
        }
        
        return btn;
    }

    // Tab switching visual effect (naive implementation for now)
    public void setActiveTab(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnInventory, btnInbound, btnOrders, btnShipments, btnReports, btnSettings};
        for (JButton b : buttons) {
            b.setFont(new Font("Arial", Font.PLAIN, 14));
            b.setForeground(new Color(100, 100, 100));
            b.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        }
        activeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        activeBtn.setForeground(new Color(25, 118, 210));
        activeBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(25, 118, 210)));
    }

    public void addContentView(JPanel panel, String name) {
        contentPanel.add(panel, name);
    }

    public void showView(String name) {
        cardLayout.show(contentPanel, name);
    }

    public void addNavListenerDashboard(ActionListener l)  { btnDashboard.addActionListener(l); }
    public void addNavListenerInventory(ActionListener l)  { btnInventory.addActionListener(l); }
    public void addNavListenerInbound(ActionListener l)    { btnInbound.addActionListener(l); }
    public void addNavListenerOrders(ActionListener l)     { btnOrders.addActionListener(l); }
    public void addNavListenerShipments(ActionListener l)  { btnShipments.addActionListener(l); }
    public void addNavListenerReports(ActionListener l)    { btnReports.addActionListener(l); }
    public void addNavListenerSettings(ActionListener l)   { btnSettings.addActionListener(l); }
    public void addNavListenerLogout(ActionListener l)     { btnLogout.addActionListener(l); }

    public JButton getBtnDashboard()  { return btnDashboard; }
    public JButton getBtnInventory()   { return btnInventory; }
    public JButton getBtnInbound()     { return btnInbound; }
    public JButton getBtnOrders()      { return btnOrders; }
    public JButton getBtnShipments()   { return btnShipments; }
    public JButton getBtnReports()     { return btnReports; }
    public JButton getBtnSettings()    { return btnSettings; }
}
