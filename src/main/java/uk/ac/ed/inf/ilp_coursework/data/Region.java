package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter

public class Region {
    @JsonProperty("name")
    private String name;

    @JsonProperty("vertices")
    private ArrayList<Position> vertices;

}

