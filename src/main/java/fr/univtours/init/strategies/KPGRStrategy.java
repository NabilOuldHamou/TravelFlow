package fr.univtours.init.strategies;

import fr.univtours.Instance;

import fr.univtours.models.Route;
import fr.univtours.models.solutions.Solution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.saveSolutionToFile;

public class KPGRStrategy implements MethodStrategy {

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
        Solution s = new Solution(instance);
        s.kpgr();
        //s.linkSite(); Méthode à corriger
        s.checkSolution();
        s.printRoutes();

        double score = s.getRoutes().stream().mapToDouble(Route::getScore).sum();
        var sr = new SolutionResult(s.getRoutes(), score);

        return sr;
    }
}
