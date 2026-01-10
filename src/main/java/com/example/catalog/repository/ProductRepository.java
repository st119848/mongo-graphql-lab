package com.example.catalog.repository;

import com.example.catalog.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Magic Method: หาตาม Category
    List<Product> findByCategory(String category);

    // Custom Query: หาตาม Attributes ภายใน Map (Nested Query)
    // ตัวอย่าง: หาของที่ attributes.brand = ?0
    @Query("{ 'attributes.brand' : ?0 }")
    List<Product> findByBrandInAttributes(String brand);
}
