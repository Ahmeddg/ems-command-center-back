package com.example.ems_command_center.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "vehicles")
public record Vehicle(
    @Id String id,
    String name,
    String status, // "available" | "busy" | "maintenance" | "out-of-service"
    String type,   // "ambulance" | "supervisor" | "fire-truck"
    Coordinates location,
    List<String> crew,
    String lastUpdate,
    List<Equipment> equipment
) {
}
