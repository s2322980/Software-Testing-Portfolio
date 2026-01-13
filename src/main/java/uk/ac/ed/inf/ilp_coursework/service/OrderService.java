package uk.ac.ed.inf.ilp_coursework.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp_coursework.data.Order;

import java.io.File;
import java.io.IOException;

public class OrderService {

    private ObjectMapper objectMapper = new ObjectMapper();

    public Order readOrderFromFile(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), Order.class);
    }
}
