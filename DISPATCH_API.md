# Dispatch And Routing API

This backend now exposes dispatch-specific endpoints for assigning ambulances to incidents and previewing response routes.

## Base endpoints

### `GET /api/dispatch/ambulances/available`
Returns ambulances whose `type` is `ambulance` and whose `status` is `available`.

Example response:

```json
[
  {
    "id": "UNIT-912",
    "name": "AMB-202",
    "status": "available",
    "type": "ambulance",
    "location": { "lat": 36.8165, "lng": 10.1715 },
    "crew": ["A. Mansour"],
    "lastUpdate": "5m ago",
    "equipment": []
  }
]
```

### `GET /api/dispatch/routes?vehicleId={vehicleId}&incidentId={incidentId}`
Builds a suggested route from the selected ambulance to the selected incident.

Example response:

```json
{
  "vehicleId": "UNIT-912",
  "vehicleName": "AMB-202",
  "incidentId": "67e2f6c0c3f5b6222e9d1012",
  "incidentTitle": "Cardiac Distress",
  "origin": { "lat": 36.8165, "lng": 10.1715 },
  "destination": { "lat": 36.8165, "lng": 10.1915 },
  "path": [
    { "lat": 36.8165, "lng": 10.1715 },
    { "lat": 36.8165, "lng": 10.1781 },
    { "lat": 36.8165, "lng": 10.1847 },
    { "lat": 36.8165, "lng": 10.1915 }
  ],
  "distanceKm": 2.2,
  "estimatedMinutes": 4,
  "trafficLevel": "light",
  "turnByTurn": [
    "Depart from current ambulance position",
    "Proceed toward Rue de Marseille, Tunis",
    "Maintain EMS priority response protocol",
    "Arrive on scene and update incident status"
  ]
}
```

### `POST /api/dispatch/assignments`
Dispatches an ambulance to an incident. This endpoint:

- validates the vehicle and incident
- ensures the vehicle is an ambulance
- updates the ambulance status to `busy`
- updates the incident status to `Dispatched`
- appends dispatcher and dispatch notes to incident tags
- returns the dispatch summary plus the generated route

Example request:

```json
{
  "incidentId": "67e2f6c0c3f5b6222e9d1012",
  "vehicleId": "UNIT-912",
  "dispatcher": "Marcus Thorne",
  "notes": "Use priority corridor and notify triage on approach."
}
```

Example response:

```json
{
  "incidentId": "67e2f6c0c3f5b6222e9d1012",
  "incidentTitle": "Cardiac Distress",
  "vehicleId": "UNIT-912",
  "vehicleName": "AMB-202",
  "dispatcher": "Marcus Thorne",
  "notes": "Use priority corridor and notify triage on approach.",
  "vehicleStatus": "busy",
  "incidentStatus": "Dispatched",
  "dispatchedAt": "2026-03-25 20:58",
  "incidentTags": [
    "Patient Onboard",
    "AMB-202 Dispatched",
    "Dispatcher: Marcus Thorne",
    "Notes: Use priority corridor and notify triage on approach."
  ],
  "route": {}
}
```

## Notes

- Route geometry is generated from ambulance and incident coordinates already stored in MongoDB.
- If you want turn-by-turn navigation from a real map provider later, keep the response contract and replace the route generator in the service layer.
- The dispatch endpoints are also visible in Swagger at `/swagger-ui.html`.
