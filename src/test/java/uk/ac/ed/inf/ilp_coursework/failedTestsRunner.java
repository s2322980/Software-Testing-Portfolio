package uk.ac.ed.inf.ilp_coursework;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class failedTestsRunner {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOG_FILE = "failed_test_results.log";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup() {
        log("===== Re-running Failed Tests at " + LocalDateTime.now() + " =====\n");
    }

    @Test
    @Order(1)
    @DisplayName("Validate Order - Expected INVALID")
    void testValidateOrderInvalid() {
        String endpoint = "/validateOrder";
        Map<String, Object> request = Map.of(
                "orderNo", "21969631",
                "orderDate", "2025-03-07",
                "priceTotalInPence", 2500,
                "pizzasInOrder", new Object[]{
                        Map.of("name", "R1: Margarita", "priceInPence", 1000),
                        Map.of("name", "R1: Calzone", "priceInPence", 1400)
                },
                "creditCardInformation", Map.of(
                        "creditCardNumber", "4057760657035765",
                        "creditCardExpiry", "07/25",
                        "cvv", "909"
                )
        );

        runTest(endpoint, request, HttpStatus.OK);
    }

    @Test
    @Order(2)
    @DisplayName("Validate Order - Invalid Credit Card Number")
    void testValidateOrderCardInvalid() {
        String endpoint = "/validateOrder";
        Map<String, Object> request = Map.of(
                "orderNo", "339C9928",
                "orderDate", "2025-03-07",
                "priceTotalInPence", 2500,
                "pizzasInOrder", new Object[]{
                        Map.of("name", "R1: Margarita", "priceInPence", 1000),
                        Map.of("name", "R1: Calzone", "priceInPence", 1400)
                },
                "creditCardInformation", Map.of(
                        "creditCardNumber", "4429428073148", // Invalid card number
                        "creditCardExpiry", "05/25",
                        "cvv", "776"
                )
        );

        runTest(endpoint, request, HttpStatus.OK);
    }

    private void runTest(String endpoint, Map<String, Object> requestData, HttpStatus expectedStatus) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestData), headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.exchange(BASE_URL + endpoint, HttpMethod.POST, request, String.class);
            long elapsedTime = System.currentTimeMillis() - startTime;

            logResult(endpoint, requestData, HttpStatus.valueOf(response.getStatusCode().value())
                    , expectedStatus, response.getBody(), elapsedTime);
            Assertions.assertEquals(expectedStatus, response.getStatusCode());
        } catch (Exception e) {
            log("ERROR: Failed to execute test for " + endpoint + "\nException: " + e.getMessage());
            Assertions.fail(e.getMessage());
        }
    }

    private static void logResult(String endpoint, Map<String, Object> requestData, HttpStatus actual, HttpStatus expected, String responseBody, long time) {
        log("\nTest: " + endpoint +
                "\nExpected: " + expected +
                "\nActual: " + actual +
                "\nResponse: " + responseBody +
                "\nRuntime: " + time + "ms\n");
    }

    private static void log(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + "\n");
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
