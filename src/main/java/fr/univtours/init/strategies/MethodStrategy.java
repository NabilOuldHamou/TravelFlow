package fr.univtours.init.strategies;

import fr.univtours.Instance;

public interface MethodStrategy {

    void saveResults(int i);
    void solve(Instance instance);
}
