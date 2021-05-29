package com.example.demo.category;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private CategoryRepository repository;
    private ProductRepository productRepository;

    public CategoryService(CategoryRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
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

    void deleteCategoryById(int id) {
        var target = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such category"));
        for(Product product : target.getProducts()) {
            product.setCategory(null);
        }
        target.getProducts().clear();
        repository.deleteById(id);
    }

    @Transactional
    public void replaceCategoryProducts(int id, Set<Product> newSet) {
        var target = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No category"));

        // break association between the category and the old products from both sides
        for(Product oldProduct : target.getProducts()) {
            oldProduct.setCategory(null);
            productRepository.deleteById(oldProduct.getId());
        }
        target.setProducts(null);

        // create association between category and new products
        var readyNewSet = newSet.stream()
                .map(product -> {
                    product.setCategory(target);
                    return product;
                }).collect(Collectors.toSet());
        // replace products
        target.setProducts(readyNewSet);

        // FIXME: When I remove the @Transactional and add this line,
        //  it generates an error because during persistence, the program encounters a null value in the `createGroup` method
        //  WHY if this line shouldn't even call this method ????????????????
        //repository.save(target);
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
