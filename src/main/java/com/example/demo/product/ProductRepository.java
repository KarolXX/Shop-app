package com.example.demo.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findProducts();

    Page<Product> findProducts(Pageable pageable);

    Optional<Product> findProductById(Integer id);

    List<Product> findProductsByAmountOrderByName(Integer amount);

    Product save(Product entity);

    void deleteById(Integer id);
}
