package com.example.demo.product;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductRepository repo;

    private String baseUrl = "http://localhost:";

    @Test
    void httpGet_returnsAllAvailableProducts() {
        // given
        int initialSize = repo.findActiveProducts().size();
        // and
        Product product0 = new Product();
        Product product1 = new Product();
        product0.setName("foo");
        product0.setAmount(2);
        product1.setName("bar");
        product1.setAmount(1);
        repo.save(product0);
        repo.save(product1);

        // when
        Product[] products = testRestTemplate.getForObject(baseUrl + port + "/products", Product[].class);

        //then
        assertThat(products).hasSize(initialSize + 2);
    }

    @Test
    void httpPost_createsNewProduct_returnsLocationHeader() throws JSONException {
        // given
//        String product = new JSONObject()
//                .put("amount", 5)
//                .put("name", "foo")
//                .toString(); //Test failed when it is passed to postForLocation
        Product product = new Product();
        product.setAmount(5);
        product.setName("foo");
        // and
        int productId = repo.findActiveProducts().size() + 1; // This may fail because this method does not return all objects from db
        URI expectedLocation = URI.create("/" + productId);

        // when
        var result = testRestTemplate.postForLocation(baseUrl + port + "/products", product);

        // then
        assertThat(result).isEqualTo(expectedLocation);
    }

    @Test
    void httpPost_createsNewProduct_returnsNewProduct() {
        // given
        Product product = new Product();
        product.setName("foo");
        product.setAmount(10);

        // when
        var result = testRestTemplate.postForEntity(baseUrl + port + "/products", product, Product.class);

        // then
        assertThat(result.getBody().getName()).isEqualTo("foo");
        assertThat(result.getBody().getAmount()).isEqualTo(10);
    }

}
