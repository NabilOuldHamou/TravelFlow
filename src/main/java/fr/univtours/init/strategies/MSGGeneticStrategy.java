package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.RandomSolution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;
import fr.univtours.saveSolutionToFile;
import fr.univtours.utils.Pair;

public class MSGGeneticStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        String[] parts = filename.split("\\.");
        String baseName = parts[0];
        String instanceNumber = baseName.replaceAll("\\D+", "");

        String outputFilename = "Instance" + instanceNumber + ".sol";

        new saveSolutionToFile(outputFilename, result.routes(), elapsedTime);

    }

    @Override
    public SolutionResult solve(Instance instance) {
        try {
            RandomSolution rs = new RandomSolution(instance);
            var sr = rs.generateSolutions(2000);

            GeneticAlgorithm ga = new GeneticAlgorithm(sr, instance, 2000, 5000, 0.1f, new Pair<>(0.3f, 0.7f), 0.05f);
            var routes =  ga.train();

            return new SolutionResult(routes, routes.stream().mapToDouble(Route::getScore).sum());

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
