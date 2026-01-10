package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // จุดสำคัญ: Cacheable
    // value = ชื่อ Cache bucket
    // key = คีย์ที่จะใช้เก็บ (ในที่นี้คือ id ของสินค้า)
    // unless = เงื่อนไขที่จะไม่ Cache (เช่น ถ้าหาไม่เจอก็ไม่ต้องเก็บ null)
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Product getProductById(String id) {
        System.out.println(">>> Fetching from MongoDB for ID: " + id); // Log เพื่อพิสูจน์
        
        // จำลองความช้า (Simulate Latency) เพื่อให้เห็นภาพชัด
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        return repository.findById(id).orElse(null);
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }
    public Product saveProduct(Product product) {
        return repository.save(product);
    }
}