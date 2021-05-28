package com.example.demo.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Category> getAll() {
        // FIXME: `totalQuantity` is not computed automatically
        //  when we change amount of specific product that belongs to category
        //  thanks to the @Transactional it updates itself after downloading all products
        //  but I would like to update it as soon as the product `amount` has changed
        return repository.findAll().stream()
                .map(category -> {
                    category.setTotalQuantity(
                            getTotalQuantityProducts(category)
                    );
                    return category;
                }).collect(Collectors.toList());
    }

    Category createCategory(Category category) {
        category.setTotalQuantity(
                getTotalQuantityProducts(category)
        );
        // add `category` to products that are created along with category
        category.setProducts(
                category.getProducts().stream()
                .map(product -> {
                    product.setCategory(category);
                    return product;
                }).collect(Collectors.toSet())
        );
        return repository.save(category);
    }

    // I labeled this method static because it doesn't use the individual characteristics of this class
    // - with the keyword static, the method has a smaller size
    private static int getTotalQuantityProducts(Category category) {
        var productsAmount = category.getProducts().stream()
                .map(product -> product.getAmount())
                .collect(Collectors.toList());
        int result = 0;
        for(int product : productsAmount) {
            result += product;
        }
        return result;
    }
}
