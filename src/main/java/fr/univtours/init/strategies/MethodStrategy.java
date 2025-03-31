package fr.univtours.init.strategies;

import fr.univtours.Instance;

public interface MethodStrategy {

    void saveResults();
    void solve(Instance instance);
}
