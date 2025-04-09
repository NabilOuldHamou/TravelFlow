package fr.univtours.models.solutions.metaheuristics;

import fr.univtours.Instance;
import fr.univtours.models.Hotel;
import fr.univtours.models.Node;
import fr.univtours.models.Route;
import fr.univtours.models.Site;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.Solution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.utils.Pair;
import lombok.Getter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GeneticAlgorithm {

    private int populationSize;
    private int iterations;
    private float mutation;
    private Pair<Float, Float> crossover;
    private float elitism;
    private List<Individual> individuals;
    private Instance instance;

    public GeneticAlgorithm(List<SolutionResult> solutions, Instance instance, int populationSize, int iterations, float mutation,
                            Pair<Float, Float> crossover, float elitism) {
        this.populationSize = populationSize;
        this.iterations = iterations;
        this.mutation = mutation;
        this.crossover = crossover;
        this.elitism = elitism;
        this.individuals = new ArrayList<>();
        this.instance = instance;

       for (SolutionResult solution : solutions) {
           List<Integer> chromosome = new ArrayList<>();
            for (int i = 0; i < solution.routes().size(); i++) {
                Route r = solution.routes().get(i);

                List<Integer> tmp = new ArrayList<>(r.getSites().stream().map(Site::getId).toList());
                if (i == 0) tmp.addFirst(r.getFirstNode().getId());
                tmp.add(r.getLastNode().getId());
                chromosome.addAll(tmp);
            }
            individuals.add(new Individual(chromosome));
       }
    }

    public List<Individual> getBestIndividuals() {
        List<Individual> scores = individuals.stream()
                .sorted(Comparator.comparingDouble(
                        i -> i.chromosome.stream().mapToDouble(c -> instance.getNodes()[c].getScore() * i.chromosome.size()).sum())
                )
                .toList().reversed();

        return scores.subList(0, (int) (populationSize * elitism));
    }

    public void createNextGeneration(List<Individual> parents) {
        Random random = new Random();
        List<Individual> temp = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {

            // Choix de deux parents au pif
            Individual parent1 = parents.get(random.nextInt(parents.size()));
            Individual parent2;
            do {
                parent2 = parents.get(random.nextInt(parents.size()));
            } while (!parent1.chromosome.equals(parent2.chromosome));


            List<Integer> childChromosome = crossover(parent1.chromosome, parent2.chromosome); // Croisement
            mutate(childChromosome); // Mutation

            if (!verifyChromosome(childChromosome)) {
                fixChromosome(childChromosome);
            }

            temp.add(new Individual(childChromosome));
        }

        individuals.clear();
        individuals.addAll(temp);
    }

    public List<Integer> crossover(List<Integer> parent1, List<Integer> parent2) {
        // Chopper les chromosomes et faire le crossover
        List<Integer> childChromosome = new ArrayList<>();

        int firstPoint = (int) (parent1.size() * crossover.getFirst());
        int secondPoint = (int) (parent1.size() * crossover.getSecond());

        List<Integer> firstChromosome = parent1.subList(0, firstPoint);
        List<Integer> secondChromosome = parent2.subList(firstPoint, secondPoint);
        List<Integer> thirdChromosome = parent1.subList(secondPoint, parent1.size());

        childChromosome.addAll(firstChromosome);
        childChromosome.addAll(secondChromosome);
        childChromosome.addAll(thirdChromosome);

        return childChromosome;
    }

    public void mutate(List<Integer> chromosome) {
        Random random = new Random();
        float randomFloat = random.nextInt(0, 101) / 100.f;
        boolean mutate = (randomFloat <= mutation);
        if (mutate) {
            int action = random.nextInt(2);
            switch (action) {
                case 0:
                    randomMutation(chromosome, random);
                    break;
                case 1:
                    swapMutation(chromosome, random);
                    break;
            }
        }
    }

    public void randomMutation(List<Integer> chromosome, Random random) {
        int action = random.nextInt(3);
        int randomIndex = random.nextInt(chromosome.size());
        List<Node> possibleNodes = new ArrayList<>(instance.getAllSites().stream()
                .filter(s -> !chromosome.contains(s.getId()))
                .map(Node.class::cast)
                .toList());
        int randomNodeIndex = random.nextInt(possibleNodes.size());
        switch (action) {
            case 0: // Modif
                chromosome.set(randomIndex, possibleNodes.get(randomNodeIndex).getId());
                break;
            case 1: // Ajout
                chromosome.add(randomIndex, possibleNodes.get(randomNodeIndex).getId());
                break;
            case 2: // Suppr
                chromosome.remove(randomIndex);
                break;
        }
    }

    public void swapMutation(List<Integer> chromosome, Random random) {
        List<List<Integer>> routes = divideChromosome(chromosome, instance.getNbrDays());

        for (List<Integer> route : routes) {
            if (route.size() <= 3) {
                continue;
            }

            double currentDistance = instance.getDistanceBetween(route);

            double currentScore = 0.0;
            for (int i = 1; i < route.size()-1; i++) {
                currentScore += instance.getNodes()[route.get(i)].getScore();
            }

            int pos1 = random.nextInt(route.size() - 2) + 1;
            int pos2;
            do {
                pos2 = random.nextInt(route.size() - 2) + 1;
            } while (pos2 == pos1);

            Collections.swap(route, pos1, pos2);

            // Mesurer la distance après échange
            double newDistance = instance.getDistanceBetween(route);

            double newScore = 0.0;
            for (int i = 1; i < route.size()-1; i++) {
                newScore += instance.getNodes()[route.get(i)].getScore();
            }

            // annule mutation
            if (newDistance > currentDistance || newScore < currentScore) {
                Collections.swap(route, pos1, pos2);
            }

        }

        chromosome.clear();
        chromosome.addAll(
                routes.stream().flatMap(List::stream).collect(
                        ArrayList::new,
                        (acc, current) -> {
                            if (acc.isEmpty() || !acc.getLast().equals(current)) {
                                acc.add(current);
                            }
                        },
                        ArrayList::addAll
                )
        );
    }

    public boolean verifyChromosome(List<Integer> chromosome) {
        // Vérification des hôtels de départ / arrivé
        if (chromosome.getFirst() != instance.getFirst().getId() || chromosome.getLast() != instance.getLast().getId()) {
            return false;
        }

        // Vérification du nombre de jours
        List<Hotel> hotels = Arrays.stream(instance.getNodes())
                .filter(n -> n instanceof Hotel)
                .map(Hotel.class::cast)
                .toList();

        List<Integer> hotelIds = chromosome.stream()
                .filter(id -> hotels.stream().anyMatch(h -> h.getId() == id))
                .toList();

        int expectedDays = instance.getNbrDays();
        if (expectedDays != (hotelIds.size() - 1)) {
            return false;
        }

        // Vérification que les sites soient uniques
        Set<Integer> visitedSites = new HashSet<>();
        for (int id : chromosome) {
            Node node = instance.getNodes()[id];
            if (!(node instanceof Hotel)) {
                if (!visitedSites.add(id)) {
                    return false;
                }
            }
        }

        // Création des "routes"
        List<List<Integer>> routes = divideChromosome(chromosome, instance.getNbrDays());

        // Vérification de la distance par jour
        for (int i = 0; i < instance.getNbrDays(); i++) {
            double dailyDistance = instance.getTravelDistances()[i];
            List<Integer> route = routes.get(i);
            if (instance.getDistanceBetween(route) > dailyDistance)
                return false;
        }

        return true;
    }

    private List<List<Integer>> divideChromosome(List<Integer> chromosome, int nbrDays) {
        List<Integer> allHotelsIds = instance.getAllHotels().stream().map(Hotel::getId).toList();
        List<List<Integer>> routes = new ArrayList<>();

        List<Integer> hotelIndices = new ArrayList<>();
        for (int i = 0; i < chromosome.size(); i++) {
            if (allHotelsIds.contains(chromosome.get(i))) {
                hotelIndices.add(i);
            }
        }

        for (int j = 0; j < hotelIndices.size() - 1; j++) {
            int start = hotelIndices.get(j);
            int end = hotelIndices.get(j + 1);
            routes.add(new ArrayList<>(chromosome.subList(start, end + 1)));
        }

        return routes;
    }

    public void fixChromosome(List<Integer> chromosome) {
        // Réparation premier hôtel
        if (chromosome.getFirst() != instance.getFirst().getId()) {
            chromosome.addFirst(instance.getFirst().getId());
        }

        // Réparation dernier hôtel
        if (chromosome.getLast() != instance.getLast().getId()) {
            chromosome.addLast(instance.getLast().getId());
        }

        // Vérif duplicité des sites
        Set<Site> seen = new HashSet<>();
        List<Integer> cleanedChromosome = chromosome.stream()
                .filter(c -> {
                    Node node = instance.getNodes()[c];
                    if (node instanceof Site site) {
                        return seen.add(site);
                    }
                    return true;
                })
                .toList();

        chromosome.clear();
        chromosome.addAll(cleanedChromosome);


        // Création des "routes"
        int nbrDays = instance.getNbrDays();
        List<List<Integer>> routes = divideChromosome(chromosome, instance.getNbrDays());

        // Vérif nombre de routes
        if (routes.size() < nbrDays) {
            int missingRoutes = nbrDays - routes.size();
            for (int i = 0; i < missingRoutes; i++) {
                List<Integer> baseRoute = routes.get(i);
                List<Integer> firstHalf = new ArrayList<>(baseRoute.subList(0, baseRoute.size() / 2));
                List<Integer> secondHalf = new ArrayList<>(baseRoute.subList(baseRoute.size() / 2, baseRoute.size()));

                Hotel closestHotel = instance.getClosestHotel(firstHalf.getLast());
                firstHalf.addLast(closestHotel.getId());
                secondHalf.addFirst(closestHotel.getId());

                routes.set(i, firstHalf);
                routes.add(i + 1, secondHalf);
            }
        } else if (routes.size() > nbrDays) {
            List<Integer> extraPoints = new ArrayList<>();
            for (int i = nbrDays; i < routes.size(); i++) {
                List<Integer> extraRoute = routes.get(i);
                if (extraRoute.size() > 2) {
                    extraPoints.addAll(extraRoute.subList(1, extraRoute.size() - 1));
                }
            }

            for (int i = routes.size() - 1; i >= nbrDays; i--) {
                routes.remove(i);
            }

            int dayIndex = 0;
            for (int p : extraPoints) {
                List<Integer> primaryRoute = routes.get(dayIndex);
                if (primaryRoute.size() >= 2) {
                    primaryRoute.add(primaryRoute.size() - 1, p);
                } else {
                    primaryRoute.add(p);
                }
                dayIndex = (dayIndex + 1) % nbrDays;
            }

        }

        for (int i = 0; i < nbrDays; i++) {
            double dailyDistance = instance.getTravelDistances()[i];
            List<Integer> route = routes.get(i);
            do {
                int bestIdx = 0;
                double bestDistance = 0;
                for (int j = 1; j < route.size() - 1; j++) {
                    int finalJ = j;
                    Integer worstPoint = route.subList(1, route.size() - 1).stream()
                            .min(Comparator.comparingDouble(p -> instance.getNodes()[p].getScore() / instance.getDistanceBetween(p, finalJ)))
                            .get();
                    double distanceAfterRemoving = getDistanceAfterRemoving(route, route.indexOf(worstPoint));
                    if (distanceAfterRemoving > bestDistance) {
                        bestIdx = j;
                        bestDistance = distanceAfterRemoving;
                    }
                }
                route.remove(bestIdx);
            } while (instance.getDistanceBetween(route) > dailyDistance);
        }

        chromosome.clear();
        chromosome.addAll(
                routes.stream().flatMap(List::stream).collect(
                        ArrayList::new,
                        (acc, current) -> {
                            if (acc.isEmpty() || !acc.getLast().equals(current)) {
                                acc.add(current);
                            }
                        },
                        ArrayList::addAll
                )
        );
    }

    private double getDistanceAfterRemoving(List<Integer> route, int index) {
        List<Integer> tempRoute = new ArrayList<>(route);
        tempRoute.remove(index);
        return instance.getDistanceBetween(tempRoute);
    }


    public List<Route> train() {
        for (int i = 0; i < iterations; i++) {
            createNextGeneration(getBestIndividuals());
        }

        List<Double> scores = individuals.stream()
                .map(i -> i.chromosome.stream().mapToDouble(c -> instance.getNodes()[c].getScore()).sum())
                .toList();

        return decodeChromosomeToRoutes(getBestIndividuals().getFirst().chromosome);
    }

    public List<Route> decodeChromosomeToRoutes(List<Integer> chromosome) {
        List<Route> routes = new ArrayList<>();
        int routeId = 0;

        Node previous = null;
        Hotel startHotel = null;

        Route currentRoute = null;

        for (int i = 0; i < chromosome.size(); i++) {
            Node node = instance.getNodes()[chromosome.get(i)];

            if (node instanceof Hotel hotel) {
                if (currentRoute != null) {
                    currentRoute.setLastNode(hotel);

                    double dist = instance.getDistanceBetween(previous, hotel);
                    currentRoute.addParcouru(dist);
                    currentRoute.setDistance(currentRoute.getParcouru());
                    routes.add(currentRoute);
                }

                startHotel = hotel;
                currentRoute = new Route(routeId++, startHotel, null, 0);
                previous = hotel;
            } else if (node instanceof Site site && currentRoute != null) {
                currentRoute.addSite(site);

                double dist = instance.getDistanceBetween(previous, site);
                currentRoute.addParcouru(dist);

                previous = site;
            }
        }

        return routes;
    }


    public record Individual(List<Integer> chromosome) {}
}
