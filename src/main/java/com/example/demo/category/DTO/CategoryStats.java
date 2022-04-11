package com.example.demo.category.DTO;

public class CategoryStats {

    private int id;

    private String name;

    private long totalQuantity;

    public CategoryStats() {
    }

    public CategoryStats(int id, String name, long totalQuantity) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
