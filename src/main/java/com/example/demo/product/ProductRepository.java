package com.example.demo.product;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findProducts();

    List<Product> findProducts(Pageable pageable);

    Optional<Product> findProductById(Integer id);

    //Optional<Product> findProductByIdAndActiveFalse(Integer id);

    List<Product> findProductsByAmountOrderByName(Integer amount);

    Product save(Product entity);

    void deleteById(Integer id);
}
