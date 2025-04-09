package fr.univtours.models.solutions;

import fr.univtours.Instance;
import fr.univtours.models.Route;
import fr.univtours.models.Site;

import java.util.*;

public class MultiSolutionGenerator {

    private Instance instance;
    private int numberOfSolutions;
    private Random random = new Random();

    public MultiSolutionGenerator(Instance instance, int numberOfSolutions) {
        this.instance = instance;
        this.numberOfSolutions = numberOfSolutions;
    }

    public List<SolutionResult> generateSolutions() {
        Set<String> uniqueSolutionSignatures = new HashSet<>();
        List<SolutionResult> solutionResults = new ArrayList<>();

        while (solutionResults.size() < numberOfSolutions) {
            RandomSolution candidateSolution = new RandomSolution(instance);
            SolutionResult solutionResult = candidateSolution.solve();

            String signature = generateSignature(solutionResult);

            if (!uniqueSolutionSignatures.contains(signature)) {
                uniqueSolutionSignatures.add(signature);
                solutionResults.add(solutionResult);
            }
        }

        return solutionResults;
    }

    private String generateSignature(SolutionResult solution) {
        StringBuilder sb = new StringBuilder();
        for (Route route : solution.routes()) {
            sb.append(route.getFirstNode().getId()).append("-");
            for (Site site : route.getSites()) {
                sb.append(site.getId()).append("-");
            }
            sb.append(route.getLastNode().getId()).append("-");
            sb.append("|");
        }
        return sb.toString();
    }
}
