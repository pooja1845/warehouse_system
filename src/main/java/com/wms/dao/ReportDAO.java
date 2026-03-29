package com.wms.dao;

import com.wms.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    // --- Inventory Summary ---
    public List<Object[]> getInventorySummary() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT sku, name, category, location, quantity, min_stock_level, " +
                     "CASE WHEN quantity <= min_stock_level THEN 'LOW STOCK' ELSE 'OK' END as stock_status " +
                     "FROM products ORDER BY category, name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return rows;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level"),
                    rs.getString("stock_status")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // --- Orders Summary ---
    public List<Object[]> getOrdersSummary() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT o.id, o.status, o.order_date, COUNT(oi.id) as item_count, " +
                     "COALESCE(SUM(oi.quantity), 0) as total_qty " +
                     "FROM orders o LEFT JOIN order_items oi ON o.id = oi.order_id " +
                     "GROUP BY o.id, o.status, o.order_date ORDER BY o.order_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return rows;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("status"),
                    rs.getTimestamp("order_date"),
                    rs.getInt("item_count"),
                    rs.getInt("total_qty")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // --- Shipments Summary ---
    public List<Object[]> getShipmentsSummary() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT s.id, s.order_id, s.status, s.tracking_number, s.ship_date " +
                     "FROM shipments s ORDER BY s.ship_date DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return rows;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    "ORD-" + rs.getInt("order_id"),
                    rs.getString("status"),
                    rs.getString("tracking_number"),
                    rs.getTimestamp("ship_date")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // --- Low Stock Alert Report ---
    public List<Object[]> getLowStockReport() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT sku, name, category, quantity, min_stock_level, " +
                     "(min_stock_level - quantity) as shortage " +
                     "FROM products WHERE quantity <= min_stock_level ORDER BY shortage DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return rows;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level"),
                    rs.getInt("shortage")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // --- KPI Summary Numbers ---
    public Map<String, Integer> getKPISummary() {
        Map<String, Integer> kpi = new LinkedHashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return kpi;

            ResultSet rs;

            rs = conn.createStatement().executeQuery("SELECT COALESCE(SUM(quantity),0) as total FROM products");
            kpi.put("Total Stock Items", rs.next() ? rs.getInt("total") : 0);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) as c FROM products WHERE quantity <= min_stock_level");
            kpi.put("Low Stock Alerts", rs.next() ? rs.getInt("c") : 0);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) as c FROM orders WHERE status='Pending'");
            kpi.put("Pending Orders", rs.next() ? rs.getInt("c") : 0);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) as c FROM orders WHERE status='Shipped'");
            kpi.put("Shipped Orders", rs.next() ? rs.getInt("c") : 0);

            rs = conn.createStatement().executeQuery("SELECT COUNT(*) as c FROM shipments WHERE DATE(ship_date)=CURDATE()");
            kpi.put("Shipments Today", rs.next() ? rs.getInt("c") : 0);

        } catch (SQLException e) { e.printStackTrace(); }
        return kpi;
    }
}
