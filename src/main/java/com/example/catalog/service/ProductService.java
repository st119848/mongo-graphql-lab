package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate; // ตัวส่งข้อความ
    private final ObjectMapper objectMapper; // ตัวแปลง Object เป็น JSON String

    public ProductService(ProductRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        return repository.findById(id).orElse(null);
    }

    @CacheEvict(value = "products", allEntries = true) // ล้าง Cache เมื่อมีของใหม่
    public Product createProduct(Product product) {
        Product savedProduct = repository.save(product);

        // --- Kafka Logic ---
        try {
            // แปลง Object เป็น JSON String
            String productJson = objectMapper.writeValueAsString(savedProduct);

            // ส่งเข้า Topic ชื่อ "product-events"
            kafkaTemplate.send("product-events", productJson);

            System.out.println(">>> Message sent to Kafka: " + productJson);
        } catch (Exception e) {
            e.printStackTrace(); // ใน Production ควร Handle ดีกว่านี้
        }
        // -------------------

        return savedProduct;
    }

    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }

   
}