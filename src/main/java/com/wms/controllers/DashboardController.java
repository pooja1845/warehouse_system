package com.wms.controllers;

import com.wms.utils.DatabaseConnection;
import com.wms.views.DashboardView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardController {

    private DashboardView view;

    public DashboardController(DashboardView view) {
        this.view = view;
        loadDashboardData();
    }

    public void loadDashboardData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if(conn == null) return;

            // 1. Total Stock
            ResultSet rsStock = conn.createStatement().executeQuery("SELECT COALESCE(SUM(quantity),0) as total FROM products");
            int totalStock = rsStock.next() ? rsStock.getInt("total") : 0;

            // 2. Pending Orders
            ResultSet rsOrders = conn.createStatement().executeQuery("SELECT COUNT(*) as pending FROM orders WHERE status='Pending'");
            int pendingOrders = rsOrders.next() ? rsOrders.getInt("pending") : 0;

            // 3. Shipments Today
            ResultSet rsShip = conn.createStatement().executeQuery("SELECT COUNT(*) as today FROM shipments WHERE DATE(ship_date)=CURDATE() OR status='Shipped'");
            int todayShipments = rsShip.next() ? rsShip.getInt("today") : 0;

            // 4. Low Stock Alerts
            ResultSet rsLow = conn.createStatement().executeQuery("SELECT COUNT(*) as low FROM products WHERE quantity <= min_stock_level");
            int lowStockAlerts = rsLow.next() ? rsLow.getInt("low") : 0;

            view.updateKPIs(String.valueOf(totalStock), String.valueOf(pendingOrders),
                            String.valueOf(todayShipments), String.valueOf(lowStockAlerts));

            // 5. Inbound Shipments — real data from inbound_logs joined with products & suppliers
            java.util.List<Object[]> inboundRows = new java.util.ArrayList<>();
            String sqlInbound = "SELECT il.id, p.name, COALESCE(s.name,'Unknown') as supplier, il.quantity_received, il.date_received " +
                                "FROM inbound_logs il " +
                                "JOIN products p ON il.product_id = p.id " +
                                "LEFT JOIN suppliers s ON il.supplier_id = s.id " +
                                "ORDER BY il.date_received DESC LIMIT 5";
            ResultSet rsInbound = conn.createStatement().executeQuery(sqlInbound);
            while (rsInbound.next()) {
                inboundRows.add(new Object[]{
                    rsInbound.getInt("id"),
                    rsInbound.getString("name"),
                    rsInbound.getString("supplier"),
                    rsInbound.getInt("quantity_received"),
                    rsInbound.getTimestamp("date_received")
                });
            }
            view.updateInboundTable(inboundRows);

            // 6. Picking Tasks — active (Pending/Processing) orders
            java.util.List<Object[]> taskRows = new java.util.ArrayList<>();
            String sqlTasks = "SELECT o.id, o.status, o.order_date, COUNT(oi.id) as item_count " +
                              "FROM orders o LEFT JOIN order_items oi ON o.id = oi.order_id " +
                              "WHERE o.status IN ('Pending','Processing') " +
                              "GROUP BY o.id, o.status, o.order_date " +
                              "ORDER BY o.order_date ASC LIMIT 5";
            ResultSet rsTasks = conn.createStatement().executeQuery(sqlTasks);
            while (rsTasks.next()) {
                taskRows.add(new Object[]{
                    "ORD-" + rsTasks.getInt("id"),
                    rsTasks.getString("status"),
                    rsTasks.getTimestamp("order_date"),
                    rsTasks.getInt("item_count") + " items"
                });
            }
            view.updatePickingTasksTable(taskRows);

            // 7. Low Stock Items — products below threshold
            java.util.List<Object[]> lowRows = new java.util.ArrayList<>();
            String sqlLowStock = "SELECT sku, name, quantity, min_stock_level FROM products " +
                                 "WHERE quantity <= min_stock_level ORDER BY quantity ASC LIMIT 5";
            ResultSet rsLowItems = conn.createStatement().executeQuery(sqlLowStock);
            while (rsLowItems.next()) {
                lowRows.add(new Object[]{
                    rsLowItems.getString("sku"),
                    rsLowItems.getString("name"),
                    rsLowItems.getInt("quantity"),
                    rsLowItems.getInt("min_stock_level")
                });
            }
            view.updateLowStockTable(lowRows);

            // 8. Bar Chart — Category wise stock distribution
            Map<String, Integer> categoryData = new LinkedHashMap<>();
            ResultSet rsCat = conn.createStatement().executeQuery(
                "SELECT COALESCE(category,'Uncategorized') as category, SUM(quantity) as total " +
                "FROM products GROUP BY category ORDER BY total DESC");
            while (rsCat.next()) {
                categoryData.put(rsCat.getString("category"), rsCat.getInt("total"));
            }
            view.updateBarChart(categoryData);

            // 9. Ring Chart — Order status counts
            Map<String, Integer> statusData = new LinkedHashMap<>();
            // Initialise all known statuses to 0 so colours are pre-assigned
            statusData.put("Pending",    0);
            statusData.put("Processing", 0);
            statusData.put("Shipped",    0);
            statusData.put("Delivered",  0);
            statusData.put("Cancelled",  0);
            ResultSet rsStatus = conn.createStatement().executeQuery(
                "SELECT status, COUNT(*) as cnt FROM orders GROUP BY status");
            while (rsStatus.next()) {
                statusData.put(rsStatus.getString("status"), rsStatus.getInt("cnt"));
            }
            view.updateRingChart(statusData);

            // 10. Recent Activity — UNION of orders, shipments, inbound_logs (latest 5)
            java.util.List<String[]> activities = new java.util.ArrayList<>();
            String sqlActivityMySQL =
                "SELECT CONCAT('Order #', id, ' created — Status: ', status) AS msg, 'order' AS type, order_date AS ts " +
                "FROM orders " +
                "UNION ALL " +
                "SELECT CONCAT('Shipment dispatched — Order #', order_id, ' | Tracking: ', tracking_number), 'shipment', ship_date " +
                "FROM shipments WHERE ship_date IS NOT NULL " +
                "UNION ALL " +
                "SELECT CONCAT('Inbound received: ', quantity_received, ' units (Log #', id, ')'), 'inbound', date_received " +
                "FROM inbound_logs " +
                "ORDER BY ts DESC LIMIT 5";
            ResultSet rsAct = conn.createStatement().executeQuery(sqlActivityMySQL);
            while (rsAct.next()) {
                activities.add(new String[]{rsAct.getString("msg"), rsAct.getString("type")});
            }
            // Also add low-stock warnings
            ResultSet rsLowAlert = conn.createStatement().executeQuery(
                "SELECT CONCAT('⚠ Low stock: ', name, ' (', quantity, ' left)') AS msg FROM products " +
                "WHERE quantity <= min_stock_level ORDER BY quantity ASC LIMIT 2");
            while (rsLowAlert.next()) {
                activities.add(0, new String[]{rsLowAlert.getString("msg"), "stock"}); // insert at top
            }
            if (activities.size() > 5) activities = activities.subList(0, 5); // keep max 5
            view.updateRecentActivity(activities);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTotalStockClickListener(Runnable action) {
        view.getBoxTotalStock().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }

    public void addPendingOrdersClickListener(Runnable action) {
        view.getBoxPendingOrders().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }

    public void addShipmentsClickListener(Runnable action) {
        view.getBoxShipments().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }

    public void addLowStockClickListener(Runnable action) {
        view.getBoxLowStock().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
    }
}
