package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import uk.ac.ed.inf.ilp_coursework.data.Position;

import java.util.ArrayList;

@Getter
@Setter

public class RegionRequest {
    @JsonProperty("position")
    private Position position;

    @JsonProperty("region")
    private Region region;

    @JsonProperty("vertices")
    private ArrayList<Position> vertices;

    @JsonProperty("lng")
    private Double lng;

    @JsonProperty("lat")
    private Double lat;
}
