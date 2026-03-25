package com.example.ems_command_center.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "users")
public record UserProfile(
    @Id String id,
    String name,
    String role,
    String email,
    String phone,
    String location,
    String joined,
    String specialization,
    List<UserStat> stats
) {
}
