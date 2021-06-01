package com.example.demo.product;

import com.example.demo.category.Category;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(generator = "inc")
    @GenericGenerator(name = "inc", strategy = "increment")
    private int id;
    @NotBlank(message = "You have to add product name")
    private String name;
    private int amount;
    private boolean active = true;
    private LocalDateTime deliveryDate;
    private LocalDateTime sellOutDate;

    @ManyToOne
    @JoinColumn(name = "categories_id")
    private Category category;

    public Product() {
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    Product fullUpdate(Product source) {
        this.name = source.name;
        this.amount = source.amount;
        this.active = source.active;
        return this;
    }

    @PrePersist
    void prePersist() {
        deliveryDate = LocalDateTime.now();
    }


}
