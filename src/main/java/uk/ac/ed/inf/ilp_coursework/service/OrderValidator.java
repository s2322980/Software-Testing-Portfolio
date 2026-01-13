package uk.ac.ed.inf.ilp_coursework.service;

import uk.ac.ed.inf.ilp_coursework.data.Order;
import uk.ac.ed.inf.ilp_coursework.data.OrderValidationResult;

public interface OrderValidator {
    OrderValidationResult validate(Order order);
}

