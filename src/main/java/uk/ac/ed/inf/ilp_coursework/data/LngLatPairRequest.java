package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ed.inf.ilp_coursework.data.Position;
import uk.ac.ed.inf.ilp_coursework.data.Region;


@Getter
@Setter

public class LngLatPairRequest {
    @JsonProperty("position1")
    private Position position1;

    @JsonProperty("position2")
    private Position position2;

    @JsonProperty("region")
    private Region region;

}
