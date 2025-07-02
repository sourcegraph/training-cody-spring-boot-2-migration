package com.sourcegraph.demo.repository;

import com.sourcegraph.demo.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void findAll_ShouldReturnAllProducts() {
        int initialCount = productRepository.findAll().size();
        
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setPrice(10.99);
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(25.99);
        entityManager.persistAndFlush(product2);

        List<Product> products = productRepository.findAll();

        assertEquals(initialCount + 2, products.size());
        assertNotNull(products.get(0).getId());
    }

    @Test
    public void findAll_ShouldReturnPreloadedProducts() {
        List<Product> products = productRepository.findAll();
        assertEquals(10, products.size());
    }
}
