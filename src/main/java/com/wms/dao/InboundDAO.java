package com.wms.dao;

import com.wms.models.Product;
import com.wms.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InboundDAO {

    /** Returns all inbound log records for display */
    public List<Object[]> getAllInboundLogs() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT il.id, p.sku, p.name, " +
                     "COALESCE(s.name,'Unknown') as supplier, " +
                     "il.quantity_received, il.date_received " +
                     "FROM inbound_logs il " +
                     "JOIN products p ON il.product_id = p.id " +
                     "LEFT JOIN suppliers s ON il.supplier_id = s.id " +
                     "ORDER BY il.date_received DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return rows;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getString("supplier"),
                    rs.getInt("quantity_received"),
                    rs.getTimestamp("date_received")
                });
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    /** All products for dropdown */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, sku, name, quantity FROM products ORDER BY name";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return products;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setSku(rs.getString("sku"));
                p.setName(rs.getString("name"));
                p.setQuantity(rs.getInt("quantity"));
                products.add(p);
            }
            rs.close(); pst.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return products;
    }

    /** All suppliers for dropdown */
    public List<Object[]> getAllSuppliers() {
        List<Object[]> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, name FROM suppliers ORDER BY name");
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("name")});
            }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Add a new supplier */
    public boolean addSupplier(String name, String contactInfo) {
        String sql = "INSERT INTO suppliers (name, contact_info) VALUES (?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, contactInfo);
            boolean ok = pst.executeUpdate() > 0;
            pst.close();
            return ok;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Core transaction: inserts inbound_log AND updates product quantity
     */
    public boolean receiveStockTransaction(int productId, int supplierId, int qty) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // 1. Insert log record
            PreparedStatement pstLog = conn.prepareStatement(
                "INSERT INTO inbound_logs (product_id, supplier_id, quantity_received) VALUES (?, ?, ?)"
            );
            pstLog.setInt(1, productId);
            pstLog.setInt(2, supplierId);
            pstLog.setInt(3, qty);
            if (pstLog.executeUpdate() == 0) { conn.rollback(); return false; }
            pstLog.close();

            // 2. Increase product quantity
            PreparedStatement pstProd = conn.prepareStatement(
                "UPDATE products SET quantity = quantity + ? WHERE id = ?"
            );
            pstProd.setInt(1, qty);
            pstProd.setInt(2, productId);
            if (pstProd.executeUpdate() == 0) { conn.rollback(); return false; }
            pstProd.close();

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
}
