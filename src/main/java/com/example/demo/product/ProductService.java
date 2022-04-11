package com.example.demo.product;

import com.example.demo.category.CategoryRepository;
import com.example.demo.product.DTO.ProductUpdateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Product updateProduct(int id, Product source) {
        var target = repository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("No active product with given id"));
        var result = target.fullUpdate(source);
        return result;
    }

    @Transactional
    public Product changeAmount(int id, ProductUpdateModel source) {
        var target = repository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with given id not found"));

        // change totalQuantity if product is associated with category
        var category = target.getCategory();
        if(category != null) {
            category.setTotalQuantity(
                    category.getTotalQuantity() - target.getAmount() + source.getAmount()
            );
        }

        target.setAmount(source.getAmount());
        return target;
    }

    @Transactional
    public void changeActive(int id) {
        var target = repository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("No product with given id"));
        target.setActive(!target.isActive());
    }
}
