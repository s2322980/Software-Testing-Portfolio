package uk.ac.ed.inf.ilp_coursework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.*;

public class GeoJsonGenerator {
    public static void main(String[] args) throws Exception {
        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");

        List<Map<String, Object>> features = new ArrayList<>();

        // Add restaurants
        features.add(createPointFeature("Civerinos Slice", "#0000ff", -3.1912869215011597, 55.945535152517735));
        features.add(createPointFeature("Sodeberg Pavillion", "#0000ff", -3.1940174102783203, 55.94390696616939));
        features.add(createPointFeature("Sora Lella Vegan Restaurant", "#0000ff", -3.202541470527649, 55.943284737579376));
        features.add(createPointFeature("Domino's Pizza - Edinburgh - Southside", "#0000ff", -3.1838572025299072, 55.94449876875712));
        features.add(createPointFeature("La Trattoria", "#0000ff", -3.1810810679852, 55.9389106437358));
        features.add(createPointFeature("Halal Pizza", "#0000ff", -3.18542820314392, 55.945846113595));
        features.add(createPointFeature("World of Pizza", "#0000ff", -3.17979897206425, 55.939884084483));

        // Add Appleton Tower
        features.add(createPointFeature("Appleton Tower", "#00ff00", -3.186874, 55.944494));

        // Add central area
        features.add(createPolygonFeature("Central Area", "#ffcc00", "#ff9900", Arrays.asList(
                Arrays.asList(-3.192473, 55.946233),
                Arrays.asList(-3.184319, 55.946233),
                Arrays.asList(-3.184319, 55.941662),
                Arrays.asList(-3.192473, 55.941662),
                Arrays.asList(-3.192473, 55.946233)
        )));

        // Add no-fly zones
        features.add(createPolygonFeature("George Square Area", "#ff0000", "#cc0000", Arrays.asList(
                Arrays.asList(-3.19057881832123, 55.9440241257753),
                Arrays.asList(-3.18998873233795, 55.9428465054091),
                Arrays.asList(-3.1870973110199, 55.9432881172426),
                Arrays.asList(-3.18768203258514, 55.9444777403937),
                Arrays.asList(-3.19057881832123, 55.9440241257753)
        )));

        features.add(createPolygonFeature("Dr Elsie Inglis Quadrangle", "#ff0000", "#cc0000", Arrays.asList(
                Arrays.asList(-3.19071829319, 55.9451957023404),
                Arrays.asList(-3.19061636924744, 55.9449824179636),
                Arrays.asList(-3.19002628326416, 55.9450755422726),
                Arrays.asList(-3.19013357162476, 55.945297838105),
                Arrays.asList(-3.19071829319, 55.9451957023404)
        )));

        features.add(createPolygonFeature("Bristo Square Open Area", "#ff0000", "#cc0000", Arrays.asList(
                Arrays.asList(-3.18954348564148, 55.9455231366331),
                Arrays.asList(-3.18938255310059, 55.9455321485469),
                Arrays.asList(-3.1892591714859, 55.9454480372693),
                Arrays.asList(-3.18920016288757, 55.9453368899437),
                Arrays.asList(-3.18919479846954, 55.9451957023404),
                Arrays.asList(-3.18913578987122, 55.9451175983387),
                Arrays.asList(-3.18813800811768, 55.9452738061846),
                Arrays.asList(-3.18855106830597, 55.9461059027456),
                Arrays.asList(-3.18953812122345, 55.9455591842759),
                Arrays.asList(-3.18954348564148, 55.9455231366331)
        )));

        features.add(createPolygonFeature("Bayes Central Area", "#ff0000", "#cc0000", Arrays.asList(
                Arrays.asList(-3.1876927614212, 55.9452069673277),
                Arrays.asList(-3.18755596876144, 55.9449621408666),
                Arrays.asList(-3.18698197603226, 55.9450567672283),
                Arrays.asList(-3.18723276257515, 55.9453699337766),
                Arrays.asList(-3.18744599819183, 55.9453361389472),
                Arrays.asList(-3.18737357854843, 55.9451934493426),
                Arrays.asList(-3.18759351968765, 55.9451566503593),
                Arrays.asList(-3.18762436509132, 55.9452197343093),
                Arrays.asList(-3.1876927614212, 55.9452069673277)
        )));

        // Add drone path
        features.add(createLineStringFeature("Drone Path", "#0000ff", Arrays.asList(
                Arrays.asList(-3.1912869215011597, 55.945535152517735),
                Arrays.asList(-3.186874, 55.944494)
        )));

        geoJson.put("features", features);

        // Write to a file
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.geojson"), geoJson);
    }


    private static Map<String, Object> createPointFeature(String name, String color, double lng, double lat) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("marker-color", color);
        feature.put("properties", properties);

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", Arrays.asList(lng, lat));
        feature.put("geometry", geometry);

        return feature;
    }

    private static Map<String, Object> createPolygonFeature(String name, String fillColor, String strokeColor, List<List<Double>> coordinates) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("fill", fillColor);
        properties.put("fill-opacity", 0.4);
        properties.put("stroke", strokeColor);
        properties.put("stroke-width", 2);
        feature.put("properties", properties);

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "Polygon");
        geometry.put("coordinates", Collections.singletonList(coordinates));
        feature.put("geometry", geometry);

        return feature;
    }

    private static Map<String, Object> createLineStringFeature(String name, String strokeColor, List<List<Double>> coordinates) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("stroke", strokeColor);
        properties.put("stroke-width", 2);
        feature.put("properties", properties);

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "LineString");
        geometry.put("coordinates", coordinates);
        feature.put("geometry", geometry);

        return feature;
    }
}
