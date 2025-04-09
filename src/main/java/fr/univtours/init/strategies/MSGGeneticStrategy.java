package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.MultiSolutionGenerator;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;
import fr.univtours.utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MSGGeneticStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        String separator = System.getProperty("os.name").startsWith("Windows") ? "\\\\" : "/";
        String path = "results/" + filename.split(separator)[1];
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
