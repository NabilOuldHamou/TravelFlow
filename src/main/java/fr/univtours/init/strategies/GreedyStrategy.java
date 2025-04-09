package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.saveSolutionToFile;

public class GreedyStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        char fn = filename.split("\\.")[0].charAt(filename.split("\\.")[0].length() - 1);
        new saveSolutionToFile("Instance" + fn + ".sol", result.routes(), elapsedTime);
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
