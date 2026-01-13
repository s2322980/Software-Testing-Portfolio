package uk.ac.ed.inf.ilp_coursework.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeliveryPathRequest {
    private LngLat start;
    private List<LngLat> deliveryPoints;
    public static Region getCentralArea() {
        Region centralArea = new Region();
        centralArea.setName("central");
        centralArea.setVertices(new ArrayList<>(List.of(
                new Position(-3.192473, 55.946233),
                new Position(-3.192473, 55.942617),
                new Position(-3.184319, 55.942617),
                new Position(-3.184319, 55.946233),
                new Position(-3.192473, 55.946233)
        )));
        return centralArea;
    }

}
