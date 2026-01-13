package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter

public class GeoJsonPath {
    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private ArrayList<ArrayList<Double>> coordinates;
}
