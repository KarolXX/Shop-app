package com.example.demo.adapter;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProductRepository extends ProductRepository, JpaRepository<Product, Integer> {
    @Override
    @Query(nativeQuery = true, value = "select * from products where amount>0 and active=true order by amount desc")
    List<Product> findActiveProducts();
}
