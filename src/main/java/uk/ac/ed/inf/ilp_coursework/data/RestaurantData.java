package uk.ac.ed.inf.ilp_coursework.data;

import java.util.ArrayList;
import java.util.List;

public class RestaurantData {

    public static List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        // Civerinos Slice
        Restaurant civerinosSlice = new Restaurant();
        civerinosSlice.setName("R1"); //Civerinos Slice
        civerinosSlice.setLocation(new Position(-3.19128692150116, 55.9455351525177));
        civerinosSlice.setOpeningDays(List.of("MONDAY", "TUESDAY", "FRIDAY", "SATURDAY", "SUNDAY"));
        civerinosSlice.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R1: Margarita", 1000),
                new Restaurant.MenuItem("R1: Calzone", 1400)
        )));
        restaurants.add(civerinosSlice);

        // Sora Lella Vegan Restaurant
        Restaurant soraLella = new Restaurant();
        soraLella.setName("R2"); //Sora Lella Vegan Restaurant
        soraLella.setLocation(new Position(-3.20254147052765, 55.9432847375794));
        soraLella.setOpeningDays(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
        soraLella.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R2: Meat Lover", 1400),
                new Restaurant.MenuItem("R2: Vegan Delight", 1100)
        )));
        restaurants.add(soraLella);

        // Domino's Pizza
        Restaurant dominos = new Restaurant();
        dominos.setName("R3"); //Domino's Pizza - Edinburgh - Southside
        dominos.setLocation(new Position(-3.18385720252991, 55.9444987687571));
        dominos.setOpeningDays(List.of("WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"));
        dominos.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R3: Super Cheese", 1400),
                new Restaurant.MenuItem("R3: All Shrooms", 900)
        )));
        restaurants.add(dominos);

        // Sodeberg Pavillion
        Restaurant sodeberg = new Restaurant();
        sodeberg.setName("R4"); //Sodeberg Pavillion
        sodeberg.setLocation(new Position(-3.19401741027832, 55.9439069661694));
        sodeberg.setOpeningDays(List.of("TUESDAY", "WEDNESDAY", "SATURDAY", "SUNDAY"));
        sodeberg.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R4: Proper Pizza", 1400),
                new Restaurant.MenuItem("R4: Pineapple & Ham & Cheese", 900)
        )));
        restaurants.add(sodeberg);

        // La Trattoria
        Restaurant laTrattoria = new Restaurant();
        laTrattoria.setName("R5"); //La Trattoria
        laTrattoria.setLocation(new Position(-3.1810810679852, 55.9389106437358));
        laTrattoria.setOpeningDays(List.of("MONDAY", "THURSDAY", "SATURDAY", "SUNDAY"));
        laTrattoria.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R5: Pizza Dream", 1400),
                new Restaurant.MenuItem("R5: My kind of pizza", 900)
        )));
        restaurants.add(laTrattoria);

        // Halal Pizza
        Restaurant halalPizza = new Restaurant();
        halalPizza.setName("R6"); //Halal Pizza
        halalPizza.setLocation(new Position(-3.18542820314392, 55.945846113595));
        halalPizza.setOpeningDays(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "SATURDAY", "SUNDAY"));
        halalPizza.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R6: Sucuk delight", 1400),
                new Restaurant.MenuItem("R6: Dreams of Syria", 900)
        )));
        restaurants.add(halalPizza);

        // World of Pizza
        Restaurant worldOfPizza = new Restaurant();
        worldOfPizza.setName("R8"); //World of Pizza
        worldOfPizza.setLocation(new Position(-3.17979897206425, 55.939884084483));
        worldOfPizza.setOpeningDays(List.of("THURSDAY", "FRIDAY", "TUESDAY"));
        worldOfPizza.setMenu(new ArrayList<>(List.of(
                new Restaurant.MenuItem("R8: Hot, hotter, the hottest", 1400),
                new Restaurant.MenuItem("R8: All you ever wanted", 900)
        )));
        restaurants.add(worldOfPizza);

        return restaurants;
    }
}
