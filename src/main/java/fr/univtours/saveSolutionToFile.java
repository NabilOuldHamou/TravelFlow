package fr.univtours;

import fr.univtours.models.Route;
import fr.univtours.models.Site;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class saveSolutionToFile {

    public saveSolutionToFile(String filename, List<Route> routes, long elapsedTime) {
        filename = "Resultat/" + filename;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Écrire la valeur de la solution (score total)
            int totalScore =(int) routes.stream().mapToDouble(Route::getScore).sum();
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
            // Écrire le temps d'exécution
            writer.write("Execution time: " + elapsedTime + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
