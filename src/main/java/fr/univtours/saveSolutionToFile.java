package fr.univtours;

import fr.univtours.models.Route;
import fr.univtours.models.Site;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class saveSolutionToFile {
    public saveSolutionToFile(String filename, List<Route> routes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Écrire la valeur de la solution (score total)
            int totalScore = routes.stream().mapToInt(Route::getScore).sum();
            writer.write(String.valueOf(totalScore));
            writer.newLine();

            // Écrire les routes
            for (Route route : routes) {
                writer.write(route.getFirstNode().getId() + " ");
                for (Site site : route.getSites()) {
                    writer.write(site.getId() + " ");
                }
                writer.write(route.getLastNode().getId() + "");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
