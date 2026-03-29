package com.wms.controllers;

import com.wms.views.DashboardView;
import com.wms.views.InboundView;
import com.wms.views.InventoryView;
import com.wms.views.MainFrame;
import com.wms.views.OrdersView;
import com.wms.views.ReportsView;
import com.wms.views.SettingsView;
import com.wms.views.ShipmentsView;

import javax.swing.*;

public class MainFrameController {

    private MainFrame mainFrame;
    private DashboardView dashboardView;
    private DashboardController dashboardController;
    // Keep references to sub-controllers to prevent GC and suppress lint
    private InventoryController inventoryController;
    private InboundController inboundController;
    private OrdersController ordersController;
    private ShipmentsController shipmentsController;
    private ReportsController reportsController;
    private SettingsController settingsController;

    public MainFrameController() {
        mainFrame = new MainFrame();

        // 1. Dashboard
        dashboardView = new DashboardView();
        dashboardController = new DashboardController(dashboardView);

        // 2. Inventory
        InventoryView inventoryView = new InventoryView();
        inventoryController = new InventoryController(inventoryView);

        // 3. Inbound Receiving
        InboundView inboundView = new InboundView();
        inboundController = new InboundController(inboundView);

        // 4. Orders
        OrdersView ordersView = new OrdersView();
        ordersController = new OrdersController(ordersView);
        
        // 4. Shipments
        ShipmentsView shipmentsView = new ShipmentsView();
        shipmentsController = new ShipmentsController(shipmentsView);
        
        // 5. Reports
        ReportsView reportsView = new ReportsView();
        reportsController = new ReportsController(reportsView);
        
        // 6. Settings
        SettingsView settingsView = new SettingsView();
        settingsController = new SettingsController(settingsView);

        // Add views to MainFrame
        mainFrame.addContentView(dashboardView,  "Dashboard");
        mainFrame.addContentView(inventoryView,  "Inventory");
        mainFrame.addContentView(inboundView,    "Inbound");
        mainFrame.addContentView(ordersView,     "Orders");
        mainFrame.addContentView(shipmentsView,  "Shipments");
        mainFrame.addContentView(reportsView,    "Reports");
        mainFrame.addContentView(settingsView,   "Settings");

        // Setup Listeners
        setupListeners();

        // Show default view
        mainFrame.showView("Dashboard");
        mainFrame.setActiveTab(mainFrame.getBtnDashboard());
    }

    private void setupListeners() {

        // Sidebar Navigation
        mainFrame.addNavListenerDashboard(e -> {
            dashboardController.loadDashboardData(); // Refresh Data
            mainFrame.showView("Dashboard");
            mainFrame.setActiveTab(mainFrame.getBtnDashboard());
        });
        mainFrame.addNavListenerInventory(e -> {
            mainFrame.showView("Inventory");
            mainFrame.setActiveTab(mainFrame.getBtnInventory());
        });
        mainFrame.addNavListenerInbound(e -> {
            mainFrame.showView("Inbound");
            mainFrame.setActiveTab(mainFrame.getBtnInbound());
        });
        mainFrame.addNavListenerOrders(e -> {
            mainFrame.showView("Orders");
            mainFrame.setActiveTab(mainFrame.getBtnOrders());
        });
        mainFrame.addNavListenerShipments(e -> {
            mainFrame.showView("Shipments");
            mainFrame.setActiveTab(mainFrame.getBtnShipments());
        });
        mainFrame.addNavListenerReports(e -> {
            mainFrame.showView("Reports");
            mainFrame.setActiveTab(mainFrame.getBtnReports());
        });
        mainFrame.addNavListenerSettings(e -> {
            mainFrame.showView("Settings");
            mainFrame.setActiveTab(mainFrame.getBtnSettings());
        });

        mainFrame.addNavListenerLogout(e -> {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                com.wms.MainApplication.main(new String[]{}); 
            }
        });

        // Dashboard Interactivity Navigation
        dashboardController.addTotalStockClickListener(() -> mainFrame.showView("Inventory"));
        dashboardController.addPendingOrdersClickListener(() -> mainFrame.showView("Orders"));
        dashboardController.addShipmentsClickListener(() -> mainFrame.showView("Shipments"));
        dashboardController.addLowStockClickListener(() -> mainFrame.showView("Inventory")); // Low stock is part of inventory
    }


    public void show() {
        mainFrame.setVisible(true);
    }
}
