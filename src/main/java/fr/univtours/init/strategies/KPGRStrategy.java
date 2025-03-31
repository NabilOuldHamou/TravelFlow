package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.Solution;

public class KPGRStrategy implements MethodStrategy {

    @Override
    public void saveResults() {

    }

    @Override
    public void solve(Instance instance) {
        Solution s = new Solution(instance);
        s.kpgr();
        s.printRoutes();
    }
}
