package fr.univtours;

import fr.univtours.models.Hotel;
import fr.univtours.models.HotelType;
import fr.univtours.models.Node;
import fr.univtours.models.Site;
import fr.univtours.utils.Pair;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

@Getter @Setter
public class Instance {

    private int nbrSites;
    private int nbrMidHotels;
    private int nbrDays;

    // Travel distance for each day
    private double[] travelDistances;

    private double[][] distances;

    private Node[] nodes;
    
    private Hotel First;
    private Hotel Last;

    public Instance(String filename) {
        try{
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            List<Node> tempSites = new ArrayList<>();

            int lineNbr = 1;
            int nodeId = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("\t");

                switch (lineNbr) {
                    case 1:
                        nbrSites = Integer.parseInt(split[0]);
                        nbrMidHotels = Integer.parseInt(split[1]);
                        nbrDays = Integer.parseInt(split[2]);
                        travelDistances = new double[nbrDays];
                        break;
                    case 2:
                        for (int i = 0; i < nbrDays; i++) {
                            travelDistances[i] = Double.parseDouble(split[i]);
                        }
                        break;
                    case 4:
                        Hotel HFirst = new Hotel(
                                HotelType.START,
                                nodeId,
                                Double.parseDouble(split[0]),
                                Double.parseDouble(split[1]),
                                Double.parseDouble(split[2])
                        );
                        tempSites.add(HFirst);
                        nodeId ++;
                        this.First = HFirst;
                        break;

                    case 5:
                        Hotel HEnd = new Hotel(
                                HotelType.END,
                                nodeId,
                                Double.parseDouble(split[0]),
                                Double.parseDouble(split[1]),
                                Double.parseDouble(split[2])
                        );
                        tempSites.add(HEnd);
                        nodeId ++;
                        this.Last = HEnd;
                        break;

                    default:
                        if (split.length == 3) {
                            if (split[2].equals("0")) {
                                tempSites.add(new Hotel(
                                        HotelType.INTERMEDIATE,
                                        nodeId,
                                        Double.parseDouble(split[0]),
                                        Double.parseDouble(split[1]),
                                        Double.parseDouble(split[2])
                                ));
                            } else {
                                tempSites.add(new Site(
                                        nodeId,
                                        Double.parseDouble(split[0]),
                                        Double.parseDouble(split[1]),
                                        Double.parseDouble(split[2])
                                ));
                            }
                            nodeId ++;
                        }
                        break;
                }

                lineNbr++;
            }

            nodes = new Node[tempSites.size()];
            tempSites.toArray(nodes);
            distances = new double[tempSites.size()][tempSites.size()];
            for(int i = 0; i < tempSites.size(); i++){
                for(int j = 0; j < tempSites.size(); j++){
                    distances[i][j] = Math.sqrt(Math.pow(nodes[i].getX() - nodes[j].getY(), 2) + Math.pow(nodes[i].getY() - nodes[j].getY(), 2));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Hotel getClosestHotel(int from) {
        return Arrays.stream(nodes)
                .filter(node -> node instanceof Hotel)
                .map(node -> (Hotel) node)
                .min(Comparator.comparingDouble(hotel -> distances[from][hotel.getId()]))
                .orElse(null);
    }

    public Site getClosestSite(int from) {
        return Arrays.stream(nodes)
                .filter(node -> node instanceof Site)
                .map(node -> (Site) node)
                .min(Comparator.comparingDouble(site -> distances[from][site.getId()]))
                .orElse(null);
    }

    public List<Site> getAllSites() {
        return Arrays.stream(nodes)
                .filter(n -> n instanceof Site)
                .map(Site.class::cast)
                .toList();
    }

    public List<Hotel> getAllHotels() {
        return Arrays.stream(nodes)
                .filter(n -> n instanceof Hotel)
                .map(Hotel.class::cast)
                .toList();
    }

    public double getDistanceBetween(List<Integer> nodeIds) {
        double totalDistance = 0.d;
        for (int i = 0; i < nodeIds.size() - 1; i++) {
            int start = nodeIds.get(i);
            int end = nodeIds.get(i + 1);

            totalDistance += distances[start][end];
        }

        return totalDistance;
    }

    public double getDistanceBetween(Node nodeA, Node nodeB) {
        return distances[nodeA.getId()][nodeB.getId()];
    }

    public double getDistanceBetween(int nodeA, int nodeB) {
        return distances[nodeA][nodeB];
    }

    @Override
    public String toString() {
        return "Instance{" +
                "nbrSites=" + nbrSites +
                ", nbrMidHotels=" + nbrMidHotels +
                ", nbrDays=" + nbrDays +
                ", travelDistances=" + Arrays.toString(travelDistances) +
                ", nodes=" + Arrays.toString(nodes) +
                '}';
    }
}
