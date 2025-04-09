package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.MultiSolutionGenerator;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;
import fr.univtours.utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GreedyStrategy implements MethodStrategy {

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
            GreedySolution gs = new GreedySolution(instance);
            return gs.solve();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
