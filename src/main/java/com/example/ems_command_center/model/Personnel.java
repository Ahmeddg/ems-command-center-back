package com.example.ems_command_center.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "personnel")
public record Personnel(
    @Id String id,
    String name,
    String email,
    String role,
    String status,
    String statusType, // "success" | "normal" | "urgent"
    String iconName,
    String color
) {
}
