package com.example.demo.category;

import com.example.demo.category.DTO.CategoryStats;
import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository repository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public List<CategoryStats> getAllStats() {
        return repository.findAllStats();
    }

//    @Transactional
//    public List<Category> getAll() {
//        return repository.findAll().stream()
//                .map(category -> {
//                    Set<Product> products = category.getProducts();
//                    if(!ObjectUtils.isEmpty(products)) {
//                        int totalQuantity = products.stream().mapToInt(Product::getAmount).sum();
//                        category.setTotalQuantity(totalQuantity);
//                    }
//                    return category;
//                }).collect(Collectors.toList());
//    }

    List<Product> getProducts(int id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No category with given id"));
        var categoryProducts = new ArrayList<>(category.getProducts());
        var result = categoryProducts.stream()
                .filter(Product::isActive)
                .filter(product -> product.getAmount() > 0)
                .sorted(Comparator.comparing(Product::getAmount))
                .collect(Collectors.toList());
        return new ArrayList<>(result);
    }

    Category createCategory(Category category) {
//        category.setTotalQuantity(
//                computeTotalQuantityProducts(category)
//        );
        // set `category` to products that are created along with category
        category.setProducts(
                category.getProducts().stream()
                .map(product -> {
                    product.setCategory(category);
                    return product;
                }).collect(Collectors.toSet())
        );
        return repository.save(category);
    }

    // after a category is deleted, its products are not removed,
    // they are still in the database,
    // but no longer associated with the category
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
                .orElseThrow(() -> new IllegalArgumentException("No such category"));

        // break association between the category and the old products from product side
        // ( Association is broken later from the category side )
        for(Product oldProduct : target.getProducts()) {
            oldProduct.setCategory(null);
            productRepository.deleteById(oldProduct.getId());
        }

        // create association between category and new products
        var readyNewSet = newSet.stream()
                .map(product -> {
                    product.setCategory(target);
                    return product;
                }).collect(Collectors.toSet());
        // replace products ( and break association between the category and the old products from category side )
        target.setProducts(readyNewSet);

        // FIXME: When I remove the @Transactional and add this line,
        //  it generates an error because during persistence, the program encounters a null value in the `createGroup` method
        //  WHY if this line shouldn't even call this method ????????????????
        //repository.save(target);
    }

    // I labeled this method static because it doesn't use the individual characteristics of this class
    // - with the keyword static, the method has a smaller size
//    public static int computeTotalQuantityProducts(Category category) {
//        var productsAmount = category.getProducts().stream()
//                .map(product -> product.getAmount())
//                .collect(Collectors.toList());
//        int result = 0;
//        for(int product : productsAmount) {
//            result += product;
//        }
//        return result;
//    }
}
