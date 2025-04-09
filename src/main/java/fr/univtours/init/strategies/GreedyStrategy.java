package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.MultiSolutionGenerator;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;
import fr.univtours.saveSolutionToFile;
import fr.univtours.utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GreedyStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        char fn = filename.split("\\.")[0].charAt(filename.split("\\.")[0].length() - 1);
        new saveSolutionToFile("Instance" + fn + ".sol", result.routes());
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
