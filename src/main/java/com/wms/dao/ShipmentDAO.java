package com.wms.dao;

import com.wms.models.Order;
import com.wms.models.Shipment;
import com.wms.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {

    public List<Shipment> getAllShipments() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM shipments ORDER BY ship_date DESC";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return shipments;

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Shipment s = new Shipment(
                    rs.getInt("id"),
                    rs.getInt("order_id"),
                    rs.getString("status"),
                    rs.getString("tracking_number"),
                    rs.getTimestamp("ship_date")
                );
                shipments.add(s);
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }

    public List<Order> getOrdersReadyForShipment() {
        List<Order> orders = new ArrayList<>();
        // Fetch only Orders that are Pending or Processing so they can be shipped
        String sql = "SELECT * FROM orders WHERE status IN ('Pending', 'Processing') ORDER BY order_date ASC";
        
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

    public boolean createShipmentTransaction(int orderId, String trackingNumber) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false);

            // 1. Insert into Shipments
            String sqlShipment = "INSERT INTO shipments (order_id, status, tracking_number, ship_date) VALUES (?, 'In Transit', ?, NOW())";
            PreparedStatement pstShipment = conn.prepareStatement(sqlShipment);
            pstShipment.setInt(1, orderId);
            pstShipment.setString(2, trackingNumber);
            int affectedShipment = pstShipment.executeUpdate();
            
            if (affectedShipment == 0) {
                conn.rollback();
                return false;
            }
            pstShipment.close();

            // 2. Update parent Order status to 'Shipped'
            String sqlOrder = "UPDATE orders SET status = 'Shipped' WHERE id = ?";
            PreparedStatement pstOrder = conn.prepareStatement(sqlOrder);
            pstOrder.setInt(1, orderId);
            int affectedOrder = pstOrder.executeUpdate();
            
            if (affectedOrder == 0) {
                conn.rollback();
                return false;
            }
            pstOrder.close();

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
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean updateShipmentStatus(int shipmentId, String newStatus) {
        String sql = "UPDATE shipments SET status = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newStatus);
            pst.setInt(2, shipmentId);
            
            boolean result = pst.executeUpdate() > 0;
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
