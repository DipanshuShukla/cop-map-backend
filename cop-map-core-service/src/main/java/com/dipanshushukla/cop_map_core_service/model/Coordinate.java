package com.dipanshushukla.cop_map_core_service.model;

import java.io.Serializable;

// A simple record to represent Map coordinates
public record Coordinate(Double latitude, Double longitude) implements Serializable {
}