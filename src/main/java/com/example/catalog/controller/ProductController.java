package com.example.catalog.controller;

import com.example.catalog.model.Product;
import com.example.catalog.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductController {

    private final ProductRepository repository;
    private final ObjectMapper objectMapper; // ใช้แปลง Map เป็น String JSON

    public ProductController(ProductRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @QueryMapping // ตรงกับ type Query { products }
    public List<Product> products() {
        return repository.findAll();
    }

    @QueryMapping // ตรงกับ type Query { productById }
    public Product productById(@Argument String id) {
        return repository.findById(id).orElse(null);
    }
    
    @QueryMapping
    public List<Product> productsByCategory(@Argument String category) {
        return repository.findByCategory(category);
    }

    @MutationMapping // ตรงกับ type Mutation
    public Product createProduct(@Argument String name, @Argument Double price, @Argument String category) {
        Product p = new Product(null, name, price, category, null);
        return repository.save(p);
    }

    // Field Resolver พิเศษสำหรับ attributes
    // เนื่องจากใน Java เป็น Map แต่ใน Schema เป็น String เราต้องแปลงข้อมูลเฉพาะ field นี้
    @SchemaMapping(typeName = "Product", field = "attributes")
    public String getAttributes(Product product) throws JsonProcessingException {
        if (product.getAttributes() == null) return "{}";
        return objectMapper.writeValueAsString(product.getAttributes());
    }
}
