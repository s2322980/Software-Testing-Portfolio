package uk.ac.ed.inf.ilp_coursework.data;

import java.util.ArrayList;
import java.util.List;

public class NoFlyZones {
    public static List<Region> getNoFlyZones() {
        List<Region> noFlyZones = new ArrayList<>();

        Region georgeSquare = new Region();
        georgeSquare.setName("George Square Area");
        georgeSquare.setVertices(new ArrayList<>(List.of(
                new Position(-3.190578818321228, 55.94402412577528),
                new Position(-3.1899887323379517, 55.94284650540911),
                new Position(-3.187097311019897, 55.94328811724263),
                new Position(-3.187682032585144, 55.944477740393744),
                new Position(-3.190578818321228, 55.94402412577528)
        )));

        noFlyZones.add(georgeSquare);

        // Dr Elsie Inglis Quadrangle
        Region elsieInglis = new Region();
        elsieInglis.setName("Dr Elsie Inglis Quadrangle");
        ArrayList<Position> elsieInglisVertices = new ArrayList<>();
        elsieInglisVertices.add(new Position(-3.19071829319, 55.9451957023404));
        elsieInglisVertices.add(new Position(-3.19061636924744, 55.9449824179636));
        elsieInglisVertices.add(new Position(-3.19002628326416, 55.9450755422726));
        elsieInglisVertices.add(new Position(-3.19013357162476, 55.945297838105));
        elsieInglisVertices.add(new Position(-3.19071829319, 55.9451957023404));
        elsieInglis.setVertices(elsieInglisVertices);
        noFlyZones.add(elsieInglis);

        // Bristo Square Open Area
        Region bristoSquare = new Region();
        bristoSquare.setName("Bristo Square Open Area");
        ArrayList<Position> bristoSquareVertices = new ArrayList<>();
        bristoSquareVertices.add(new Position(-3.18954348564148, 55.9455231366331));
        bristoSquareVertices.add(new Position(-3.18938255310059, 55.9455321485469));
        bristoSquareVertices.add(new Position(-3.1892591714859, 55.9454480372693));
        bristoSquareVertices.add(new Position(-3.18920016288757, 55.9453368899437));
        bristoSquareVertices.add(new Position(-3.18919479846954, 55.9451957023404));
        bristoSquareVertices.add(new Position(-3.18913578987122, 55.9451175983387));
        bristoSquareVertices.add(new Position(-3.18813800811768, 55.9452738061846));
        bristoSquareVertices.add(new Position(-3.18855106830597, 55.9461059027456));
        bristoSquareVertices.add(new Position(-3.18953812122345, 55.9455591842759));
        bristoSquareVertices.add(new Position(-3.18954348564148, 55.9455231366331));
        bristoSquare.setVertices(bristoSquareVertices);
        noFlyZones.add(bristoSquare);

        // Bayes Central Area
        Region bayesCentral = new Region();
        bayesCentral.setName("Bayes Central Area");
        ArrayList<Position> bayesCentralVertices = new ArrayList<>();
        bayesCentralVertices.add(new Position(-3.1876927614212, 55.9452069673277));
        bayesCentralVertices.add(new Position(-3.18755596876144, 55.9449621408666));
        bayesCentralVertices.add(new Position(-3.18698197603226, 55.9450567672283));
        bayesCentralVertices.add(new Position(-3.18723276257515, 55.9453699337766));
        bayesCentralVertices.add(new Position(-3.18744599819183, 55.9453361389472));
        bayesCentralVertices.add(new Position(-3.18737357854843, 55.9451934493426));
        bayesCentralVertices.add(new Position(-3.18759351968765, 55.9451566503593));
        bayesCentralVertices.add(new Position(-3.18762436509132, 55.9452197343093));
        bayesCentralVertices.add(new Position(-3.1876927614212, 55.9452069673277));
        bayesCentral.setVertices(bayesCentralVertices);
        noFlyZones.add(bayesCentral);

        return noFlyZones;
    }

}
