package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Solution;
import fr.univtours.saveSolutionToFile;

public class KPGRStrategy implements MethodStrategy {

    Solution s;

    @Override
    public void saveResults(int i) {
        new saveSolutionToFile("Instance" + i + ".sol", s.getRoutes());
    }

    @Override
    public void solve(Instance instance) {
        s = new Solution(instance);
        s.kpgr();
        //s.linkSite(); Méthode à corriger
        s.checkSolution();
        s.printRoutes();


    }
}
