package com.example.demo.adapter;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlCategoryRepository extends CategoryRepository, JpaRepository<Category, Integer> {
    @Override
    @Query("select distinct c from Category c join fetch c.products")
    List<Category> findAll();
}
