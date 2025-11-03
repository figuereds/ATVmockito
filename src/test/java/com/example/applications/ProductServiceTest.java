package com.example.services;

import com.example.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;
    private Product product;
    private Path sourcePath;
    private Path destinationPath;

    @BeforeEach
    void setup() {
        productService = new ProductService();
        product = new Product(1, "Mouse logitech", 99.90f, "X:\\mouse.jpg");
        sourcePath = Paths.get(product.getImage());
        destinationPath = Paths.get("X:\\1.jpg");
    }

    @Test
    void deveSalvarImagemCorretamente() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(sourcePath)).thenReturn(true);
            mockedFiles.when(() -> Files.copy(sourcePath, destinationPath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING)).thenReturn(destinationPath);

            boolean result = productService.save(product);

            assertTrue(result);
            assertEquals("X:\\1.jpg", product.getImage());
        }
    }

    @Test
    void deveRemoverImagemCorretamente() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {


            mockedFiles.when(() -> Files.deleteIfExists(Paths.get("X:\\1.jpg")))
                    .thenReturn(true);


            ProductService spyService = Mockito.spy(productService);
            Mockito.doReturn("X:\\1.jpg").when(spyService).getImagePathById(1);

            assertDoesNotThrow(() -> spyService.remove(1));
        }
    }

    @Test
    void deveAtualizarImagemCorretamente() {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            ProductService spyService = Mockito.spy(productService);

            
            Mockito.doNothing().when(spyService).remove(1);
            Mockito.doReturn(true).when(spyService).save(product);

            spyService.update(product);

            Mockito.verify(spyService).remove(product.getId());
            Mockito.verify(spyService).save(product);
        }
    }
}
