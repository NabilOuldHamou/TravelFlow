package fr.univtours;

import fr.univtours.init.InstanceStarter;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            String[] method = args[0].split("=");
            List<String> files = new ArrayList<>();

            if (args.length > 1) {
                // TODO
            } else {
                File folder = new File("ressources/");
                for (File file : folder.listFiles()) {
                    files.add(file.getPath());
                }
                files.sort(String::compareTo);
            }

            long startTime = System.currentTimeMillis();
            InstanceStarter is = new InstanceStarter(method[1], files);
            is.solve();
            long endTime = System.currentTimeMillis();
            System.out.println("\nTotal time: " + (endTime - startTime) + "ms");
        }
    }

}
