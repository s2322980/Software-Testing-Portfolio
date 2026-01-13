package uk.ac.ed.inf.ilp_coursework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.ilp_coursework.data.Restaurant;
import uk.ac.ed.inf.ilp_coursework.data.RestaurantData;
import uk.ac.ed.inf.ilp_coursework.repository.RestaurantRepository;
import uk.ac.ed.inf.ilp_coursework.service.Clock;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderValidationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantRepository restaurantRepository;

    @MockBean
    private Clock clock;

    @BeforeEach
    void setup() {
        // deterministic time for all tests
        when(clock.today()).thenReturn(LocalDate.of(2025, 1, 3));

        // controlled restaurant dataset
        List<Restaurant> restaurants = RestaurantData.getRestaurants();
        when(restaurantRepository.getRestaurants()).thenReturn(restaurants);
    }

    @Test
    void testValidOrder() throws Exception {
        String requestBody = """
            {
              "orderNo": "72245E15",
              "orderDate": "2025-01-03",
              "priceTotalInPence": 2500,
              "pizzasInOrder": [
                { "name": "R1: Margarita", "priceInPence": 1000 },
                { "name": "R1: Calzone", "priceInPence": 1400 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "4242424242424242",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              }
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testEmptyOrder() throws Exception {
        String requestBody = """
            {
              "pizzasInOrder": [],
              "creditCardInformation": {
                "creditCardNumber": "4242424242424242",
                "expiryDate": "12/27",
                "cvv": "123"
              },
              "priceTotalInPence": 0
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EMPTY_ORDER"));
    }

    @Test
    void testInvalidCreditCardNumberLength() throws Exception {
        String requestBody = """
            {
              "pizzasInOrder": [
                { "name": "Margherita", "priceInPence": 899 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "12345678901234",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              },
              "priceTotalInPence": 999
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CARD_NUMBER_INVALID"));
    }
    @Test
    void testInvalidCreditCardNumberType() throws Exception {
        String requestBody = """
            {
              "pizzasInOrder": [
                { "name": "Margherita", "priceInPence": 899 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "12345678901234ab",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              },
              "priceTotalInPence": 899
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CARD_NUMBER_INVALID"));
    }
    @Test
    void testInvalidTotalPrice() throws Exception {
        String requestBody = """
        {
          "priceTotalInPence": 1998,
          "orderDate": "2025-01-03",
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/27",
            "cvv": "123"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("TOTAL_INCORRECT"));
    }

    @Test
    void testInvalidExpiryDateFormat() throws Exception {
        String requestBody = """
        {
          "priceTotalInPence": 1998,
          "pizzasInOrder": [
            { "name": "Margherita", "priceInPence": 899 },
            { "name": "Pepperoni", "priceInPence": 1099 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "2027/12",
            "cvv": "123"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EXPIRY_DATE_INVALID"));
    }

    @Test
    void testExpiredCreditCard() throws Exception {
        String requestBody = """
        {
          "priceTotalInPence": 1998,
          "pizzasInOrder": [
            { "name": "Margherita", "priceInPence": 899 },
            { "name": "Pepperoni", "priceInPence": 1099 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/21",
            "cvv": "123"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EXPIRY_DATE_INVALID"));
    }

    @Test
    void testInvalidCVVLength() throws Exception {
        String requestBody = """
        {
          "priceTotalInPence": 899,
          "pizzasInOrder": [
            { "name": "Margherita", "priceInPence": 899 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/27",
            "cvv": "12"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CVV_INVALID"));
    }

    @Test
    void testInvalidCVVType() throws Exception {
        String requestBody = """
        {
          "priceTotalInPence": 899,
          "pizzasInOrder": [
            { "name": "Margherita", "priceInPence": 899 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/27",
            "cvv": "12a"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CVV_INVALID"));
    }
    @Test
    void testMaxPizzaCountExceeded() throws Exception {
        String requestBody = """
            {
              "priceTotalInPence": 5000,
              "pizzasInOrder": [
                { "name": "R1: Margarita", "priceInPence": 1000 },
                { "name": "R1: Calzone", "priceInPence": 1000 },
                { "name": "R1: Margarita", "priceInPence": 1000 },
                { "name": "R1: Calzone", "priceInPence": 1000 },
                { "name": "R1: Margarita", "priceInPence": 1000 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "4242424242424242",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              }
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("MAX_PIZZA_COUNT_EXCEEDED"));
    }

    @Test
    void testPizzaNotDefined() throws Exception {
        String requestBody = """
            {
              "orderNo": "72245E15",
              "orderDate": "2025-01-02",
              "priceTotalInPence": 1000,
              "pizzasInOrder": [
                { "name": "Nonexistent Pizza", "priceInPence": 1000 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "4242424242424242",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              }
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_NOT_DEFINED"));
    }

    @Test
    void testMultipleRestaurantViolation() throws Exception {
        String requestBody = """
            {
              "orderNo": "020DA1C2",
               "orderDate": "2025-01-03",
              "orderStatus": "INVALID",
                           "orderValidationCode": "PIZZA_FROM_MULTIPLE_RESTAURANTS",
                           "priceTotalInPence": 3400,
                           "pizzasInOrder": [
                             {
                               "name": "R3: Super Cheese",
                               "priceInPence": 1400
                             },
                             {
                               "name": "R3: All Shrooms",
                               "priceInPence": 900
                             },
                             {
                               "name": "R1: Margarita",
                               "priceInPence": 1000
                             }
                           ],
                           "creditCardInformation": {
                             "creditCardNumber": "5159568403235165",
                             "creditCardExpiry": "01/27",
                             "cvv": "789"
                           }
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_FROM_MULTIPLE_RESTAURANTS"));
    }

    @Test
    void testRestaurantClosedOnOrderDate() throws Exception {
        String requestBody = """
            {
              "orderDate": "2025-01-01",
              "priceTotalInPence": 1000,
              "pizzasInOrder": [
                { "name": "R1: Margarita", "priceInPence": 1000 }
              ],
              "creditCardInformation": {
                "creditCardNumber": "4242424242424242",
                "creditCardExpiry": "12/27",
                "cvv": "123"
              }
            }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("RESTAURANT_CLOSED"));
    }

    @Test
    void testMissingOrderDateTreatedAsRestaurantClosed() throws Exception {
        String requestBody = """
    {
      "priceTotalInPence": 999,
      "pizzasInOrder": [
        { "name": "R1: Margarita", "priceInPence": 899 }
      ],
      "creditCardInformation": {
        "creditCardNumber": "4242424242424242",
        "creditCardExpiry": "12/27",
        "cvv": "123"
      }
    }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("RESTAURANT_CLOSED"));
    }

    @Test
    void testMaxAllowedPizzaCountIsValid() throws Exception {
        String requestBody = """
    {
      "orderDate": "2027-01-03",
      "priceTotalInPence": 4900,
      "pizzasInOrder": [
        { "name": "R1: Margarita", "priceInPence": 1000 },
        { "name": "R1: Calzone", "priceInPence": 1400 },
        { "name": "R1: Margarita", "priceInPence": 1000 },
        { "name": "R1: Calzone", "priceInPence": 1400 }
      ],
      "creditCardInformation": {
        "creditCardNumber": "4242424242424242",
        "creditCardExpiry": "12/27",
        "cvv": "123"
      }
    }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }
    @Test
    void testSinglePizzaValid() throws Exception {
        String body = """
        {
          "orderDate": "2027-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4242424242424242",
            "creditCardExpiry": "12/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"));
    }
    @Test
    void testMalformedJsonReturnsBadRequest() throws Exception {
        String body = "{ this is not valid json }";

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }


}
