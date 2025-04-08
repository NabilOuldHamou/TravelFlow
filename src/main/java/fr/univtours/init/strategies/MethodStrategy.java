package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.solutions.SolutionResult;

public interface MethodStrategy {

    void saveResults(long elapsedTime, String filename, SolutionResult result);
    SolutionResult solve(Instance instance);
}
