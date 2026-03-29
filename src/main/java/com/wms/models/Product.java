package com.wms.models;

public class Product {
    private int id;
    private String sku;
    private String name;
    private int quantity;
    private String category;
    private String location;
    private int minStockLevel;

    public Product() {}

    public Product(int id, String sku, String name, int quantity, String category, String location, int minStockLevel) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.location = location;
        this.minStockLevel = minStockLevel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
}
