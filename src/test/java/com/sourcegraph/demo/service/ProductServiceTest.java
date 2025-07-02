package com.sourcegraph.demo.service;

import com.sourcegraph.demo.entity.Product;
import com.sourcegraph.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    public void getAllProducts_ShouldReturnAllProductsFromRepository() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setPrice(10.99);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setPrice(25.99);

        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.getAllProducts();

        assertEquals(expectedProducts, actualProducts);
        assertEquals(2, actualProducts.size());
        assertEquals("Test Product 1", actualProducts.get(0).getName());
        assertEquals("Test Product 2", actualProducts.get(1).getName());
    }

    @Test
    public void getAllProducts_ShouldReturnEmptyList_WhenNoProductsInRepository() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<Product> actualProducts = productService.getAllProducts();

        assertEquals(0, actualProducts.size());
    }
}
