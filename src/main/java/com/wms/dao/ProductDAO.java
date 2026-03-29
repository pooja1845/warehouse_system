package com.wms.dao;

import com.wms.models.Product;
import com.wms.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        return fetchProducts("SELECT * FROM products ORDER BY name ASC", null);
    }

    public List<Product> searchProducts(String query) {
        String sql = "SELECT * FROM products WHERE sku LIKE ? OR name LIKE ? OR category LIKE ? ORDER BY name ASC";
        String likeQuery = "%" + query + "%";
        return fetchProducts(sql, new String[]{likeQuery, likeQuery, likeQuery});
    }

    private List<Product> fetchProducts(String sql, String[] params) {
        List<Product> products = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return products;
            
            PreparedStatement pst = conn.prepareStatement(sql);
            if (params != null) {
                for(int i = 0; i < params.length; i++){
                    pst.setString(i+1, params[i]);
                }
            }
            
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("sku"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getInt("min_stock_level")
                );
                products.add(p);
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (sku, name, quantity, category, location, min_stock_level) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, p.getSku());
            pst.setString(2, p.getName());
            pst.setInt(3, p.getQuantity());
            pst.setString(4, p.getCategory());
            pst.setString(5, p.getLocation());
            pst.setInt(6, p.getMinStockLevel());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET sku=?, name=?, quantity=?, category=?, location=?, min_stock_level=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, p.getSku());
            pst.setString(2, p.getName());
            pst.setInt(3, p.getQuantity());
            pst.setString(4, p.getCategory());
            pst.setString(5, p.getLocation());
            pst.setInt(6, p.getMinStockLevel());
            pst.setInt(7, p.getId());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
