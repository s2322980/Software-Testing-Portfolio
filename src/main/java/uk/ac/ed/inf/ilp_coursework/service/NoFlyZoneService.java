package uk.ac.ed.inf.ilp_coursework.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.ilp_coursework.data.Region;

import java.io.File;
import java.util.List;

public class NoFlyZoneService {
    public List<Region> loadNoFlyZones(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), new TypeReference<List<Region>>() {});
    }
}
