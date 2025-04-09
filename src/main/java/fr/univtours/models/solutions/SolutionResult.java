package fr.univtours.models.solutions;

import fr.univtours.models.Route;

import java.util.List;

public record SolutionResult(List<Route> routes, double score) {
    public void printResult() {
        System.out.println("\n--------- SOLUTION RESULT ---------");
        for (Route route : routes) {
            System.out.println(route);
        }
        System.out.printf("TOTAL SCORE: %f%n", score);
    }
}
