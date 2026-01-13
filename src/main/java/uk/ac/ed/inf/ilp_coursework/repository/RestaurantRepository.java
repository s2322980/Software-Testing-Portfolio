package uk.ac.ed.inf.ilp_coursework.repository;

import uk.ac.ed.inf.ilp_coursework.data.Restaurant;
import uk.ac.ed.inf.ilp_coursework.data.RestaurantData;

import java.util.List;

public interface RestaurantRepository {
    List<Restaurant> getRestaurants();
}

