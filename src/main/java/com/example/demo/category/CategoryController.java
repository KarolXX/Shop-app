package com.example.demo.category;

import com.example.demo.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private CategoryRepository repository;
    private CategoryService service;

    public CategoryController(CategoryRepository repository, CategoryService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<Category>> getAllCategories() {
        logger.warn("Exposing all the categories!");
        var result = service.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/products")
    ResponseEntity<List<Product>> getProducts(@PathVariable int id) {
        logger.warn("Exposing all the category products");
        var result = service.getProducts(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<?> createCategory(@RequestBody Category category) {
        logger.info("New category has been created");
        var result = service.createCategory(category);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(category);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCategory(@PathVariable int id) {
        logger.warn("Removing category");
        service.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> replaceCategoryProducts(@PathVariable int id, @RequestBody Set<Product> source) {
        logger.warn("Products swap");
        service.replaceCategoryProducts(id, source);
        return ResponseEntity.ok().build();
    }
}
