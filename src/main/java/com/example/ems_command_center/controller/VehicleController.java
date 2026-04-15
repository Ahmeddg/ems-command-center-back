package com.example.ems_command_center.controller;

import com.example.ems_command_center.model.Vehicle;
import com.example.ems_command_center.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Fleet and vehicle management")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Fetch all vehicles (ambulances, supervisors, etc.)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DRIVER')")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PostMapping
    @Operation(summary = "Register a new vehicle")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(vehicle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle status or location")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or (hasRole('DRIVER') and @accessControlService.isAssignedAmbulance(authentication, #id))")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable String id, @RequestBody Vehicle updates) {
        return vehicleService.updateVehicle(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Decommission a vehicle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        return vehicleService.deleteVehicle(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
