package com.example.demo.category;

import com.example.demo.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceTest {
    @Test
    @DisplayName("should calculate the sum of all products")
    void computeTotalQuantityProducts() {
        // given
        Category category = getMockCategory(Set.of(1, 2));

        // when
        var result = CategoryService.computeTotalQuantityProducts(category);

        // then
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("should create category and set `category` to products that are created along with category")
    void createCategory() {
        // given
        Category categoryToSave = getMockCategory(Set.of(1, 2));
        inMemoryCategoryRepository inMemoryCategoryRepo = getInMemoryCategoryRepository(null);
        // and
        int sizeBefore = inMemoryCategoryRepo.getSize();
        // system under test
        var toTest = new CategoryService(inMemoryCategoryRepo, null);

        // when
        var result = toTest.createCategory(categoryToSave);

        // then
        assertThat(result.getTotalQuantity()).isEqualTo(3);
        assertThat(sizeBefore + 1).isEqualTo(inMemoryCategoryRepo.getSize());
        // FIXME: my `createCategory` method gets the products,
        //  and this method is responsible for setting each of them a category
        //  so i need to test it here as well
        //  but I don't know how to do it...

    }

    @Test
    @DisplayName("should throw IllegalArgumentException when no category with given id")
    void deleteCategoryById_noCategory_throwsIllegalArgumentException() {
        // given
        var mockCategoryRepo = mock(CategoryRepository.class);
        when(mockCategoryRepo.findById(anyInt()))
                .thenReturn(Optional.empty());
        // system under test
        var toTest = new CategoryService(mockCategoryRepo, null);

        // when
        var exception = catchThrowable(() -> toTest.deleteCategoryById(1));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No such category");
    }

    // FIXME: There is a problem with breaking the association
    //  namely, here the `categoryToDelete` is not even associated to the products at the start,
    //  because the method `getMockCategory` does not provide this
    @Test
    @DisplayName("should delete category and break the associations between this category and its products")
    void deleteCategoryById_categoryExists_deleteCategoryAndBreakAssociationWithItsProducts() throws NoSuchFieldException, IllegalAccessException {
        // given
        Category categoryToDelete = getMockCategory(Set.of(1, 2));
        // and
        inMemoryCategoryRepository inMemoryCategoryRepo = getInMemoryCategoryRepository(categoryToDelete);
        int size = inMemoryCategoryRepo.getSize();
        // system under test
        var toTest = new CategoryService(inMemoryCategoryRepo, null);

        // when
        toTest.deleteCategoryById(1);

        // then
        assertThat(size - 1).isEqualTo(inMemoryCategoryRepo.getSize());

        // FIXME: I want to check that each product's category is null,
        //  but I don't have a public getter for the category field.
        //  Is my solution okay?
        categoryToDelete.getProducts().forEach(product -> {
            Field field = null;
            try {
                field = product.getClass().getDeclaredField("category");
                field.setAccessible(true);
                var category = field.get(Integer.class);
                assertThat(category).isEqualTo(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException();
            };
        });
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when no category with given id")
    void replaceCategoryProducts_noCategory_throwIllegalArgumentException() {
        // given
        var mockCategoryRepo = mock(CategoryRepository.class);
        when(mockCategoryRepo.findById(anyInt())).thenReturn(Optional.empty());
        // system under test
        var toTest = new CategoryService(mockCategoryRepo, null);

        // when
        var exception = catchThrowable(() -> toTest.replaceCategoryProducts(1, null));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No such category");
    }

    @Test
    @DisplayName("should replace category products")
    void replaceCategoryProducts_categoryExists_replacesCategoryProducts() {
        // given


        // when

        // then
    }

    private static Category getMockCategory(Set<Integer> amounts) {
        Set<Product> products = amounts.stream()
                .map(amount -> {
                    var product = mock(Product.class);
                    when(product.getAmount()).thenReturn(amount);
                    return product;
                }).collect(Collectors.toSet());

        Category result = mock(Category.class);
        when(result.getProducts()).thenReturn(products);

        // FIXME: I need the `totalQuantity` property to test the `createCategory` method.
        //  But the code below is like a duplicate of the production code.
        //  I don't think that's the best way ...
        int totalQuantity = 0;
        for(Integer amount : amounts) {
            totalQuantity += amount;
        }
        when(result.getTotalQuantity()).thenReturn(totalQuantity);

        return result;
    }

    private static inMemoryCategoryRepository getInMemoryCategoryRepository(Category sampleCategory) {
        if(sampleCategory == null)
            return new inMemoryCategoryRepository();
        else
            return new inMemoryCategoryRepository(sampleCategory);
    }

    private static class inMemoryCategoryRepository implements CategoryRepository {
        private Map<Integer, Category> map = new HashMap<>();
        private int index = 1;

        private inMemoryCategoryRepository(Category sampleCategory) {
            // add sample category to fake DB
            map.put(1, sampleCategory);
        }

        private inMemoryCategoryRepository() {}

        public int getSize() {
            return map.size();
        }

        @Override
        public List<Category> findAll() {
            return new ArrayList<Category>(map.values());
        }

        @Override
        public Optional<Category> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public Category save(Category entity) {
            if(entity.getId() == 0) {
                try{
                    var field = Category.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);
            return map.get(entity.getId());
        }

        @Override
        public void deleteById(Integer id) {
            map.remove(id);
            index--;
        }
    };

}