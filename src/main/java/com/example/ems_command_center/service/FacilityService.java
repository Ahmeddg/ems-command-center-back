package com.example.ems_command_center.service;

import com.example.ems_command_center.model.Facility;
import com.example.ems_command_center.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public List<Facility> getAllHospitals() {
        return facilityRepository.findByFacilityTypeNotNull();
    }

    public Facility createHospital(Facility hospital) {
        return facilityRepository.save(hospital);
    }

    public Optional<Facility> updateHospital(String id, Facility updates) {
        return facilityRepository.findById(id).map(existing -> {
            // Records are immutable, so we rebuild the record with updated fields
            Facility updated = new Facility(
                existing.id(),
                updates.name() != null ? updates.name() : existing.name(),
                updates.status() != null ? updates.status() : existing.status(),
                updates.beds() != null ? updates.beds() : existing.beds(),
                updates.distance() != null ? updates.distance() : existing.distance(),
                updates.type() != null ? updates.type() : existing.type(),
                updates.occupancy() != 0 ? updates.occupancy() : existing.occupancy(),
                updates.coordinates() != null ? updates.coordinates() : existing.coordinates(),
                updates.equipment() != null ? updates.equipment() : existing.equipment(),
                updates.facilityType() != null ? updates.facilityType() : existing.facilityType(),
                updates.waitTime() != null ? updates.waitTime() : existing.waitTime(),
                updates.icu() != 0 ? updates.icu() : existing.icu(),
                updates.icuTotal() != 0 ? updates.icuTotal() : existing.icuTotal(),
                updates.waitType() != null ? updates.waitType() : existing.waitType()
            );
            return facilityRepository.save(updated);
        });
    }

    public boolean deleteHospital(String id) {
        if (facilityRepository.existsById(id)) {
            facilityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public double getAverageOccupancy() {
        return facilityRepository.findAll().stream()
                .mapToInt(Facility::occupancy)
                .average()
                .orElse(0);
    }
}
