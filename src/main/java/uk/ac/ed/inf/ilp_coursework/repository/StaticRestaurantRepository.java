package uk.ac.ed.inf.ilp_coursework.repository;

import org.springframework.stereotype.Repository;
import uk.ac.ed.inf.ilp_coursework.data.Restaurant;
import uk.ac.ed.inf.ilp_coursework.data.RestaurantData;

import java.util.List;

@Repository
public class StaticRestaurantRepository implements RestaurantRepository {

    @Override
    public List<Restaurant> getRestaurants() {
        return RestaurantData.getRestaurants();
    }
}

