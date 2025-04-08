package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.RandomSolution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreedyStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        String path = "results/" + filename.split("/")[1];
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("Instance: " + filename);
            writer.newLine();
            writer.write("Routes: ");
            writer.newLine();
            for (Route r : result.routes()) {
                writer.write(r.toString());
                writer.newLine();
            }
            writer.write("Score: " + result.score());
            writer.newLine();
            writer.write("Time: " + elapsedTime + "ms");
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing results to file: " + e.getMessage());
        }
    }

    @Override
    public SolutionResult solve(Instance instance) {
        try {
            RandomSolution rs = new RandomSolution(instance);
            var sr = rs.generateSolutions(500);
            List<SolutionResult> optimized = new ArrayList<>();
            sr.forEach(n -> {
                optimized.add(rs.optimizeSolution(n));
            });
            GeneticAlgorithm ga = new GeneticAlgorithm(optimized, instance, 500, 500, 0.3f, 0.3f, 0.5f);
            var routes =  ga.train();

            return new SolutionResult(routes, routes.stream().mapToDouble(Route::getScore).sum());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
