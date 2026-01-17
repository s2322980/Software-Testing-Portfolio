package uk.ac.ed.inf.ilp_coursework;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.ilp_coursework.service.Clock;
import uk.ac.ed.inf.ilp_coursework.service.RestaurantService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ValidateOrderTestExtensive {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testValidCvv123() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testInvalidCvvLength2Numeric() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "12"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CVV_INVALID"));
    }

    @Test
    void testInvalidCvvAllLetters() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "abc"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CVV_INVALID"));
    }

    @Test
    void testInvalidCvvBoundary12a() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "12a"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CVV_INVALID"));
    }


    @Test
    void testValidExpiry0127() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testBoundaryValidExpiry0126() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/26",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testInvalidExpiredExpiry0125() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/25",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EXPIRY_DATE_INVALID"));
    }

    @Test
    void testInvalidExpiryWrongFormat01() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EXPIRY_DATE_INVALID"));
    }


    @Test
    void testValidCardNumber16Digits() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testInvalidCardNumberTooLong17Digits() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "12345678901234567",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CARD_NUMBER_INVALID"));
    }

    @Test
    void testInvalidCardNumberContainsLetter() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "123456789012345a",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("CARD_NUMBER_INVALID"));
    }


    @Test
    void testValidTotalIncludesDeliveryFee() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testInvalidTotalIncorrect() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1099,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("TOTAL_INCORRECT"));
    }


    @Test
    void testBoundaryValidPizzaCountOne() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testBoundaryValidPizzaCountFour() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 4100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testBoundaryInvalidPizzaCountZero() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 0,
          "pizzasInOrder": [],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("EMPTY_ORDER"));
    }

    @Test
    void testBoundaryInvalidPizzaCountFive() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 5100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("MAX_PIZZA_COUNT_EXCEEDED"));
    }


    @Test
    void testValidAllPizzasSameRestaurant() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 2500,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ERROR"));
    }

    @Test
    void testInvalidPizzaNotDefinedInAnyRestaurant() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "Nonexistent Pizza", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_NOT_DEFINED"));
    }

    @Test
    void testInvalidPizzasFromMultipleRestaurants() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 2500,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R3: Super Cheese", "priceInPence": 1400 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_FROM_MULTIPLE_RESTAURANTS"));
    }

    @Test
    void testInvalidRestaurantClosedOnOrderDate() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-01",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("RESTAURANT_CLOSED"));
    }


    @Test
    void testInvalidOrderDateFormatReturnsBadRequest() throws Exception {
        String body = """
        {
          "orderDate": "03-01-2025",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingCreditCardInformationReturnsBadRequest() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ]
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingPizzasInOrderReturnsBadRequest() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingCardNumberReturnsBadRequestOrInvalid() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardExpiry": "01/27",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingExpiryReturnsBadRequestOrInvalid() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "cvv": "123"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMissingCvvReturnsBadRequestOrInvalid() throws Exception {
        String body = """
        {
          "orderDate": "2025-01-03",
          "priceTotalInPence": 1100,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "1234567890123456",
            "creditCardExpiry": "01/27"
          }
        }
        """;

        mockMvc.perform(post("/validateOrder").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
