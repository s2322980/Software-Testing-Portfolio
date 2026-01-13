package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LngLat {
    @JsonProperty("lng")
    private Double lng;

    @JsonProperty("lat")
    private Double lat;

    public LngLat(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public boolean equals(LngLat other) {
        if (this == other) return true; // Check if both references point to the same object
        if (other == null) return false; // Check for null
        return Double.compare(this.lng, other.lng) == 0 && Double.compare(this.lat, other.lat) == 0;
    }
    public double distanceTo(LngLat other) {
        if (other == null) {
            throw new IllegalArgumentException("The other LngLat point cannot be null");
        }
        // Use the Euclidean distance formula
        double dLat = this.lat - other.lat;
        double dLng = this.lng - other.lng;
        return Math.sqrt(dLat * dLat + dLng * dLng);
    }
    public Position toPosition() {
        return new Position(this.lat, this.lng);
    }


    public boolean isCloseTo(LngLat other) {
        double distanceThreshold = 0.00015; // Define a small distance threshold
        double distance = Math.sqrt(Math.pow(this.lat - other.lat, 2) + Math.pow(this.lng - other.lng, 2));
        return distance <= distanceThreshold;
    }

}
