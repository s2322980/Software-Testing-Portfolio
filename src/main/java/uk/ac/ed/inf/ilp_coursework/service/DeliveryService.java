package uk.ac.ed.inf.ilp_coursework.service;

import lombok.Setter;
import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.ilp_coursework.data.LngLat;
import uk.ac.ed.inf.ilp_coursework.data.Position;
import uk.ac.ed.inf.ilp_coursework.data.Region;
import uk.ac.ed.inf.ilp_coursework.data.RegionRequest;

import java.util.*;

@Service
public class DeliveryService {

    public List<LngLat> aStarFindPath(LngLat start, LngLat end, List<Region> noFlyZones, Region centralArea) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        Map<LngLat, Node> allNodes = new HashMap<>();
        Set<LngLat> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);
        System.out.println("Before loop");

        //LngLat neighbortest = new LngLat(-3.19128692150116, 55.9455351525177); // Replace with a known value
        //boolean isValid = isValidMove(neighbortest, noFlyZones, centralArea, List.of(start));
        //System.out.println("Test Neighbor: " + neighbortest + ", Is valid: " + isValid);

        while (!openSet.isEmpty()) {
            System.out.println("In loop");
            Node current = openSet.poll();
            closedSet.add(current.getPosition());
            System.out.println(current);

            // If the destination is reached
            System.out.println(arePositionsClose(current.getPosition(), end));
            if (current.getPosition().isCloseTo(end)) {
                System.out.println(current.getPosition());
                System.out.println(end);
                return reconstructPath2(current);
            }

            for (LngLat neighbor : generateAllPossibleMoves(current.getPosition())) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                if (!isValidMove(neighbor, noFlyZones, centralArea, List.of(start))) {
                    System.out.println("Not valid move");
                    continue;
                }
                if (!isInRegion(neighbor.toPosition(), centralArea.getVertices())) {
                    continue; // Skip invalid moves
                }

                double tentativeG = current.getG() + current.getPosition().distanceTo(neighbor);
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));
                //System.out.println("NeighborNode " + neighborNode);
                //System.out.println("tentativeG: " + tentativeG + ", neighborNode.getG(): " + neighborNode.getG());

                if (tentativeG < neighborNode.getG()) {
                    //System.out.println("tentativeG < neighborNode.getG()");
                    neighborNode.setParent(current);
                    neighborNode.setG(tentativeG);
                    neighborNode.setH(heuristic(neighbor, end));
                    allNodes.put(neighbor, neighborNode);

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                    //openSet.add(neighborNode);
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }

    private List<LngLat> reconstructPath2(Node node) {
        List<LngLat> path = new ArrayList<>();
        while (node != null) {
            System.out.println("Node " + node);
            path.add(node.getPosition());
            node = node.getParent();
        }
        Collections.reverse(path);
        System.out.println("Path" + path);
        return path;
    }

    private double heuristic(LngLat a, LngLat b) {
        // Use Euclidean distance as the heuristic
        return Math.sqrt(Math.pow(a.getLat() - b.getLat(), 2) + Math.pow(a.getLng() - b.getLng(), 2));
    }

    public List<LngLat> generateAllPossibleMoves(LngLat current) {
        double step = 0.00015; // Distance step
        List<LngLat> moves = new ArrayList<>();

        for (int angle = 0; angle < 360; angle += 45) { // Increment by 45 degrees
            double radian = Math.toRadians(angle);
            double newLat = current.getLat() + (step * Math.sin(radian));
            double newLng = current.getLng() + (step * Math.cos(radian));
            moves.add(new LngLat(newLng, newLat));
        }
        System.out.println(moves);

        return moves;
    }
    private boolean isValidMove(LngLat position, List<Region> noFlyZones, Region centralArea, List<LngLat> path) {
        Position pos = position.toPosition();
        System.out.println("Is path empty " + path);

        // Check if inside no-fly zones
        //System.out.println("Restaurant Position: " + restaurant.getLocation());
        for (Region noFlyZone : noFlyZones) {
            if (false){ //(isPointInPolygon(pos, noFlyZone.getVertices())) {
                //boolean isInZone = isInRegion(restaurant.getLocation().toPosition(), noFlyZone.getVertices());
                //System.out.println("Restaurant in no-fly zone: " + isInZone);
                System.out.println("No-fly zone vertices: " + noFlyZone.getVertices());
                System.out.println("Next in no fly zone");
                return false;
            }
        }

        // Check if the drone is already inside the central area
        if (!path.isEmpty()) {
            Position currentPos = path.get(path.size() - 1).toPosition(); // Current drone position
            System.out.println("Currently in central " + isInRegion(currentPos, centralArea.getVertices()));
            System.out.println("Next pos in central " + isInRegion(pos, centralArea.getVertices()));

            // If the drone is inside the central area, ensure the next position stays inside
            if (isInRegion(currentPos, centralArea.getVertices()) && !isInRegion(pos, centralArea.getVertices())) {
                return false; // Invalid move if the drone leaves the central area
            }
        }

        return true;
    }



    /***
    private boolean isValidMove(LngLat position, List<Region> noFlyZones, Region centralArea, List<LngLat> path) {
        Position pos = position.toPosition();

        // Check if inside no-fly zones
        for (Region noFlyZone : noFlyZones) {
            System.out.println("Check no fly zones");
            if (isPointInPolygon(pos, noFlyZone.getVertices())) {
                System.out.println("Point in no fly zone");
                return false;
            }
        }

        // If already inside the central area, ensure we stay within it
        if (!path.isEmpty() && isPointInPolygon(path.get(0).toPosition(), centralArea.getVertices())) {
            System.out.println("path not empty, point in central area");
            if (!isPointInPolygon(pos, centralArea.getVertices())) {
                System.out.println("Next point not in central area, while current is");
                return false;
            }
        }

        return true;
    }***/

    public boolean isInRegion(Position point, List<Position> vertices) {
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Region must have at least 3 vertices.");
        }

        if (!isPolygonClosed(vertices)) {
            throw new IllegalArgumentException("Polygon is not closed.");
        }

        if (arePointsCollinear(vertices)) {
            throw new IllegalArgumentException("Region vertices are collinear.");
        }

        return isPointInPolygon(point, vertices);
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
    private boolean arePositionsClose(LngLat pos1, LngLat pos2) {
        double latDiff = Math.abs(pos1.getLat() - pos2.getLat());
        double lngDiff = Math.abs(pos1.getLng() - pos2.getLng());
        return (latDiff < 0.00015) && (lngDiff < 0.00015);
    }


    // Node class for A* algorithm
    @Getter
    public static class Node {
        private LngLat position;
        @Setter
        private Node parent;
        @Setter
        private double g; // Cost from start to this node
        @Setter
        private double h; // Heuristic cost to goal

        public Node(LngLat position) {
            this.position = position;
            this.g = Double.MAX_VALUE; // Initialize to "infinity"
            this.h = 0;
        }

        public Node(LngLat position, Node parent, double g, double h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }
        public LngLat getPosition() {
            return position;
        }

        public Node getParent() {
            return parent;
        }


        public double getG() {
            return g;
        }

        public double getH() {
            return h;
        }


        public double getF() {
            return g + h;
        }
    }
}

