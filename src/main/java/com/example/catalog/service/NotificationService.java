package com.example.catalog.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // KafkaListener จะคอยฟัง Topic "product-events" ตลอดเวลา
    @KafkaListener(topics = "product-events", groupId = "notification-group")
    public void listen(String message) {
        System.out.println("==========================================");
        System.out.println("📧 NOTIFICATION SERVICE RECEIVED EVENT:");
        System.out.println("New Product Added: " + message);
        System.out.println(">> Sending Email to subscribers... (Simulated)");
        System.out.println("==========================================");
    }
}
