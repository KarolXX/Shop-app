package com.example.demo.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private ProductRepository repository;
    private ProductService service;

    public ProductController(ProductRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<Product>> getAllAvailable() {
        logger.warn("Exposing all available products");
        var result = repository.findProducts();
        return ResponseEntity.ok(result);
    }

    @GetMapping(params = "available")
    ResponseEntity<List<Product>> getAllUnavailable(@RequestParam(defaultValue = "true") boolean available) {
        if(available)
            return getAllAvailable();
        logger.warn("Exposing all unavailable products");
        var result = repository.findProductsByAmountOrderByName(0);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<Product> createProduct(@RequestBody @Valid Product source) {
        if(source.getAmount() < 1)
            throw new IllegalArgumentException("You cannot add product if you don't have any");
        logger.info("New product has been created");
        var result = repository.save(source);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @PatchMapping(value = "/{id}", params = "value")
    ResponseEntity<?> changeAmount(@PathVariable int id, @RequestParam("value") String change) {
        service.changeAmount(id, change);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody @Valid Product source) {
        var result = service.updateProduct(id, source);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        logger.warn("Deleting product!");
        service.deleteProduct(id);
        //repository.deleteById(id); // removes row with given id from db
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
