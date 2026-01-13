package uk.ac.ed.inf.ilp_coursework.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ed.inf.ilp_coursework.data.Position;

@Getter
@Setter
public class NextPositionRequest {
    @JsonProperty("start")
    private Position start;

    @JsonProperty("angle")
    private Double angle;

}
