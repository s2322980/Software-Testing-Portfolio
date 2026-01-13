package uk.ac.ed.inf.ilp_coursework.data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderValidationResult {

    // Getters and Setters
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    // Constructors
    public OrderValidationResult() {}
    private OrderValidationCode failedAt;

    public void setFailedAt(OrderValidationCode orderValidationCode) {
        this.failedAt = orderValidationCode;
    }

    public OrderValidationCode getFailedAt() {
        return failedAt;
    }

    public OrderValidationResult(OrderStatus orderStatus, OrderValidationCode orderValidationCode) {
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
    }


}

