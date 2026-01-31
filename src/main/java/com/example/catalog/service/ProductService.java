package com.example.catalog.service;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
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

      // ✅ CLEAN CODE VERSION
    // 1. ใช้ Logger แทน System.out
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    // 2. กำหนด Constant แทน Magic Number
    private static final double EXPENSIVE_THRESHOLD = 1000.0;
    private static final double MID_RANGE_CEILING = 5000.0;

    public void processProductData(List<Product> products, String type) {
        // 3. Early Return / Guard Clause (ลด Nesting)
        if (products == null || products.isEmpty()) {
            return;
        }

        // 4. ใช้ Stream API (อ่านง่ายกว่า for-loop ซับซ้อน)
        products.stream()
                .filter(product -> product.getPrice() > EXPENSIVE_THRESHOLD)
                .forEach(product -> handleProductByType(product, type));
    }

    // 5. Extract Method (แยก Logic ย่อยออกมาเพื่อให้ Main Logic อ่านง่าย)
    private void handleProductByType(Product product, String type) {
        if ("A".equals(type)) {
            log.info("Expensive Type A: {}", product.getName());
        } else if ("B".equals(type) && product.getPrice() < MID_RANGE_CEILING) {
            log.info("Mid Range Type B: {}", product.getName());
        }
    }

}