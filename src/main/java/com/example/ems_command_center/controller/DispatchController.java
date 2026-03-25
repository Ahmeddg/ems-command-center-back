package com.example.ems_command_center.controller;

import com.example.ems_command_center.model.AmbulanceRouteResponse;
import com.example.ems_command_center.model.DispatchAssignmentResponse;
import com.example.ems_command_center.model.DispatchRequest;
import com.example.ems_command_center.model.Vehicle;
import com.example.ems_command_center.service.DispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Ambulance routing and dispatch operations")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/ambulances/available")
    @Operation(summary = "List all available ambulances")
    public ResponseEntity<List<Vehicle>> getAvailableAmbulances() {
        return ResponseEntity.ok(dispatchService.getAvailableAmbulances());
    }

    @GetMapping("/routes")
    @Operation(summary = "Preview the suggested route from an ambulance to an incident")
    public ResponseEntity<AmbulanceRouteResponse> getRoute(
        @RequestParam String vehicleId,
        @RequestParam String incidentId
    ) {
        return ResponseEntity.ok(dispatchService.getRoute(vehicleId, incidentId));
    }

    @PostMapping("/assignments")
    @Operation(summary = "Dispatch an ambulance to an incident")
    public ResponseEntity<DispatchAssignmentResponse> dispatchAmbulance(@RequestBody DispatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispatchService.dispatchAmbulance(request));
    }
}
