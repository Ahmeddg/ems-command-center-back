package com.example.ems_command_center.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String phone;
    private String location;
    private String joined;
    private String specialization;
    private String role;          // USER, ADMIN, DRIVER, MANAGER
    private String status;        // e.g. "Active Now", "Off Duty"
    private String statusType;    // "success", "normal", "urgent"
    private String iconName;      // UI icon
    private String color;         // CSS class
    private List<UserStat> stats; // embedded stats
    private String ambulanceId;   // DRIVER-specific
    private String hospitalId;    // MANAGER-specific
    
    @Indexed(unique = true, sparse = true)
    private String keycloakId;    // Keycloak user ID (sub claim)

    // No-arg constructor
    public User() {
    }

    // All-arg constructor
    public User(String id, String name, String email, String phone, String location, String joined,
                String specialization, String role, String status, String statusType, String iconName,
                String color, List<UserStat> stats, String ambulanceId, String hospitalId, String keycloakId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.joined = joined;
        this.specialization = specialization;
        this.role = role;
        this.status = status;
        this.statusType = statusType;
        this.iconName = iconName;
        this.color = color;
        this.stats = stats;
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
        this.keycloakId = keycloakId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<UserStat> getStats() {
        return stats;
    }

    public void setStats(List<UserStat> stats) {
        this.stats = stats;
    }

    public String getAmbulanceId() {
        return ambulanceId;
    }

    public void setAmbulanceId(String ambulanceId) {
        this.ambulanceId = ambulanceId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }
}
