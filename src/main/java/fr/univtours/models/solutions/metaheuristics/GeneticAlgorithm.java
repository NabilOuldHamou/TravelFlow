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

    public GeneticAlgorithm(List<SolutionResult> results, int populationSize, int iterations, float mutation,
                            float crossover, float elitism) {
       this.populationSize = populationSize;
       this.iterations = iterations;
       this.mutation = mutation;
       this.crossover = crossover;
       this.elitism = elitism;
       this.individuals = new ArrayList<>();

       for (SolutionResult solutionResult : results) {
           List<Integer> chromosome = new ArrayList<>();
            for (int i = 0; i < solutionResult.routes().size(); i++) {
                Route r = solutionResult.routes().get(i);

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
                        i -> i.chromosome.stream().mapToDouble(c -> Instance.staticNodes[c].getScore()).sum())
                )
                .toList().reversed();

        return scores.subList(0, (int) (populationSize * elitism));
    }

    public void createNextGeneration(List<Individual> parents) {
        Random random = new Random();
        for (int i = 0; i < populationSize; i++) {

            // Choix de deux parents au pif
            Individual parent1 = parents.get(random.nextInt(parents.size()));
            Individual parent2;
            do {
                parent2 = parents.get(random.nextInt(parents.size()));
            } while (!parent1.chromosome.equals(parent2.chromosome));


            List<Integer> childChromosome = crossover(parent1, parent2);
            mutate(childChromosome);
        }
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
            switch (action) {
                case 0: // Modif

                    break;
                case 1: // Ajout

                    break;
                case 2: // Suppr

                    break;
            }
        }
    }

    public void fix(Individual individual) {
        // Ajouter
        // Modifier
        // Swap
        // Supprimer
    }

    public void train() {
        createNextGeneration(getBestIndividuals());
    }

    public record Individual(List<Integer> chromosome) {}
}
