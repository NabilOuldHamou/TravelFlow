package fr.univtours.models.solutions;

import fr.univtours.Instance;
import fr.univtours.models.*;
import fr.univtours.utils.Pair;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GreedySolution {

    private Instance instance;
    private List<Route> routes = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private List<Node> visited = new ArrayList<>();
    private List<Hotel> hotels = new ArrayList<>();

    public GreedySolution(Instance instance) {
        this.instance = instance;
        this.nodes.addAll(Arrays.asList(instance.getNodes()));
        this.hotels = nodes.stream().filter(n -> n instanceof Hotel).map(n -> (Hotel) n).toList();
    }

    public SolutionResult solve() {
        Node currentLocation = instance.getFirst();
        for (int i = 0; i < instance.getNbrDays(); i++) {
            boolean lastDay = (i == instance.getNbrDays() - 1);
            double remainingDistance = instance.getTravelDistances()[i];
            Route dailyRoute = new Route(i, (Hotel) currentLocation, null, instance.getTravelDistances()[i]);

            while (remainingDistance > 0) {
                if (currentLocation == null) break;
                Node n = getHighestNode(currentLocation, remainingDistance, lastDay);
                if (n == null) {
                    Hotel hotel = lastDay ? instance.getLast() : (Hotel) getClosestHotel(currentLocation, remainingDistance);
                    dailyRoute.setLastNode(hotel);
                    currentLocation = hotel;
                    break;
                };

                double distance = instance.getDistances()[currentLocation.getId()][n.getId()];
                dailyRoute.addSite((Site) n);
                remainingDistance -= distance;
                dailyRoute.addParcouru(distance);

                visited.add(n);
                currentLocation = n;
            }
            routes.add(dailyRoute);
        }

        double sum = routes.stream().mapToDouble(Route::getScore).sum();
        return new SolutionResult(routes, sum);
    }


    public void recalculateParcouru(Route route) {
        double total = 0;
        Node current = route.getFirstNode();

        for (Site s : route.getSites()) {
            total += instance.getDistanceBetween(current, s);
            current = s;
        }

        total += instance.getDistanceBetween(current, route.getLastNode());
        route.setParcouru(total);
    }


    public double getTotalScore(List<Route> routes) {
        return routes.stream().mapToDouble(Route::getScore).sum();
    }


    public boolean checkRoutesValidity(List<Route> routes) {
        for (Route route : routes) {
            List<Site> visited = new ArrayList<>();
            if (route.getFirstNode() == null || route.getLastNode() == null) {
                return false;
            }

            if (route.getParcouru() > route.getDistance()) {
                return false;
            }

            Node prevNode = route.getFirstNode();
            double totalDistance = 0;

            for (Site site : route.getSites()) {
                if (!visited.contains(site)) {
                    visited.add(site);
                } else {
                    return false;
                }
                double segmentDistance = instance.getDistances()[prevNode.getId()][site.getId()];
                totalDistance += segmentDistance;

                if (totalDistance > route.getDistance()) {
                    return false;
                }
                prevNode = site;
            }

            double finalLegDistance = instance.getDistances()[prevNode.getId()][route.getLastNode().getId()];
            totalDistance += finalLegDistance;

            if (totalDistance > route.getDistance()) {
                return false;
            }

            if (route.getNbSiteVisite() != route.getSites().size()) {
                return false;
            }
        }

        return true;
    }

    public boolean checkRouteValidity(Route route) {
        List<Site> visited = new ArrayList<>();
        if (route.getFirstNode() == null || route.getLastNode() == null) {
            return false;
        }

        if (route.getParcouru() > route.getDistance()) {
            return false;
        }

        Node prevNode = route.getFirstNode();
        double totalDistance = 0;

        for (Site site : route.getSites()) {
            if (!visited.contains(site)) {
                visited.add(site);
            } else {
                return false;
            }
            double segmentDistance = instance.getDistances()[prevNode.getId()][site.getId()];
            totalDistance += segmentDistance;

            if (totalDistance > route.getDistance()) {
                return false;
            }
            prevNode = site;
        }

        double finalLegDistance = instance.getDistances()[prevNode.getId()][route.getLastNode().getId()];
        totalDistance += finalLegDistance;

        if (totalDistance > route.getDistance()) {
            return false;
        }

        if (route.getNbSiteVisite() != route.getSites().size()) {
            return false;
        }

        return true;
    }



    private Node getHighestNode(Node from, double remainingDistance, boolean lastDay) {
        List<Node> sortedSites = nodes.stream().filter(n -> n instanceof Site).filter(n -> !visited.contains(n))
                .sorted(Comparator.comparing(n -> n.getScore() / instance.getDistances()[from.getId()][n.getId()]))
                .toList().reversed();

        for (Node site : sortedSites) {
            double distance = instance.getDistances()[from.getId()][site.getId()];
            if (distance < remainingDistance) {
                if (lastDay) {
                    if (distance + instance.getDistances()[site.getId()][instance.getLast().getId()] <= remainingDistance) {
                        return site;
                    }
                } else {
                    List<Node> hotels = nodes.stream().filter(n-> n instanceof Hotel)
                            .sorted(Comparator.comparing(n -> instance.getDistances()[site.getId()][n.getId()])).toList();

                    for (Node hotel : hotels) {
                        if (distance + instance.getDistances()[site.getId()][hotel.getId()] <= remainingDistance) {
                            return site;
                        }
                    }
                }
            }
        }

        return null;
    }

    private Node getClosestHotel(Node from, double remainingDistance) {
        List<Node> hotels = nodes.stream().filter(n-> n instanceof Hotel)
                .sorted(Comparator.comparing(n -> instance.getDistances()[from.getId()][n.getId()])).toList();
        for (Node hotel : hotels) {
            if (instance.getDistances()[from.getId()][hotel.getId()] < remainingDistance) {
                return hotel;
            }
        }

        return null;
    }

}
