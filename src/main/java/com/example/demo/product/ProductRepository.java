package com.example.demo.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findProducts();

    Optional<Product> findProductById(Integer id);

    //Optional<Product> findProductByIdAndActiveFalse(Integer id);

    List<Product> findProductsByAmountOrderByName(Integer amount);

    Product save(Product entity);

    void deleteById(Integer id);
}
