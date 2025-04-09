package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.MultiSolutionGenerator;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;
import fr.univtours.saveSolutionToFile;
import fr.univtours.utils.Pair;

public class MSGGeneticStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        char fn = filename.split("\\.")[0].charAt(filename.split("\\.")[0].length() - 1);
        new saveSolutionToFile("Instance" + fn + ".sol", result.routes(), elapsedTime);
    }

    @Override
    public SolutionResult solve(Instance instance) {
        try {
            MultiSolutionGenerator msg = new MultiSolutionGenerator(instance, 150);
            var sr = msg.generateSolutions();

            GeneticAlgorithm ga = new GeneticAlgorithm(sr, instance, 150, 5000, 0.1f, new Pair<>(0.3f, 0.7f), 0.2f);
            var routes =  ga.train();

            return new SolutionResult(routes, routes.stream().mapToDouble(Route::getScore).sum());

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
