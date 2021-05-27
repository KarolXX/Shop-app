package com.example.demo.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Product updateProduct(int id, Product source) {
        var target = repository.findProductByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("No product with given id"));
        logger.info("Full updating product");
        return target.fullUpdate(source);
    }

    @Transactional
    public void changeAmount(int id, String change) {
        var target = repository.findProductByIdAndActiveTrue(id).orElseThrow(() -> new IllegalArgumentException("Product with given id not found"));
        if(change.equals("plus")) {
            logger.info("Increasing the amount of product");
            target.setAmount(target.getAmount() + 1);
        }
        else if(target.getAmount() > 0) {
            logger.info("reducing the amount of product");
            target.setAmount(target.getAmount() - 1);
        }
    }

//    public static void setTimeout(Runnable runnable, int delay) {
//        new Thread(() -> {
//            try{
//                Thread.sleep(delay);
//                runnable.run();
//            } catch (Exception e) {
//                logger.error("Error during deletion the product");
//            }
//        }).start();
//    }

    @Transactional
    public void deleteProduct(int id) {
        var target = repository.findProductByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("No product with gicen id"));
        target.setActive(false);
        // complete removal after 150 seconds
        new Thread(() -> {
            try {
                Thread.sleep(150000);
                repository.deleteById(id);
            } catch (Exception e) {
                logger.error("Error removing the product");
            }
        }).start();
    }
}
