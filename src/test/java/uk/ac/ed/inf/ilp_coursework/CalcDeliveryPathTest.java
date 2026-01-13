package uk.ac.ed.inf.ilp_coursework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.ilp_coursework.data.LngLat;
import uk.ac.ed.inf.ilp_coursework.data.Position;
import uk.ac.ed.inf.ilp_coursework.data.Region;

import java.io.File;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CalcDeliveryPathTest {
    private static List<Map<String, Object>> geoJsonPaths = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidOrderR6() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-06",
                "priceTotalInPence": 2400,
                "pizzasInOrder": [
            { "name": "R6: Sucuk delight", "priceInPence": 1400 },
            { "name": "R6: Dreams of Syria", "priceInPence": 900 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R6 Order Path", response, geoJsonPaths);

    }

    @Test
    void testValidOrderR1() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-06",
                "priceTotalInPence": 2500,
                "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty()); // Path should be returned
    }

    @Test
    void plotValidOrderR1Path() throws Exception {
        String validOrder = """
    {
        "orderNo": "72245E15",
        "orderDate": "2025-01-06",
        "priceTotalInPence": 2500,
        "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
        ],
        "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/25",
            "cvv": "123"
        }
    }
    """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R1 Order Path", response, geoJsonPaths);
    }


    @Test
    void testValidOrderR2() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-06",
                "priceTotalInPence": 2600,
                "pizzasInOrder": [
            { "name": "R2: Meat Lover", "priceInPence": 1400 },
            { "name": "R2: Vegan Delight", "priceInPence": 1100 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R2 Order Path", response, geoJsonPaths);
    }

    @Test
    void testValidOrderR3() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-05",
                "priceTotalInPence": 2400,
                "pizzasInOrder": [
            { "name": "R3: Super Cheese", "priceInPence": 1400 },
            { "name": "R3: All Shrooms", "priceInPence": 900 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R3 Order Path", response, geoJsonPaths);

    }

    @Test
    void testValidOrderR4() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-07",
                "priceTotalInPence": 2400,
                "pizzasInOrder": [
            { "name": "R4: Proper Pizza", "priceInPence": 1400 },
            { "name": "R4: Pineapple & Ham & Cheese", "priceInPence": 900 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R4 Order Path", response, geoJsonPaths);

    }

    @Test
    void testValidOrderR5() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-06",
                "priceTotalInPence": 2400,
                "pizzasInOrder": [
            { "name": "R5: Pizza Dream", "priceInPence": 1400 },
            { "name": "R5: My kind of pizza", "priceInPence": 900 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/25",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R5 Order Path", response, geoJsonPaths);

    }

    @Test
    void testValidOrderR7() throws Exception {
        String validOrder = """
        {
            "orderNo": "72245E15",
                "orderDate": "2025-01-07",
                "priceTotalInPence": 2400,
                "pizzasInOrder": [
            { "name": "R8: Hot, hotter, the hottest", "priceInPence": 1400 },
            { "name": "R8: All you ever wanted", "priceInPence": 900 }
              ],
            "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
                    "creditCardExpiry": "12/27",
                    "cvv": "123"
        }
        }
        """;

        // Perform the request and get the response
        String response = mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrder))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Add the path to GeoJSON
        addPathToGeoJson("R7 Order Path", response, geoJsonPaths);

    }

    @Test
    void testInvalidOrder() throws Exception {
        String invalidOrder = """
            {
                "orderNo": "20BD5183",
                "orderDate": "2025-01-22",
                "priceTotalInPence": 2500,
                "pizzasInOrder": [{ "name": "R7: Hot, hotter, the hottest", "priceInPence": 1400 }],
                "creditCardInformation": {
                    "creditCardNumber": "909660427979271",
                    "creditCardExpiry": "03/25",
                    "cvv": "943"
                }
            }
        """;

        mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrder))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOrderWithNoRestaurantFound() throws Exception {
        String orderWithInvalidPizza = """
            {
                "orderNo": "20BD5183",
                "orderDate": "2025-01-22",
                "priceTotalInPence": 2500,
                "pizzasInOrder": [
                    { "name": "Nonexistent Pizza", "priceInPence": 1000 }
                ],
                "creditCardInformation": {
                    "creditCardNumber": "909660427979271",
                    "creditCardExpiry": "03/25",
                    "cvv": "943"
                }
            }
        """;

        mockMvc.perform(post("/calcDeliveryPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderWithInvalidPizza))
                .andExpect(status().isBadRequest());
    }

    @AfterAll
    static void generateGeoJson() throws Exception {
        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");

        List<Map<String, Object>> features = new ArrayList<>();

        // Add paths generated during tests
        features.addAll(geoJsonPaths);

        geoJson.put("features", features);

        // Write to GeoJSON file
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.geojson4"), geoJson);
    }


    private void addPathToGeoJson(String name, String jsonResponse, List<Map<String, Object>> features) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // Deserialize JSON response
        List<Map<String, Double>> path = mapper.readValue(jsonResponse, new TypeReference<List<Map<String, Double>>>() {});

        // Convert to GeoJSON coordinates
        List<List<Double>> geoJsonCoordinates = new ArrayList<>();
        for (Map<String, Double> point : path) {
            geoJsonCoordinates.add(Arrays.asList(point.get("lng"), point.get("lat")));
        }

        // Create GeoJSON feature
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("stroke", "#0000ff");
        properties.put("stroke-width", 2);

        feature.put("properties", properties);

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "LineString");
        geometry.put("coordinates", geoJsonCoordinates);

        feature.put("geometry", geometry);

        features.add(feature);
    }



}
