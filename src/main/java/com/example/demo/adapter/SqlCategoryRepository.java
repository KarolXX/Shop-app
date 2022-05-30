package com.example.demo.adapter;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.category.DTO.CategoryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlCategoryRepository extends CategoryRepository, JpaRepository<Category, Integer> {
    // TODO - create query to fetch only categories in which at least one product has an amount > 0,
    //  and then fetch only such products for that
    @Override
    @Query("select distinct c from Category c inner join fetch c.products") // protection against n + 1 selects
    List<Category> findAll();

    @Override
    @Query("SELECT new com.example.demo.category.DTO.CategoryStats(p.category.id, p.category.name, sum(p.amount)) FROM Product p GROUP by p.category.id")
    List<CategoryStats> findAllStats();
}
