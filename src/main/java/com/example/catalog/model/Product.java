package com.example.catalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products") // ระบุชื่อ Collection (คล้าย Table)
public class Product implements java.io.Serializable {
    @Id
    private String id;
    private String name;
    private Double price;
    private String category;
    
    // Key Feature: เก็บข้อมูล Specs ที่ไม่เหมือนกันในแต่ละสินค้า
    private Map<String, Object> attributes; 
}
