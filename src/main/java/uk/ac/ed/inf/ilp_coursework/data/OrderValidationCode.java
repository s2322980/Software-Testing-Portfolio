package uk.ac.ed.inf.ilp_coursework.data;

public enum OrderValidationCode {
    NO_ERROR,                 // Order is valid
    EMPTY_ORDER,              // Order has no pizzas
    CARD_NUMBER_INVALID,      // Invalid credit card number
    EXPIRY_DATE_INVALID,      // Credit card expiry date is invalid
    CVV_INVALID,              // CVV is invalid
    TOTAL_INCORRECT,          // Order total does not match the sum of pizzas
    MAX_PIZZA_COUNT_EXCEEDED, // Too many pizzas in the order
    PIZZA_NOT_DEFINED,        // Pizza is not defined in the menu
    PRICE_FOR_PIZZA_INVALID,  // Price for a pizza is invalid
    PIZZA_FROM_MULTIPLE_RESTAURANTS, // Pizzas from multiple restaurants
    GENERIC_ERROR, RESTAURANT_CLOSED,         // Restaurant is closed on order date
    TOTAL_CHECK
}
