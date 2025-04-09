package fr.univtours.models.solutions;

import fr.univtours.Instance;
import fr.univtours.models.Hotel;
import fr.univtours.models.Node;
import fr.univtours.models.Route;
import fr.univtours.models.Site;
import fr.univtours.utils.Pair;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RandomSolution {

    private Instance instance;
    private List<Site> sites = new ArrayList<>();
    private List<Hotel> hotels;
    private Random random = new Random();
    private List<Route> routes = new ArrayList<>();
    private Set<Site> visited = new HashSet<>();

    public RandomSolution(Instance instance) {
        this.instance = instance;
        this.hotels = instance.getAllHotels();
        this.sites = instance.getAllSites();
        this.routes.clear();
        this.visited.clear();
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

                visited.add((Site) n);
                currentLocation = n;
            }
            routes.add(dailyRoute);
        }

        return new SolutionResult(routes, getTotalScore(routes));
    }

    private List<Site> getHighestSites(Node from, List<Site> visited) {
        return sites.stream()
                .filter(n -> !visited.contains(n))
                .sorted(Comparator.comparing(n -> n.getScore() / instance.getDistanceBetween(from.getId(), n.getId())))
                .toList().reversed();
    }

    private Node getClosestHotel(Node from, double remainingDistance) {
        List<Hotel> sortedHotels = hotels.stream()
                .sorted(Comparator.comparing(n -> instance.getDistances()[from.getId()][n.getId()])).toList();
        for (Node hotel : sortedHotels) {
            if (instance.getDistances()[from.getId()][hotel.getId()] < remainingDistance) {
                return hotel;
            }
        }

        return null;
    }

    private Node getHighestNode(Node from, double remainingDistance, boolean lastDay) {
        List<Site> candidates = getHighestSites(from, new ArrayList<>(visited));

        List<Site> reachableCandidates = new ArrayList<>();
        for (Site candidate : candidates) {
            double distance = instance.getDistanceBetween(from.getId(), candidate.getId());
            if (distance <= remainingDistance) {
                if (lastDay) {
                    if (distance + instance.getDistances()[candidate.getId()][instance.getLast().getId()] <= remainingDistance) {
                        reachableCandidates.add(candidate);
                    }
                } else {
                    List<Hotel> sortedHotels = hotels.stream()
                            .sorted(Comparator.comparing(n -> instance.getDistances()[candidate.getId()][n.getId()])).toList();

                    for (Node hotel : sortedHotels) {
                        if (distance + instance.getDistances()[candidate.getId()][hotel.getId()] <= remainingDistance) {
                            reachableCandidates.add(candidate);
                        }
                    }
                }

            }
        }

        if (reachableCandidates.isEmpty()) {
            return null;
        }

        return reachableCandidates.get(new Random().nextInt(reachableCandidates.size()));
    }


    private double getTotalScore(List<Route> routes) {
        return routes.stream().mapToDouble(Route::getScore).sum();
    }

}
