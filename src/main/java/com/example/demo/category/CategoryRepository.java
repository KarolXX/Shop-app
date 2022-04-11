package com.example.demo.category;

import com.example.demo.category.DTO.CategoryStats;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();

    List<CategoryStats> findAllStats();

    Optional<Category> findById(Integer id);

    Category save(Category entity);

    void deleteById(Integer id);

}
