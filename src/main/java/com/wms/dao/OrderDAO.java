package com.wms.dao;

import com.wms.models.Order;
import com.wms.models.OrderItem;
import com.wms.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return orders;

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getString("status"),
                    rs.getTimestamp("order_date")
                );
                orders.add(order);
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        // Join with products table to fetch sku and name natively
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, p.sku, p.name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ?";
                     
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return items;

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity")
                );
                item.setProductSku(rs.getString("sku"));
                item.setProductName(rs.getString("name"));
                items.add(item);
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean createOrderTransaction(String status, List<OrderItem> items) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            // Start transaction
            conn.setAutoCommit(false);

            // 1. Insert Order
            String sqlOrder = "INSERT INTO orders (status, order_date) VALUES (?, NOW())";
            PreparedStatement pstOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstOrder.setString(1, status);
            int affectedRows = pstOrder.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Get the generated Order ID
            int newOrderId;
            ResultSet generatedKeys = pstOrder.getGeneratedKeys();
            if (generatedKeys.next()) {
                newOrderId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Insert Order Items AND Deduct Inventory
            String sqlItem = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
            String sqlDeduct = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
            
            PreparedStatement pstItem = conn.prepareStatement(sqlItem);
            PreparedStatement pstDeduct = conn.prepareStatement(sqlDeduct);

            for (OrderItem item : items) {
                // Insert item mapping
                pstItem.setInt(1, newOrderId);
                pstItem.setInt(2, item.getProductId());
                pstItem.setInt(3, item.getQuantity());
                pstItem.addBatch();

                // Deduct inventory. 
                // Notice the conditional `quantity >= ?` in SQL ensures we don't go negative natively if concurrency happens.
                pstDeduct.setInt(1, item.getQuantity());
                pstDeduct.setInt(2, item.getProductId());
                pstDeduct.setInt(3, item.getQuantity()); 
                
                int deducted = pstDeduct.executeUpdate();
                if (deducted == 0) {
                    // This means either product doesn't exist OR stock was insufficient
                    System.err.println("Transaction Failed: Insufficient stock for Product ID " + item.getProductId());
                    conn.rollback();
                    return false;
                }
            }

            pstItem.executeBatch();

            // Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    // Re-enable auto-commit mode to prevent bugs in later simple queries
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, newStatus);
            pst.setInt(2, orderId);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, orderId);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
