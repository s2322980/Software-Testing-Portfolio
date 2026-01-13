package uk.ac.ed.inf.ilp_coursework.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class  Restaurant {
    private String name;
    private Position location; // Changed from Location class
    private List<String> openingDays;
    private List<MenuItem> menu;

    @Getter
    @Setter
    public static class MenuItem {
        private String name;
        private int priceInPence;

        public MenuItem(String name, int priceInPence) {
            this.name = name;
            this.priceInPence = priceInPence;
        }
    }
}
