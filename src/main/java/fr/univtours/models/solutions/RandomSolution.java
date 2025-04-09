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
    private List<Node> nodes = new ArrayList<>();
    private List<Hotel> hotels = new ArrayList<>();
    private Random random = new Random();

    public RandomSolution(Instance instance) {
        this.instance = instance;
        this.nodes.addAll(Arrays.asList(instance.getNodes()));
        this.hotels = nodes.stream()
                .filter(n -> n instanceof Hotel)
                .map(n -> (Hotel) n)
                .collect(Collectors.toList());
    }

    public List<SolutionResult> generateSolutions(int n) {
        List<SolutionResult> solutions = new ArrayList<>();
        int maxAttemptsPerSolution = 100; // safety cap

        while (solutions.size() < n) {
            int attempts = 0;
            List<Route> routes = null;
            while (attempts < maxAttemptsPerSolution) {
                routes = generateSolutionRoutes();
                if (checkRoutesValidity(routes)) {
                    break;
                }
                attempts++;
            }
            if (attempts < maxAttemptsPerSolution && routes != null) {
                double totalScore = routes.stream().mapToDouble(Route::getScore).sum();
                solutions.add(new SolutionResult(routes, totalScore));
            } else {
                System.err.println("Warning: Unable to generate a valid solution after " + maxAttemptsPerSolution + " attempts.");
                break;
            }
        }
        return solutions;
    }

    public SolutionResult optimizeSolution(SolutionResult original) {
        List<Route> optimizedRoutes = Route.deepCopyRoutes(original.routes());

        for (Route route : optimizedRoutes) {
            boolean improved = true;

            while (improved) {
                improved = false;
                List<Site> sites = new ArrayList<>(route.getSites());

                for (int i = 0; i < sites.size(); i++) {
                    for (int j = i + 1; j < sites.size(); j++) {
                        Collections.swap(sites, i, j);

                        Route newRoute = new Route(route.getRouteId(), route.getFirstNode(), route.getLastNode(), route.getDistance());
                        Node current = route.getFirstNode();
                        double totalDistance = 0;

                        for (Site site : sites) {
                            double dist = instance.getDistanceBetween(current, site);
                            totalDistance += dist;
                            if (totalDistance > newRoute.getDistance()) break;
                            newRoute.addSite(site);
                            current = site;
                        }

                        totalDistance += instance.getDistanceBetween(current, route.getLastNode());

                        if (totalDistance <= newRoute.getDistance()) {
                            newRoute.setParcouru(totalDistance);
                            if (newRoute.getScore() > route.getScore()) {
                                route.setSites(newRoute.getSites());
                                route.setParcouru(newRoute.getParcouru());
                                improved = true;
                            }
                        }

                        Collections.swap(sites, i, j); // undo the swap
                    }
                }
            }
        }

        double newScore = optimizedRoutes.stream().mapToDouble(Route::getScore).sum();
        return new SolutionResult(optimizedRoutes, newScore);
    }


    private List<Route> generateSolutionRoutes() {
        List<Route> routes = new ArrayList<>();
        Set<Integer> visitedSites = new HashSet<>();
        Node currentLocation = instance.getFirst();

        for (int day = 0; day < instance.getNbrDays(); day++) {
            boolean lastDay = (day == instance.getNbrDays() - 1);
            double remainingDistance = instance.getTravelDistances()[day];
            Route dailyRoute = new Route(day, (Hotel) currentLocation, null, instance.getTravelDistances()[day]);

            boolean addedSite;
            do {
                addedSite = false;
                double finalRemainingDistance1 = remainingDistance;
                Node finalCurrentLocation2 = currentLocation;
                List<Node> candidateSites = nodes.stream()
                        .filter(n -> n instanceof Site)
                        .filter(n -> !visitedSites.contains(n.getId()))
                        .filter(n -> {
                            double dToSite = instance.getDistances()[finalCurrentLocation2.getId()][n.getId()];
                            double dReturn;
                            if (lastDay) {
                                dReturn = instance.getDistances()[n.getId()][instance.getLast().getId()];
                            } else {
                                // For non-last days, check that a hotel is reachable after visiting the site.
                                OptionalDouble minHotelDist = hotels.stream()
                                        .mapToDouble(h -> instance.getDistances()[n.getId()][h.getId()])
                                        .min();
                                dReturn = minHotelDist.orElse(Double.MAX_VALUE);
                            }
                            return (dToSite + dReturn) <= finalRemainingDistance1;
                        })
                        .collect(Collectors.toList());

                if (!candidateSites.isEmpty()) {
                    Node chosenSite = candidateSites.get(random.nextInt(candidateSites.size()));
                    double dToSite = instance.getDistances()[currentLocation.getId()][chosenSite.getId()];
                    dailyRoute.addSite((Site) chosenSite);
                    dailyRoute.addParcouru(dToSite);
                    remainingDistance -= dToSite;
                    visitedSites.add(chosenSite.getId());
                    currentLocation = chosenSite;
                    addedSite = true;
                }
            } while (addedSite && remainingDistance > 0);

            Hotel endHotel;
            if (lastDay) {
                endHotel = instance.getLast();
            } else {
                Node finalCurrentLocation = currentLocation;
                double finalRemainingDistance = remainingDistance;
                List<Hotel> reachableHotels = hotels.stream()
                        .filter(h -> instance.getDistances()[finalCurrentLocation.getId()][h.getId()] <= finalRemainingDistance)
                        .collect(Collectors.toList());
                if (reachableHotels.isEmpty()) {
                    Node finalCurrentLocation1 = currentLocation;
                    endHotel = hotels.stream()
                            .min(Comparator.comparingDouble(h -> instance.getDistances()[finalCurrentLocation1.getId()][h.getId()]))
                            .orElse((Hotel) instance.getFirst());
                } else {
                    endHotel = reachableHotels.get(random.nextInt(reachableHotels.size()));
                }
            }
            double dToHotel = instance.getDistances()[currentLocation.getId()][endHotel.getId()];
            dailyRoute.addParcouru(dToHotel);
            dailyRoute.setLastNode(endHotel);
            currentLocation = endHotel;
            routes.add(dailyRoute);
        }
        return routes;
    }

    public boolean checkRoutesValidity(List<Route> routes) {
        // Check each route.
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
                if (visited.contains(site)) {
                    return false;
                }
                visited.add(site);
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
}