package uk.ac.ed.inf.ilp_coursework.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp_coursework.data.Restaurant;

import java.io.InputStream;
import java.util.List;

public class RestaurantService {
    private List<Restaurant> restaurants;

    public void loadRestaurants() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("restaurants.json");
        restaurants = objectMapper.readValue(inputStream, new TypeReference<List<Restaurant>>() {});
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}

