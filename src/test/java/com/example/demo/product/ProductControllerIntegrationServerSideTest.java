package com.example.demo.product;

import com.example.demo.TestConfiguration;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class ProductControllerIntegrationServerSideTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestConfiguration.TestProductRepository repository;

    @Test
    void httpGet_returnsAllAvailableProducts() throws Exception {
        // given
        var unavailableProduct = new Product();
        unavailableProduct.setName("foo");
        repository.save(unavailableProduct);
        // and
        var availableProduct = new Product();
        availableProduct.setAmount(1);
        availableProduct.setName("bar");
        repository.save(availableProduct);
        var availableProduct1 = new Product();
        availableProduct1.setAmount(2);
        repository.save(availableProduct1);

        // when + then
        mockMvc.perform(get("/products"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].name").value("bar"))
                .andExpect(jsonPath("$..amount", Matchers.containsInAnyOrder(2, 1)));
    }

    @Test
    void httpPost_createsProduct_returnsCreatedProduct() throws Exception {
        // given
        String jsonString = new JSONObject()
                .put("name", "foo")
                .put("amount", 5)
                .toString();

        // when + then
        mockMvc.perform(post("/products")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("foo"));
    }

    @Test
    void httpPut_updatesProduct() throws Exception {
        // given
        Product existingProduct = new Product();
        existingProduct.setAmount(2);
        existingProduct.setName("foo");
        repository.save(existingProduct);
        // and
        String jsonString = new JSONObject()
                .put("name", "bar")
                .put("amount", 20)
                .toString();
        // and
        int lastProductId = repository.getSize();

        // when + then
        mockMvc.perform(put("/products/" + lastProductId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void httpPatch_increaseProductAmount() throws Exception {
        // given
        Product existingProduct = new Product();
        existingProduct.setName("foo");
        existingProduct.setAmount(10);
        var id = repository.save(existingProduct).getId();

        // when + then
        mockMvc.perform(patch("/products/" + id + "?value=plus"))
                .andExpect(status().is2xxSuccessful());
    }
}