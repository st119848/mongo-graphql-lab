package com.example.catalog.config;

import com.example.catalog.model.Product;
import com.example.catalog.model.User;
import com.example.catalog.repository.ProductRepository;
import com.example.catalog.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository repository;
    private final UserRepository userRepository;

    public DataSeeder(ProductRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll(); // ล้างข้อมูลเก่าก่อน

        Product laptop = new Product(null, "Gaming Laptop", 45000.0, "Electronics",
                Map.of("cpu", "i9", "ram", "32GB", "brand", "Alienware"));

        Product tshirt = new Product(null, "Cool T-Shirt", 500.0, "Clothing",
                Map.of("size", "L", "color", "Black", "fabric", "Cotton"));

        Product table = new Product(null, "Office Table", 2500.0, "Furniture",
                Map.of("material", "Wood", "width", "120cm"));

        repository.saveAll(List.of(laptop, tshirt, table));
        System.out.println(">>> Mock Data Inserted to MongoDB!");

        // Read-back: fetch and display inserted data
        List<Product> all = repository.findAll();
        System.out.println(">>> Total products: " + all.size());
        all.forEach(p -> System.out.println(
            " - " + p.getId() + " | " + p.getName() + " | " + p.getCategory() + " | price=" + p.getPrice()
        ));

        // Examples: filter queries
        List<Product> electronics = repository.findByCategory("Electronics");
        System.out.println(">>> Electronics count: " + electronics.size());

        List<Product> brandAlienware = repository.findByBrandInAttributes("Alienware");
        System.out.println(">>> Brand 'Alienware' count: " + brandAlienware.size());

        userRepository.deleteAll();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // สร้าง User: admin / password123
        User admin = new User("admin", encoder.encode("password123"), "ADMIN");
        userRepository.save(admin);
        System.out.println(">>> Mock User Created: admin / password123");

    }
}


