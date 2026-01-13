package uk.ac.ed.inf.ilp_coursework;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.ilp_coursework.service.RestaurantService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class rerun_failed_tests {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void testValidOrderCoreFields() throws Exception {
        String requestBody = """
        {
          "orderNo": "21969631",
          "orderDate": "2025-03-07",
          "priceTotalInPence": 2500,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4057760657035765",
            "creditCardExpiry": "07/25",
            "cvv": "909"
          }
        }
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("VALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("NO_ORDER"));
    }
    @Test
    void testValidOrderExtraFields() throws Exception {
        String requestBody = """
        {
          "orderNo": "21969631",
          "orderDate": "2025-03-07",
          "orderStatus": "VALID",
          "orderValidationCode": "NO_ERROR",
          "priceTotalInPence": 2500,
          "pizzasInOrder": [
            { "name": "R1: Margarita", "priceInPence": 1000 },
            { "name": "R1: Calzone", "priceInPence": 1400 }
          ],
          "creditCardInformation": {
            "creditCardNumber": "4057760657035765",
            "creditCardExpiry": "07/25",
            "cvv": "909"
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
    void testInvalidOrderPizzaNotDefined() throws Exception {
        String requestBody = """
        {"orderNo":"4F180FCC","orderDate":"2025-03-07","orderStatus":"INVALID","orderValidationCode":"PIZZA_NOT_DEFINED","priceTotalInPence":498705280,"pizzasInOrder":[{"name":"R5: Pizza Dream","priceInPence":1400},{"name":"R5: My kind of pizza","priceInPence":900},{"name":"Pizza-Surprise ","priceInPence":498702880}],"creditCardInformation":{"creditCardNumber":"4347653335471405","creditCardExpiry":"05/25","cvv":"193"}}
    """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_NOT_DEFINED"));
    }
    @Test
    void testInvalidOrderMaxPizza() throws Exception {
        String requestBody = """
                {"orderNo":"12C9359B","orderDate":"2025-03-07","orderStatus":"INVALID","orderValidationCode":"MAX_PIZZA_COUNT_EXCEEDED","priceTotalInPence":8000,"pizzasInOrder":[{"name":"R5: Pizza Dream","priceInPence":1400},{"name":"R5: My kind of pizza","priceInPence":900},{"name":"R5: Pizza Dream","priceInPence":1400},{"name":"R5: Pizza Dream","priceInPence":1400},{"name":"R5: Pizza Dream","priceInPence":1400},{"name":"R5: Pizza Dream","priceInPence":1400}],"creditCardInformation":{"creditCardNumber":"5331349164762510","creditCardExpiry":"03/26","cvv":"491"}}
                """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("MAX_PIZZA_COUNT_EXCEEDED"));
    }
    @Test
    void testInvalidOrderMultipleRestaurants() throws Exception {
        String requestBody = """
                {"orderNo":"3F94CFE9","orderDate":"2025-03-07","orderStatus":"INVALID","orderValidationCode":"PIZZA_FROM_MULTIPLE_RESTAURANTS","priceTotalInPence":3900,"pizzasInOrder":[{"name":"R1: Margarita","priceInPence":1000},{"name":"R1: Calzone","priceInPence":1400},{"name":"R2: Meat Lover","priceInPence":1400}],"creditCardInformation":{"creditCardNumber":"4364716037816694","creditCardExpiry":"06/25","cvv":"229"}}
                """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PIZZA_FROM_MULTIPLE_RESTAURANTS"));
    }
    @Test
    void testInvalidOrderRestaurantClosed() throws Exception {
        String requestBody = """
                {"orderNo":"2297EC5E","orderDate":"2025-03-05","orderStatus":"INVALID","orderValidationCode":"RESTAURANT_CLOSED","priceTotalInPence":1500,"pizzasInOrder":[{"name":"R3: Super Cheese","priceInPence":1400}],"creditCardInformation":{"creditCardNumber":"4441628647227856","creditCardExpiry":"07/25","cvv":"603"}}
                """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("RESTAURANT_CLOSED"));
    }
    @Test
    void testInvalidOrderPriceInvalid() throws Exception {
        String requestBody = """
                {"orderNo":"2669648A","orderDate":"2025-03-07","orderStatus":"INVALID","orderValidationCode":"PRICE_FOR_PIZZA_INVALID","priceTotalInPence":2500,"pizzasInOrder":[{"name":"R5: My kind of pizza","priceInPence":900},{"name":"R5: Pizza Dream","priceInPence":1500}],"creditCardInformation":{"creditCardNumber":"5143515133709869","creditCardExpiry":"07/25","cvv":"392"}}
                """;

        mockMvc.perform(post("/validateOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("INVALID"))
                .andExpect(jsonPath("$.orderValidationCode").value("PRICE_FOR_PIZZA_INVALID"));
    }



}
