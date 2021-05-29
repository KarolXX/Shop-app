package com.example.demo.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();

    Optional<Category> findById(Integer id);

    Category save(Category entity);

    void deleteById(Integer id);
}
