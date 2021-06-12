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
    // protection against n + 1 selects
    @Override
    // FIXME how to create query to fetch only categories in which at least one product has an amount > 0,
    //  and then fetch only such products for that
    @Query("select distinct c from Category c inner join fetch c.products")
    List<Category> findAll();
}
