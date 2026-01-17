package uk.ac.ed.inf.ilp_coursework.controllers;

import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.ilp_coursework.data.*;
import uk.ac.ed.inf.ilp_coursework.service.DeliveryService;
import uk.ac.ed.inf.ilp_coursework.data.SystemConstants;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ed.inf.ilp_coursework.data.DeliveryPathRequest.getCentralArea;



@RestController
public class BasicController {

    @GetMapping({"/isAlive", "/isalive"})
    public boolean isAlive() {
        return true;
    }

    @GetMapping("/uuid")
    public String uuid() {
        return "s2322980";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody LngLatPairRequest request) {
        if (request == null ||
                request.getPosition1() == null ||
                request.getPosition1().getLat() == null ||
                request.getPosition1().getLng() == null ||
                request.getPosition2() == null ||
                request.getPosition2().getLat() == null ||
                request.getPosition2().getLng() == null) {

            return ResponseEntity.badRequest().build();
        }
        Position pos1 = request.getPosition1();
        Position pos2 = request.getPosition2();

        if (!isValidCoordinate(pos1.getLat(), pos1.getLng()) ||
                !isValidCoordinate(pos2.getLat(), pos2.getLng())) {
            return ResponseEntity.badRequest().body(null);
        }


        try {

            double distance = calculateEuclideanDistance(
                    pos1.getLat(), pos1.getLng(),
                    pos2.getLat(), pos2.getLng()
            );
            distance = round(distance, 6);
            return ResponseEntity.ok(distance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody LngLatPairRequest request) {
        // Check if the request or positions are null
        if (request == null ||
                request.getPosition1() == null ||
                request.getPosition1().getLat() == null ||
                request.getPosition1().getLng() == null ||
                request.getPosition2() == null ||
                request.getPosition2().getLat() == null ||
                request.getPosition2().getLng() == null) {

            return ResponseEntity.badRequest().build();
        }

        // Extract positions
        Position pos1 = request.getPosition1();
        Position pos2 = request.getPosition2();

        // Validate coordinates
        if (!isValidCoordinate(pos1.getLat(), pos1.getLng()) ||
                !isValidCoordinate(pos2.getLat(), pos2.getLng())) {
            return ResponseEntity.badRequest().body(null);
        }

        // Check if the two positions are close
        boolean close = arePositionsClose(pos1, pos2);
        return ResponseEntity.ok(close);
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest request) {
        // Check for null values in the request
        if (request == null ||
                request.getStart() == null ||
                request.getStart().getLat() == null ||
                request.getStart().getLng() == null ||
                request.getAngle() == null) {
            return ResponseEntity.badRequest().build();
        }


        try {
            if (!isValidCoordinate(request.getStart().getLat(), request.getStart().getLng())) {
                return ResponseEntity.badRequest().body(null);
            }
            // Get the starting position and angle
            Position start = request.getStart();
            double angle = request.getAngle();
            if (!(0 <= angle & angle <= 360)) {
                return ResponseEntity.badRequest().body(null);
            }

            // Convert angle to radians
            double angleInRadians = Math.toRadians(angle);


            double distance = 0.00015;
            double newLat = start.getLat() + (distance * Math.sin(angleInRadians));
            double newLng = start.getLng() + (distance * Math.cos(angleInRadians));
            newLat = round(newLat, 6);
            newLng = round(newLng, 6);

            // Create new LngLat object
            LngLat nextPosition = new LngLat(newLng, newLat);

            return ResponseEntity.ok(nextPosition);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/isInRegion")
    public ResponseEntity<Object> isInRegion(@RequestBody RegionRequest request) {
        // Check for null values in the request

        if (request == null || request.getPosition() == null ||
                request.getPosition().getLat() == null ||
                request.getPosition().getLng() == null ||
                request.getRegion() == null ||
                request.getRegion().getVertices() == null ||
                request.getRegion().getVertices().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ArrayList<Position> vertices = request.getRegion().getVertices();

        if (vertices.size() < 3) {
            return ResponseEntity.badRequest().build();
        }


        try {

            if (!isPolygonClosed(vertices)) {
                return ResponseEntity.badRequest().body("Invalid polygon: the polygon is not closed!");
            }
            if (arePointsCollinear(vertices)) {
                return ResponseEntity.badRequest().body("Invalid request: Collinear points"); // Points are collinear, invalid region
            }

            // Extract the position to check
            Position point = request.getPosition();
            boolean isInside = isPointInPolygon(point, vertices);

            return ResponseEntity.ok(isInside);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody Order order) {
        OrderValidationResult result = new OrderValidationResult();
        try {
            if (order == null || order.getCreditCardInformation() == null
                    || order.getPizzasInOrder() == null || order.getOrderDate() == null)
                return ResponseEntity.badRequest().build();
            var card = order.getCreditCardInformation();

            if (card.getCreditCardNumber() == null
                    || card.getCreditCardExpiry() == null
                    || card.getCvv() == null)
                return ResponseEntity.badRequest().build();

            if (order.getPizzasInOrder().isEmpty())
                return invalid(result, OrderValidationCode.EMPTY_ORDER);

            if (!isValidExpiry(card.getCreditCardExpiry()))
                return invalid(result, OrderValidationCode.EXPIRY_DATE_INVALID);

            if (!card.getCreditCardNumber().matches("\\d{16}"))
                return invalid(result, OrderValidationCode.CARD_NUMBER_INVALID);

            if (!card.getCvv().matches("\\d{3}"))
                return invalid(result, OrderValidationCode.CVV_INVALID);

            if (order.getPizzasInOrder().size() > SystemConstants.MAX_PIZZAS_PER_ORDER)
                return invalid(result, OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);

            LocalDate orderDate;
            try {
                orderDate = LocalDate.parse(order.getOrderDate());
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }

            DayOfWeek day = orderDate.getDayOfWeek();
            String restaurantName = null;

            for (Order.Pizza pizza : order.getPizzasInOrder()) {
                Restaurant r = findRestaurantByPizza(pizza.getName());

                if (r == null)
                    return invalid(result, OrderValidationCode.PIZZA_NOT_DEFINED);

                if (!isPriceValid(pizza, r))
                    return invalid(result, OrderValidationCode.PRICE_FOR_PIZZA_INVALID);

                if (restaurantName == null)
                    restaurantName = r.getName();
                else if (!restaurantName.equals(r.getName()))
                    return invalid(result, OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

                if (!r.getOpeningDays().contains(day.toString()))
                    return invalid(result, OrderValidationCode.RESTAURANT_CLOSED);
            }

            int expectedTotal = order.getPizzasInOrder().stream()
                    .mapToInt(Order.Pizza::getPriceInPence)
                    .sum() + SystemConstants.ORDER_CHARGE_IN_PENCE;

            if (expectedTotal != order.getPriceTotalInPence())
                return invalid(result, OrderValidationCode.TOTAL_INCORRECT);

            result.setOrderStatus(OrderStatus.VALID);
            result.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.setOrderStatus(OrderStatus.INVALID);
            result.setOrderValidationCode(OrderValidationCode.GENERIC_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private ResponseEntity<OrderValidationResult> invalid(
            OrderValidationResult result, OrderValidationCode code) {
        result.setOrderStatus(OrderStatus.INVALID);
        result.setOrderValidationCode(code);
        return ResponseEntity.ok(result);
    }

    private boolean isValidExpiry(String expiry) {
        if (!expiry.matches("\\d{2}/\\d{2}")) return false;
        YearMonth exp = YearMonth.parse(expiry, DateTimeFormatter.ofPattern("MM/yy"));
        return !exp.isBefore(YearMonth.now());
    }



    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<List<LngLat>> calcDeliveryPath(@RequestBody Order order) {
        // Find the restaurant from the order
        String restaurantName = order.getPizzasInOrder().get(0).getName().split(":")[0];
        Restaurant restaurant = RestaurantData.getRestaurants().stream()
                .filter(r -> r.getName().contains(restaurantName))
                .findFirst()
                .orElse(null);

        if (restaurant == null) {
            return ResponseEntity.badRequest().body(null); // Invalid restaurant
        }

        // Start and end positions
        LngLat start = new LngLat(restaurant.getLocation().getLng(), restaurant.getLocation().getLat());
        LngLat end = new LngLat(-3.186874, 55.944494); // Appleton Tower location

        // Fetch no-fly zones and central area
        List<Region> noFlyZones = NoFlyZones.getNoFlyZones();
        Region centralArea = getCentralArea();

        // Calculate the path
        List<LngLat> path = greedyFindPath(start, end, noFlyZones, centralArea, restaurant);

        if (path.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // No valid path found
        }

        return ResponseEntity.ok(path);
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<GeoJsonPath> calcDeliveryPathAsGeoJson(@RequestBody Order order) {
    try {
        List<LngLat> path = calcDeliveryPath(order).getBody();
        GeoJsonPath geoJsonPath = new GeoJsonPath();
        geoJsonPath.setType("LineString");
        ArrayList<ArrayList<Double>> reformattedPath = new ArrayList<ArrayList<Double>>();
        for (LngLat point : path) {
            ArrayList<Double> coordinates = new ArrayList<>();
            coordinates.add(point.getLng());
            coordinates.add(point.getLat());
            reformattedPath.add(coordinates);
        }
        geoJsonPath.setCoordinates(reformattedPath);
        return ResponseEntity.ok(geoJsonPath);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
    }

    public List<LngLat> greedyFindPath(LngLat start, LngLat end, List<Region> noFlyZones, Region centralArea, Restaurant restaurant) {
        List<LngLat> path = new ArrayList<>();
        LngLat current = start;
        LngLat temporaryEnd = new LngLat(-3.190610529616322, 55.94415998981171);
        LngLat temporaryEnd2 = new LngLat(-3.1903, 55.9433);
        boolean useTemporaryEnd = restaurant.getName().equals("R2") || restaurant.getName().equals("R4");
        boolean useTemporaryEnd2 = restaurant.getName().equals("R2");
        int totalMoves = 0;
        Set<LngLat> visited = new HashSet<>();
        //PriorityQueue<LngLat> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> heuristic(node, end)));
        //openSet.add(start);

        while (!current.isCloseTo(end)) {

            if (useTemporaryEnd && current.isCloseTo(temporaryEnd)) {
                useTemporaryEnd = false;
                end = new LngLat(-3.186874, 55.944494); // Appleton Tower coordinates
                continue;

            }

            // Set the active endpoint (temporary or final)
            LngLat activeEnd = useTemporaryEnd ? temporaryEnd : end;
            List<LngLat> neighbors = generateAllPossibleMoves(current, noFlyZones);
            LngLat bestNeighbor = null;
            double bestDistance = Double.MAX_VALUE;

            for (LngLat neighbor : neighbors) {
                // Check if the move is valid (avoids no-fly zones and respects constraints)
                if (isInNoFlyZoneOrCloseToBorder(neighbor, noFlyZones, restaurant)) {
                    continue;
                }
                if (isInRegion2(current, centralArea) && !isInRegion2(neighbor, centralArea)){
                    continue;
                }

                // Heuristic: Choose the neighbor closest to the end
                double distance = neighbor.distanceTo(activeEnd);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestNeighbor = neighbor;
                }
                totalMoves++;
            }

            // No valid moves available
            if (bestNeighbor == null) {
                return Collections.emptyList(); // Return empty path if stuck
            }

            // Add the best neighbor to the path
            path.add(bestNeighbor);
            current = bestNeighbor;
        }

        return path;
    }

    public boolean isInNoFlyZoneOrCloseToBorder(LngLat position, List<Region> noFlyZones, Restaurant restaurant) {
        boolean isInsideOrClose = false;
        double threshold = 0.00005; // Threshold distance to the border
        if (restaurant.getName().equals("R1")){
            threshold = 0.00015;
        }


        for (Region noFlyZone : noFlyZones) {
            RegionRequest region = new RegionRequest();

            region.setLat(position.getLat());
            region.setLng(position.getLng());
            region.setRegion(noFlyZone);

            // Check if the position is inside the region
            if (isInRegion(region).equals(true)) {
                isInsideOrClose = true;
                break;
            }

            // Check if the position is close to the border
            if (isCloseToRegionBorder(position, noFlyZone, threshold)) {
                isInsideOrClose = true;
                break;
            }
        }
        return isInsideOrClose;
    }

    // Helper function to check proximity to region border
    private boolean isCloseToRegionBorder(LngLat position, Region region, double threshold) {
        // Get the vertices of the region as a list of Positions
        List<Position> vertices = region.getVertices();

        // Convert the Positions to LngLat objects
        List<LngLat> lngLatVertices = new ArrayList<>();
        for (Position vertex : vertices) {
            lngLatVertices.add(new LngLat(vertex.getLng(), vertex.getLat()));
        }

        // Iterate through each edge of the region
        for (int i = 0; i < lngLatVertices.size(); i++) {
            LngLat start = lngLatVertices.get(i);
            LngLat end = lngLatVertices.get((i + 1) % lngLatVertices.size()); // Wrap around to form a closed loop

            // Calculate the shortest distance from the position to this edge
            double distance = calculateDistanceToEdge(position, start, end);

            // Check if the distance is within the threshold
            if (distance <= threshold) {
                return true;
            }
        }
        return false;
    }

    // Calculate the shortest distance from a point to a line segment (edge)
    private double calculateDistanceToEdge(LngLat point, LngLat start, LngLat end) {
        double px = point.getLng();
        double py = point.getLat();
        double x1 = start.getLng();
        double y1 = start.getLat();
        double x2 = end.getLng();
        double y2 = end.getLat();

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            // The start and end points are the same
            return calculateEuclideanDistance(px, py, x1, y1);
        }

        // Project the point onto the line segment, clamping to the segment's endpoints
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        // Find the closest point on the segment
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        // Calculate the distance to the closest point
        return calculateEuclideanDistance(px, py, closestX, closestY);
    }

    private Region getGeorgeSquareRegion(List<Region> noFlyZones) {
        for (Region region : noFlyZones) {
            if (region.getName().equalsIgnoreCase("George Square Area")) {
                return region;
            }
        }
        throw new IllegalArgumentException("George Square Area not found in no-fly zones.");
    }


    public List<LngLat> generateAllPossibleMoves(LngLat current, List<Region> noFlyZones) {
        double step = SystemConstants.DRONE_MOVE_DISTANCE; // Distance step
        List<LngLat> moves = new ArrayList<>();
        /***
        if (isInRegion2(current, getCentralArea())){
            step = SystemConstants.DRONE_MOVE_DISTANCE*100;
        }***/

        for (double angle = 0; angle < 360; angle += 22.5) { // Increment by 45 degrees
            double radian = Math.toRadians(angle);
            double newLat = current.getLat() + (step * Math.sin(radian));
            double newLng = current.getLng() + (step * Math.cos(radian));
            moves.add(new LngLat(newLng, newLat));
        }


        return moves;
    }

    private boolean arePointsCollinear(List<Position> vertices) {
        if (vertices.size() < 3) return true; // Less than 3 points are always considered collinear

        Position p0 = vertices.get(0);
        Position p1 = vertices.get(1);
        double dx = p1.getLng() - p0.getLng();
        double dy = p1.getLat() - p0.getLat();

        for (int i = 2; i < vertices.size(); i++) {
            Position pi = vertices.get(i);
            double dxi = pi.getLng() - p0.getLng();
            double dyi = pi.getLat() - p0.getLat();

            // Check if the cross product is zero (which indicates collinearity)
            if (dy * dxi != dx * dyi) {
                return false; // Not collinear
            }
        }
        return true; // All points are collinear
    }

    private boolean isPolygonClosed(List<Position> vertices) {
        if (vertices.size() < 3) {
            return false; // A polygon cannot be formed with less than 3 vertices
        }

        LngLat first = new LngLat(vertices.get(0).getLng(), vertices.get(0).getLat());
        LngLat last = new LngLat(vertices.get(vertices.size() - 1).getLng(), vertices.get(vertices.size() - 1).getLat());

        // A polygon is closed if the first vertex equals the last vertex
        return first.equals(last);
    }


    private boolean isPointInPolygon(Position point, List<Position> vertices) {
        int n = vertices.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Position v1 = vertices.get(i);
            Position v2 = vertices.get(j);
            if (isPointOnSegment(point, v1, v2)) {
                return true; // Point is on the edge
            }

            if ((v1.getLat() > point.getLat()) != (v2.getLat() > point.getLat()) &&
                    point.getLng() < (v2.getLng() - v1.getLng()) * (point.getLat() - v1.getLat()) / (v2.getLat() - v1.getLat()) + v1.getLng()) {
                inside = !inside;
            }
        }
        return inside;
    }
    private boolean isPointOnSegment(Position point, Position v1, Position v2) {
        double crossProduct = (point.getLat() - v1.getLat()) * (v2.getLng() - v1.getLng()) - (point.getLng() - v1.getLng()) * (v2.getLat() - v1.getLat());
        if (Math.abs(crossProduct) > 1e-10) return false; // Not collinear

        double dotProduct = (point.getLng() - v1.getLng()) * (v2.getLng() - v1.getLng()) + (point.getLat() - v1.getLat()) * (v2.getLat() - v1.getLat());
        if (dotProduct < 0) return false; // Point is outside the segment

        double squaredLength = (v2.getLng() - v1.getLng()) * (v2.getLng() - v1.getLng()) + (v2.getLat() - v1.getLat()) * (v2.getLat() - v1.getLat());
        return dotProduct <= squaredLength; // Point is within the segment
    }
    public boolean isInRegion2(LngLat point, Region region) {
        if (region.getVertices().size() < 3) {
            throw new IllegalArgumentException("Region must have at least 3 vertices.");
        }

        if (!isPolygonClosed2(region.getVertices())) {
            throw new IllegalArgumentException("Polygon is not closed.");
        }

        if (arePointsCollinear2(region)) {
            throw new IllegalArgumentException("Region vertices are collinear.");
        }

        return isPointInPolygon2(point, region);
    }
    private boolean arePointsCollinear2(Region region) {
        if (region.getVertices().size() < 3) return true; // Less than 3 points are always considered collinear

        Position p0 = region.getVertices().get(0);
        Position p1 = region.getVertices().get(1);
        double dx = p1.getLng() - p0.getLng();
        double dy = p1.getLat() - p0.getLat();

        for (int i = 2; i < region.getVertices().size(); i++) {
            Position pi = region.getVertices().get(i);
            double dxi = pi.getLng() - p0.getLng();
            double dyi = pi.getLat() - p0.getLat();

            // Check if the cross product is zero (which indicates collinearity)
            if (dy * dxi != dx * dyi) {
                return false; // Not collinear
            }
        }
        return true; // All points are collinear
    }

    private boolean isPolygonClosed2(List<Position> vertices) {
        if (vertices.size() < 3) {
            return false; // A polygon cannot be formed with less than 3 vertices
        }

        LngLat first = new LngLat(vertices.get(0).getLng(), vertices.get(0).getLat());
        LngLat last = new LngLat(vertices.get(vertices.size() - 1).getLng(), vertices.get(vertices.size() - 1).getLat());

        // A polygon is closed if the first vertex equals the last vertex
        return first.equals(last);
    }


    private boolean isPointInPolygon2(LngLat point, Region region) {
        int n = region.getVertices().size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Position v1 = region.getVertices().get(i);
            Position v2 = region.getVertices().get(j);
            if (isPointOnSegment2(point, v1, v2)) {
                return true; // Point is on the edge
            }

            if ((v1.getLat() > point.getLat()) != (v2.getLat() > point.getLat()) &&
                    point.getLng() < (v2.getLng() - v1.getLng()) * (point.getLat() - v1.getLat()) / (v2.getLat() - v1.getLat()) + v1.getLng()) {
                inside = !inside;
            }
        }
        return inside;
    }
    private boolean isPointOnSegment2(LngLat point, Position v1, Position v2) {
        double crossProduct = (point.getLat() - v1.getLat()) * (v2.getLng() - v1.getLng()) - (point.getLng() - v1.getLng()) * (v2.getLat() - v1.getLat());
        if (Math.abs(crossProduct) > 1e-10) return false; // Not collinear

        double dotProduct = (point.getLng() - v1.getLng()) * (v2.getLng() - v1.getLng()) + (point.getLat() - v1.getLat()) * (v2.getLat() - v1.getLat());
        if (dotProduct < 0) return false; // Point is outside the segment

        double squaredLength = (v2.getLng() - v1.getLng()) * (v2.getLng() - v1.getLng()) + (v2.getLat() - v1.getLat()) * (v2.getLat() - v1.getLat());
        return dotProduct <= squaredLength; // Point is within the segment
    }


    private Restaurant findRestaurantByPizza(String pizzaName) {
        return RestaurantData.getRestaurants().stream()
                .filter(r -> r.getMenu().stream().anyMatch(p -> p.getName().equalsIgnoreCase(pizzaName)))
                .findFirst()
                .orElse(null);
    }

    private boolean isPriceValid(Order.Pizza pizza, Restaurant restaurant) {
        return restaurant.getMenu().stream()
                .anyMatch(menuItem -> menuItem.getName().equals(pizza.getName()) && menuItem.getPriceInPence() == pizza.getPriceInPence());
    }

    // Method to check if two positions are close
    private boolean arePositionsClose(Position pos1, Position pos2) {
        double latDiff = Math.abs(pos1.getLat() - pos2.getLat());
        double lngDiff = Math.abs(pos1.getLng() - pos2.getLng());
        return (latDiff < 0.00015) && (lngDiff < 0.00015);
    }

    public double calculateEuclideanDistance(double lat1, double lng1, double lat2, double lng2) {
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lng2 - lng1, 2));
    }
    public boolean isValidCoordinate(Double lat, Double lng) {
        return (lat >= -90.0 && lat <= 90.0) && (lng >= -180.0 && lng <= 180.0);
    }
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}

