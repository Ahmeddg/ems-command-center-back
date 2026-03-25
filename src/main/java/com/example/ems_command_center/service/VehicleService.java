package com.example.ems_command_center.service;

import com.example.ems_command_center.model.Vehicle;
import com.example.ems_command_center.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> updateVehicle(String id, Vehicle updates) {
        return vehicleRepository.findById(id).map(existing -> {
            Vehicle updated = new Vehicle(
                existing.id(),
                existing.name(),
                updates.status() != null ? updates.status() : existing.status(),
                existing.type(),
                updates.location() != null ? updates.location() : existing.location(),
                updates.crew() != null ? updates.crew() : existing.crew(),
                updates.lastUpdate() != null ? updates.lastUpdate() : existing.lastUpdate(),
                updates.equipment() != null ? updates.equipment() : existing.equipment()
            );
            return vehicleRepository.save(updated);
        });
    }

    public boolean deleteVehicle(String id) {
        if (vehicleRepository.existsById(id)) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long countByStatus(String status) {
        return vehicleRepository.countByStatus(status);
    }

    public long countAll() {
        return vehicleRepository.count();
    }
}
