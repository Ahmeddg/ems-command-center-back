package com.example.ems_command_center.service;

import com.example.ems_command_center.model.AmbulanceRouteResponse;
import com.example.ems_command_center.model.Coordinates;
import com.example.ems_command_center.model.DispatchAssignmentResponse;
import com.example.ems_command_center.model.DispatchRequest;
import com.example.ems_command_center.model.Incident;
import com.example.ems_command_center.model.Vehicle;
import com.example.ems_command_center.repository.IncidentRepository;
import com.example.ems_command_center.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DispatchService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VehicleRepository vehicleRepository;
    private final IncidentRepository incidentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public DispatchService(
        VehicleRepository vehicleRepository,
        IncidentRepository incidentRepository,
        SimpMessagingTemplate messagingTemplate
    ) {
        this.vehicleRepository = vehicleRepository;
        this.incidentRepository = incidentRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<Vehicle> getAvailableAmbulances() {
        return vehicleRepository.findByType("ambulance").stream()
            .filter(vehicle -> "available".equalsIgnoreCase(vehicle.status()))
            .toList();
    }

    public AmbulanceRouteResponse getRoute(String vehicleId, String incidentId) {
        Vehicle vehicle = getVehicle(vehicleId);
        Incident incident = getIncident(incidentId);
        validateDispatchableVehicle(vehicle);
        return buildRoute(vehicle, incident);
    }

    public DispatchAssignmentResponse dispatchAmbulance(DispatchRequest request) {
        if (request == null || request.incidentId() == null || request.vehicleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "incidentId and vehicleId are required");
        }

        Vehicle vehicle = getVehicle(request.vehicleId());
        Incident incident = getIncident(request.incidentId());
        validateDispatchableVehicle(vehicle);

        AmbulanceRouteResponse route = buildRoute(vehicle, incident);
        String dispatchedAt = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String dispatcher = request.dispatcher() == null || request.dispatcher().isBlank()
            ? "Dispatch Center"
            : request.dispatcher();

        Vehicle dispatchedVehicle = new Vehicle(
            vehicle.id(),
            vehicle.name(),
            "busy",
            vehicle.type(),
            vehicle.location(),
            vehicle.crew(),
            dispatchedAt,
            vehicle.equipment()
        );

        List<String> updatedTags = new ArrayList<>(incident.tags() == null ? List.of() : incident.tags());
        updatedTags.removeIf(tag -> tag != null && tag.startsWith(vehicle.name()));
        updatedTags.add(vehicle.name() + " Dispatched");
        updatedTags.add("Dispatcher: " + dispatcher);
        if (request.notes() != null && !request.notes().isBlank()) {
            updatedTags.add("Notes: " + request.notes());
        }

        Incident updatedIncident = new Incident(
            incident.id(),
            incident.title(),
            incident.location(),
            incident.coordinates(),
            incident.time(),
            incident.type(),
            updatedTags,
            "Dispatched",
            incident.priority()
        );

        vehicleRepository.save(dispatchedVehicle);
        incidentRepository.save(updatedIncident);

        DispatchAssignmentResponse response = new DispatchAssignmentResponse(
            updatedIncident.id(),
            updatedIncident.title(),
            dispatchedVehicle.id(),
            dispatchedVehicle.name(),
            dispatcher,
            request.notes(),
            dispatchedVehicle.status(),
            updatedIncident.status(),
            dispatchedAt,
            updatedIncident.tags(),
            route
        );

        publishDispatchNotifications(response);
        return response;
    }

    private Vehicle getVehicle(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }

    private Incident getIncident(String incidentId) {
        return incidentRepository.findById(incidentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));
    }

    private void validateDispatchableVehicle(Vehicle vehicle) {
        if (!"ambulance".equalsIgnoreCase(vehicle.type())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ambulance vehicles can be dispatched");
        }
    }

    private AmbulanceRouteResponse buildRoute(Vehicle vehicle, Incident incident) {
        Coordinates origin = vehicle.location();
        Coordinates destination = incident.coordinates();
        if (origin == null || destination == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle and incident must both have coordinates");
        }

        double distanceKm = haversineKm(origin, destination);
        int estimatedMinutes = Math.max(3, (int) Math.ceil(distanceKm / averageSpeedFor(incident.type()) * 60));
        List<Coordinates> path = List.of(
            origin,
            interpolate(origin, destination, 0.33),
            interpolate(origin, destination, 0.66),
            destination
        );

        return new AmbulanceRouteResponse(
            vehicle.id(),
            vehicle.name(),
            incident.id(),
            incident.title(),
            origin,
            destination,
            path,
            round(distanceKm),
            estimatedMinutes,
            trafficFor(distanceKm, incident.type()),
            List.of(
                "Depart from current ambulance position",
                "Proceed toward " + incident.location(),
                "Maintain EMS priority response protocol",
                "Arrive on scene and update incident status"
            )
        );
    }

    private double averageSpeedFor(String incidentType) {
        return "urgent".equalsIgnoreCase(incidentType) ? 52.0 : 38.0;
    }

    private String trafficFor(double distanceKm, String incidentType) {
        if ("urgent".equalsIgnoreCase(incidentType)) {
            return distanceKm > 6 ? "moderate" : "clear";
        }
        return distanceKm > 5 ? "moderate" : "light";
    }

    private Coordinates interpolate(Coordinates origin, Coordinates destination, double ratio) {
        double lat = origin.lat() + (destination.lat() - origin.lat()) * ratio;
        double lng = origin.lng() + (destination.lng() - origin.lng()) * ratio;
        return new Coordinates(round(lat), round(lng));
    }

    private double haversineKm(Coordinates origin, Coordinates destination) {
        double earthRadiusKm = 6371.0;
        double latDistance = Math.toRadians(destination.lat() - origin.lat());
        double lonDistance = Math.toRadians(destination.lng() - origin.lng());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(origin.lat())) * Math.cos(Math.toRadians(destination.lat()))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void publishDispatchNotifications(DispatchAssignmentResponse response) {
        messagingTemplate.convertAndSend("/topic/drivers/dispatches", response);
        messagingTemplate.convertAndSend("/topic/drivers/" + response.vehicleId() + "/dispatches", response);
        messagingTemplate.convertAndSend("/topic/hospital-manager/dispatches", response);
    }
}
