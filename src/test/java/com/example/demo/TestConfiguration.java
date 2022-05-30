package com.example.demo;

import com.example.demo.product.Product;
import com.example.demo.product.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class TestConfiguration {
    @Bean
    @Primary
    @Profile("!integration")
    DataSource e2eTestDataSource() {
        var result = new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        result.setDriverClassName("org.h2.Driver");
        return result;
    }

    @Bean
    @Profile("integration")
    @Primary
    ProductRepository testProductRepo() {
        return new TestProductRepository();
    }

    public class TestProductRepository implements ProductRepository {
        private final Map<Integer, Product> products = new HashMap<>();
        private int index = 0;

        public int getSize() {
            return products.size();
        }

        @Override
        public List<Product> findProducts() {
            return new ArrayList<Product>(products.values().stream()
                    .filter(product -> product.getAmount() > 0)
                    .filter(Product::isActive)
                    .collect(Collectors.toList())
            );
        }

        // temporary implementation only to avoid errors after starting the application
        @Override
        public Page<Product> findProducts(Pageable pageable) {
            return null;
        }

        @Override
        public Optional<Product> findProductById(Integer id) {
            return products.values().stream()
                    .filter(product -> product.getId() == id)
                    .filter(Product::isActive)
                    .findAny();
        }

        @Override
        public List<Product> findProductsByAmountOrderByName(Integer amount) {
            return null;
        }

        @Override
        public Product save(Product entity) {
            if(entity.getId() == 0) {
                try {
                    var field = Product.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException();
                }
            }
            products.put(entity.getId(), entity);
            return products.get(index);
        }

        @Override
        public void deleteById(Integer id) {
            products.remove(id);
        }
    }
}
