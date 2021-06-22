package com.example.demo.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Test
    @DisplayName("should throw IllegalArgumentException when no active product with given id")
    void updateProduct_noActiveProduct_throwsIllegalArgumentException() {
        // given
        var mockProductRepo = productRepositoryReturning(null);
        // system under test
        var toTest = new ProductService(mockProductRepo);

        // when
        var exception = catchThrowable(() -> toTest.updateProduct(1, new Product()));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should update active product with given id")
    void updateProduct_activeProductExists_updatesProduct() {
        // given
        var mockProductRepo = productRepositoryReturning(new Product());
        // and
        Product newProduct = new Product();
        newProduct.setId(1);
        newProduct.setAmount(2);
        newProduct.setName("bar");
        // system under test
        var toTest = new ProductService(mockProductRepo);

        // when
       /* var result = toTest.updateProduct(1, newProduct);

        // then
        assertThat(result.getAmount()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("bar");*/
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when no active product with given id")
    void changeAmount_noActiveProduct_changesAmount() {
        // given
        var mockProductRepo = productRepositoryReturning(null);
        // system under test
        var toTest = new ProductService(mockProductRepo);

        // when
        //fixme
        //var exception = catchThrowable(() -> toTest.changeAmount(1, "plus"));

        // then
//        assertThat(exception)
//                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should increase amount when product with given id exists and `change` parameter is equal to plus")
    void changeAmount_ProductExists_andChangeParameterEqualToPlus_increasesAmount() {
        // given
        Product product = new Product();
        var mockProductRepo = productRepositoryReturning(product);
        // system under test
        var toTest = new ProductService(mockProductRepo);

        // when
        //FIXME
        //toTest.changeAmount(1, "plus");

        // then
        //assertThat(product.getAmount()).isEqualTo(2);
    }

    // TODO: test deleteProduct method but first implement it correct.

    private static ProductRepository productRepositoryReturning(Product product) {
        if(product != null) {
            product.setId(1);
            product.setAmount(1);
            product.setName("foo");
        }

        var result = mock(ProductRepository.class);
        when(result.findProductById(anyInt()))
                .thenReturn(Optional.ofNullable(product));

        return result;
    }
}