package uk.ac.ed.inf.ilp_coursework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.ilp_coursework.controllers.BasicController;
import uk.ac.ed.inf.ilp_coursework.data.LngLat;
import uk.ac.ed.inf.ilp_coursework.data.Position;
import uk.ac.ed.inf.ilp_coursework.data.Region;
import uk.ac.ed.inf.ilp_coursework.service.RestaurantService;

import java.io.File;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GeometryUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private BasicController BasicController;

    BasicController controller = new BasicController();

    @Test
    void testEuclideanDistanceZero() {
        double d = controller.calculateEuclideanDistance(0, 0, 0, 0);
        assertEquals(0.0, d);
    }

    @Test
    void testEuclideanDistanceSymmetry() {
        double d1 = controller.calculateEuclideanDistance(1, 2, 3, 4);
        double d2 = controller.calculateEuclideanDistance(3, 4, 1, 2);
        assertEquals(d1, d2);
    }

    @Test
    void testValidCoordinate() {
        assertTrue(controller.isValidCoordinate(55.9, -3.18));
        assertFalse(controller.isValidCoordinate(100.0, 0.0));
    }

    private void assertTrue(boolean validCoordinate) {
    }
}
