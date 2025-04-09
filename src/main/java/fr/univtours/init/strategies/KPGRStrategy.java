package fr.univtours.init.strategies;

import fr.univtours.Instance;

import fr.univtours.models.solutions.Solution;
import fr.univtours.models.solutions.SolutionResult;

public class KPGRStrategy implements MethodStrategy {

    @Override
    public void saveResults(long elapsedTime, String filename, SolutionResult result) {

        new saveSolutionToFile("Instance" + i + ".sol", s.getRoutes());
    }

    @Override
    public void solve(Instance instance) {
        s = new Solution(instance);
    }

    public SolutionResult solve(Instance instance) {
        Solution s = new Solution(instance);
        s.kpgr();
        //s.linkSite(); Méthode à corriger
        s.checkSolution();
        s.printRoutes();

        return null;
    }
}
