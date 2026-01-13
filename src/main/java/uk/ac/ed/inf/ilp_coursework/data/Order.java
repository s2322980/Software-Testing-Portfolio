package uk.ac.ed.inf.ilp_coursework.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class Order {
    @Getter
    @Setter
    private String orderNo;
    private String orderDate;
    private int priceTotalInPence;
    @Setter
    @Getter
    private List<Pizza> pizzasInOrder;
    private CreditCardInformation creditCardInformation;


    @Getter
    @Setter


    public static class Pizza {
        private String name;
        private int priceInPence;
        private String restaurantId;

    }
    @Getter
    @Setter


    public static class CreditCardInformation {
        private String creditCardNumber;
        private String creditCardExpiry;
        private String cvv;

    }

}
