package fr.univtours;

import fr.univtours.models.Hotel;
import fr.univtours.models.HotelType;
import fr.univtours.models.Node;
import fr.univtours.models.Site;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("\t");

                switch (lineNbr) {
                    case 1:
                        nbrSites = Integer.parseInt(split[0]);
                        nbrMidHotels = Integer.parseInt(split[1]);
                        nbrDays = Integer.parseInt(split[2]);
                        travelDistances = new double[nbrDays];
                        nodes = new Node[nbrSites];
                        distances = new double[nbrSites][nbrSites];
                        break;
                    case 2:
                        for (int i = 0; i < nbrDays; i++) {
                            travelDistances[i] = Double.parseDouble(split[i]);
                        }
                        break;
                    case 4:
                        tempSites.add(new Hotel(
                                HotelType.START,
                                Double.parseDouble(split[0]),
                                Double.parseDouble(split[1]),
                                Double.parseDouble(split[2])
                        ));
                        break;

                    case 5:
                        tempSites.add(new Hotel(
                                HotelType.END,
                                Double.parseDouble(split[0]),
                                Double.parseDouble(split[1]),
                                Double.parseDouble(split[2])
                        ));
                        break;

                    default:
                        if (split.length == 3) {
                            if (split[2].equals("0")) {
                                tempSites.add(new Hotel(
                                        HotelType.INTERMEDIATE,
                                        Double.parseDouble(split[0]),
                                        Double.parseDouble(split[1]),
                                        Double.parseDouble(split[2])
                                ));
                            } else {
                                tempSites.add(new Site(
                                        Double.parseDouble(split[0]),
                                        Double.parseDouble(split[1]),
                                        Double.parseDouble(split[2])
                                ));
                            }
                        }
                        break;
                }

                lineNbr++;
            }

            tempSites.toArray(nodes);
            for(int i = 0; i < nbrSites; i++){
                for(int j = 0; j < nbrSites; j++){
                    distances[i][j] = Math.sqrt(Math.pow(nodes[i].getX() - nodes[j].getX(), 2) + Math.pow(nodes[i].getY() - nodes[j].getY(), 2));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
