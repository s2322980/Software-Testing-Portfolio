package uk.ac.ed.inf.ilp_coursework.service;

import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.ilp_coursework.data.*;
import uk.ac.ed.inf.ilp_coursework.repository.RestaurantRepository;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Service
public class DefaultOrderValidator implements OrderValidator {

    private final RestaurantRepository restaurantRepository;
    private final Clock clock;
    @Setter
    private OrderValidationCode failedAt;

    public DefaultOrderValidator(RestaurantRepository restaurantRepository, Clock clock) {
        this.restaurantRepository = restaurantRepository;
        this.clock = clock;
    }

    @Override
    public OrderValidationResult validate(Order order) {
        // MOVE ALL validation logic here
        // (copy from BasicController.validateOrder)
        // replace RestaurantData.getRestaurants() with restaurantRepository.getRestaurants()
        // replace LocalDate.now() with clock.today()
        OrderValidationResult result = new OrderValidationResult();

        try {
            if (order == null
                    || order.getCreditCardInformation() == null
                    || order.getPizzasInOrder() == null) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.EMPTY_ORDER);
                return ResponseEntity.ok(result).getBody();
            }
            // Check if the order contains pizzas
            if (order.getPizzasInOrder() == null || order.getPizzasInOrder().isEmpty()) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.EMPTY_ORDER);
                return ResponseEntity.ok(result).getBody();
            }
            // Expiry Date validation
            if (order.getCreditCardInformation().getCreditCardExpiry() == null
                    || !order.getCreditCardInformation().getCreditCardExpiry().matches("\\d{2}/\\d{2}")
                    || new SimpleDateFormat("MM/yy").parse(order.getCreditCardInformation().getCreditCardExpiry()).before(new Date())){
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                return ResponseEntity.ok(result).getBody();
            }

            // Card Number validation
            if (order.getCreditCardInformation().getCreditCardNumber() == null
                    || !order.getCreditCardInformation().getCreditCardNumber().matches("\\d{16}")){
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
                return ResponseEntity.ok(result).getBody();
            }


            // CVV validation
            if (order.getCreditCardInformation().getCvv() == null
                    || !order.getCreditCardInformation().getCvv().matches("\\d{3}")){
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
                return ResponseEntity.ok(result).getBody();
            }
            int calculatedTotal = order.getPizzasInOrder().stream()
                    .mapToInt(Order.Pizza::getPriceInPence)
                    .sum();
            int totalCharge = SystemConstants.ORDER_CHARGE_IN_PENCE + calculatedTotal;


            // Check maximum pizza count
            if (order.getPizzasInOrder().size() > SystemConstants.MAX_PIZZAS_PER_ORDER) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
                return ResponseEntity.ok(result).getBody();
            }

            // Validate Pizza Names and Prices
            String restaurantName = null;
            // Extract the day of the week from the orderDate
            //DayOfWeek orderDay = parseOrderDate(order.getOrderDate());
            // ---- Order date validation (DEFENSIVE) ----
            if (order.getOrderDate() == null) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                return ResponseEntity.ok(result).getBody();
            }

            LocalDate orderDate;
            try {
                orderDate = LocalDate.parse(order.getOrderDate());
            } catch (DateTimeParseException e) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                return ResponseEntity.ok(result).getBody();
            }

            DayOfWeek orderDay = orderDate.getDayOfWeek();
            // ---- end date validation ----


            for (Order.Pizza pizza : order.getPizzasInOrder()) {
                Restaurant restaurant = findRestaurantByPizza(pizza.getName());
                if (restaurant == null) {
                    result.setOrderStatus(OrderStatus.INVALID);
                    result.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                    return ResponseEntity.ok(result).getBody();
                }

                // Validate price
                if (!isPriceValid(pizza, restaurant)) {
                    result.setOrderStatus(OrderStatus.INVALID);
                    result.setOrderValidationCode(OrderValidationCode.PRICE_FOR_PIZZA_INVALID);
                    return ResponseEntity.ok(result).getBody();
                }

                // Ensure all pizzas are from the same restaurant
                if (restaurantName == null) {
                    restaurantName = restaurant.getName();
                } else if (!restaurant.getName().equals(restaurantName)) {
                    result.setOrderStatus(OrderStatus.INVALID);
                    result.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                    return ResponseEntity.ok(result).getBody();
                }
                // Check if the restaurant is open on the order day
                if (!restaurant.getOpeningDays().contains(orderDay.toString().toUpperCase())) {
                    result.setOrderStatus(OrderStatus.INVALID);
                    result.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                    return ResponseEntity.ok(result).getBody();
                }
            }
            // Total price validation (order-level)
            if (!Integer.valueOf(totalCharge).equals(order.getPriceTotalInPence())) {
                result.setOrderStatus(OrderStatus.INVALID);
                result.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
                result.setFailedAt(OrderValidationCode.TOTAL_CHECK);

                return ResponseEntity.ok(result).getBody();
            }


            // If all validations pass
            result.setOrderStatus(OrderStatus.VALID);
            result.setOrderValidationCode(OrderValidationCode.NO_ERROR);

        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Return a generic validation error response
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.GENERIC_ERROR);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result).getBody();
        }

        return result;
    }
    private Restaurant findRestaurantByPizza(String pizzaName) {
        return RestaurantData.getRestaurants().stream()
                .filter(r -> r.getMenu().stream().anyMatch(p -> p.getName().equalsIgnoreCase(pizzaName)))
                .findFirst()
                .orElse(null);
    }

    private boolean isPriceValid(Order.Pizza pizza, Restaurant restaurant) {
        return restaurant.getMenu().stream()
                .anyMatch(menuItem -> menuItem.getName().equals(pizza.getName()) && menuItem.getPriceInPence() == pizza.getPriceInPence());
    }
}

