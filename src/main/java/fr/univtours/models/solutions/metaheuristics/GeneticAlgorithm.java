package fr.univtours.models.solutions.metaheuristics;

import fr.univtours.Instance;
import fr.univtours.models.Hotel;
import fr.univtours.models.Node;
import fr.univtours.models.Route;
import fr.univtours.models.Site;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.Solution;
import fr.univtours.models.solutions.SolutionResult;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GeneticAlgorithm {

    private int populationSize;
    private int iterations;
    private float mutation;
    private float crossover;
    private float elitism;
    private List<Individual> individuals;
    private Instance instance;

    public GeneticAlgorithm(List<SolutionResult> solutions, Instance instance, int populationSize, int iterations, float mutation,
                            float crossover, float elitism) {
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
                        i -> i.chromosome.stream().mapToDouble(c -> instance.getNodes()[c].getScore()).sum())
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


            List<Integer> childChromosome = crossover(parent1, parent2); // Croisement
            mutate(childChromosome); // Mutation

            if (!verifyChromosome(childChromosome)) {
                fixChromosome(childChromosome);
            }

            temp.add(new Individual(childChromosome));
        }

        individuals.clear();
        individuals.addAll(temp);
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


        // Vérif distance
        List<Hotel> hotels = Arrays.stream(instance.getNodes())
                .filter(n -> n instanceof Hotel)
                .map(Hotel.class::cast)
                .toList();

        List<Integer> hotelIds = chromosome.stream()
                .filter(id -> hotels.stream().anyMatch(h -> h.getId() == id))
                .toList();

    }

    public List<Integer> crossover(Individual parent1, Individual parent2) {
        // Chopper les chromosomes et faire le crossover
        List<Integer> childChromosome = new ArrayList<>();

        List<Integer> firstCrossover = new ArrayList<>(parent1.chromosome.subList(0, (int) (parent1.chromosome.size() * crossover)));
        List<Integer> secondCrossover = new ArrayList<>(parent2.chromosome.subList((int) (parent1.chromosome.size() * crossover), parent2.chromosome.size()));

        childChromosome.addAll(firstCrossover);
        childChromosome.addAll(secondCrossover);

        return childChromosome;
    }

    public void mutate(List<Integer> chromosome) {
        Random random = new Random();
        boolean mutate = (random.nextFloat() <= mutation);
        if (mutate) {
            int action = random.nextInt(3);
            int randomIndex = random.nextInt(1, chromosome.size() - 1);
            int randomNodeIndex = random.nextInt(instance.getNodes().length);
            switch (action) {
                case 0: // Modif
                    chromosome.set(randomIndex, instance.getNodes()[randomNodeIndex].getId());
                    break;
                case 1: // Ajout
                    chromosome.add(randomIndex, instance.getNodes()[randomNodeIndex].getId());
                    break;
                case 2: // Suppr
                    chromosome.remove(randomIndex);
                    break;
            }
        }
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

        // Vérification de la distance par jour
        for (int i = 0; i < expectedDays; i++) {
            int startHotelId = hotelIds.get(i);
            int endHotelId = hotelIds.get(i + 1);

            int startIndex = chromosome.indexOf(startHotelId);
            int endIndex = chromosome.indexOf(endHotelId);

            if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
                return false;
            }

            double totalDistance = 0.d;
            for (int j = startIndex; j < endIndex; j++) {
                Node a = instance.getNodes()[chromosome.get(j)];
                Node b = instance.getNodes()[chromosome.get(j + 1)];
                totalDistance += instance.getDistanceBetween(a, b);
            }

            if (totalDistance > instance.getTravelDistances()[i]) {
                return false;
            }
        }
        return true;
    }

    public List<Route> train() {
        for (int i = 0; i < iterations; i++) {
            createNextGeneration(getBestIndividuals());
        }

        List<Double> scores = individuals.stream()
                .map(i -> i.chromosome.stream().mapToDouble(c -> instance.getNodes()[c].getScore()).sum())
                .toList();
        return decodeChromosomeToRoutes(getBestIndividuals().get(0).chromosome);
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

                    if (previous != null) {
                        double dist = instance.getDistanceBetween(previous, hotel);
                        currentRoute.addParcouru(dist);
                        currentRoute.setDistance(currentRoute.getParcouru());
                    }
                    routes.add(currentRoute);
                }

                startHotel = hotel;
                currentRoute = new Route(routeId++, startHotel, null, 0);
                previous = hotel;
            } else if (node instanceof Site site && currentRoute != null) {
                currentRoute.addSite(site);

                if (previous != null) {
                    double dist = instance.getDistanceBetween(previous, site);
                    currentRoute.addParcouru(dist);
                }

                previous = site;
            }
        }

        return routes;
    }


    public record Individual(List<Integer> chromosome) {}
}
