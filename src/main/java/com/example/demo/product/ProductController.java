package com.example.demo.product;

import com.example.demo.product.DTO.ProductUpdateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductRepository repository;
    private final ProductService service;

    public ProductController(ProductRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<Product>> getAllAvailable() {
        logger.warn("Exposing all products");
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

    @GetMapping("/{id}")
    ResponseEntity<Product> getProduct(@PathVariable int id) {
        logger.info("Exposing specified product");
        var result = repository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException());
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

    @PutMapping("/{id}")
    ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody @Valid Product source) {
        logger.info("Full updating product");
        service.updateProduct(id, source);
        return ResponseEntity.noContent().build();
    }

    // old version with 3 different methods for PATCH
//    @PatchMapping(value = "/{id}", params = "value")
//    ResponseEntity<?> changeAmount(@PathVariable int id, @RequestParam("value") String change) {
//        service.changeAmount(id, change);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/potentialRemoval/{id}")
//    public ResponseEntity<?> potentialProductRemoval(@PathVariable int id) {
//        logger.warn("Potential product removing!");
//        service.potentialProductRemoval(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/restoring/{id}")
//    public ResponseEntity<?> restoreProduct(@PathVariable int id) {
//        logger.warn("Restore product!");
//        service.restoreProduct(id);
//        return ResponseEntity.noContent().build();
//    }

    // new version
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProductProperty (
        @PathVariable int id,
        @RequestBody(required = false) ProductUpdateModel source
    ) {
        // change amount
        if(source != null) {
            var result = service.changeAmount(id, source);
            return ResponseEntity.ok(result);
        }
        else {
            service.changeActive(id);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> completeProductRemoval(@PathVariable int id) {
        logger.warn("Complete product removing!");
        repository.deleteById(id); // removes row with given id from db
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
