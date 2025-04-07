package fr.univtours.init.strategies;

import fr.univtours.Instance;
import fr.univtours.models.solutions.GreedySolution;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.models.solutions.metaheuristics.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GreedyStrategy implements MethodStrategy {

    @Override
    public void saveResults() {

    }

    @Override
    public void solve(Instance instance) {
        GreedySolution gs = new GreedySolution(instance);
        var result = gs.solve();
        result.printResult();

        List<SolutionResult> sr = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            sr.add(result);
        }

        GeneticAlgorithm ga = new GeneticAlgorithm(sr, 500, 1000, 0.2f, 0.7f, 0.3f);
        ga.train();

    }
}
