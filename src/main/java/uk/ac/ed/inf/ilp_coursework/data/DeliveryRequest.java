package uk.ac.ed.inf.ilp_coursework.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryRequest {
    private Order order;        // Uses the existing Order class
    private Restaurant restaurant; // Uses the existing Restaurant class

    @Override
    public String toString() {
        return "DeliveryRequest{" +
                "order=" + order +
                ", restaurant=" + restaurant +
                '}';
    }
}
