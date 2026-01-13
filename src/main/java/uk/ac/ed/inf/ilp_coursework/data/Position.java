package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


public class Position {
    @JsonProperty("lng")
    private Double lng;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("start")
    private Position start;

    @JsonProperty("angle")
    private Double angle;

    public Position(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

}