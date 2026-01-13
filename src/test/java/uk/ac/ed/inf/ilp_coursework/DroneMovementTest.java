package uk.ac.ed.inf.ilp_coursework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DroneMovementTest {

    @Autowired
    private MockMvc mockMvc;

    /* =========================
       FR-N-01: 16 COMPASS DIRECTIONS
       ========================= */

    @Test
    void testNextPositionNorth() throws Exception {
        String body = """
        {
          "start": { "lat": 55.0, "lng": -3.0 },
          "angle": 90
        }
        """;

        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lat", greaterThan(55.0)))
                .andExpect(jsonPath("$.lng", closeTo(-3.0, 1e-6)));
    }

    @Test
    void testNextPositionEast() throws Exception {
        String body = """
        {
          "start": { "lat": 55.0, "lng": -3.0 },
          "angle": 0
        }
        """;

        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng", greaterThan(-3.0)))
                .andExpect(jsonPath("$.lat", closeTo(55.0, 1e-6)));
    }

    /* =========================
       FR-N-02: FIXED STEP SIZE
       ========================= */

    @Test
    void testStepSizeIsConstant() throws Exception {
        String body = """
        {
          "start": { "lat": 55.0, "lng": -3.0 },
          "angle": 0
        }
        """;

        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng",
                        closeTo(-3.0 + 0.00015, 1e-6)));
    }

    /* =========================
       UT-GEO-01: INVALID COORDINATES
       ========================= */

    @Test
    void testInvalidCoordinatesRejected() throws Exception {
        String body = """
        {
          "start": { "lat": 200.0, "lng": -3.0 },
          "angle": 90
        }
        """;

        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    /* =========================
       UT-GEO-02 & UT-GEO-03
       ========================= */

    @Test
    void testIsCloseToTrueWithinThreshold() throws Exception {
        String body = """
        {
          "position1": { "lat": 55.0, "lng": -3.0 },
          "position2": { "lat": 55.0001, "lng": -3.0001 }
        }
        """;

        mockMvc.perform(post("/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testDistanceSymmetry() throws Exception {
        String body = """
        {
          "position1": { "lat": 55.0, "lng": -3.0 },
          "position2": { "lat": 56.0, "lng": -4.0 }
        }
        """;

        String reversed = """
        {
          "position1": { "lat": 56.0, "lng": -4.0 },
          "position2": { "lat": 55.0, "lng": -3.0 }
        }
        """;

        String d1 = mockMvc.perform(post("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();

        String d2 = mockMvc.perform(post("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reversed))
                .andReturn().getResponse().getContentAsString();

        assertEquals(d1, d2);
    }
}
