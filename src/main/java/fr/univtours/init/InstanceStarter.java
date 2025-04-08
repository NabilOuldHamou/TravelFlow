package fr.univtours.init;

import fr.univtours.Instance;
import fr.univtours.init.strategies.GreedyStrategy;
import fr.univtours.init.strategies.KPGRStrategy;
import fr.univtours.init.strategies.MethodStrategy;
import fr.univtours.models.solutions.SolutionResult;
import fr.univtours.utils.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class InstanceStarter {

    private MethodStrategy method;
    private List<Pair<String, Instance>> instances;

    public InstanceStarter(String method, List<String> files) {
        this.instances = new ArrayList<>();
        files.forEach(f -> {
            this.instances.add(new Pair<>(f, new Instance(f)));
        });

        switch (method) {
            case "kpgr":
                this.method = new KPGRStrategy();
                break;
            case "greedy":
                this.method = new GreedyStrategy();
                break;
        }
    }

    public void solve() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (Pair<String, Instance> p : this.instances) {
            executor.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    SolutionResult sr = method.solve(p.getSecond());
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    System.out.println("Runtime (" + p.getFirst() + "): " + elapsedTime + "ms");

                    // Save the results
                    method.saveResults(elapsedTime, p.getFirst(), sr);
                } catch (Exception e) {
                    // Handle any exception that occurs within the task
                    System.err.println("Error processing " + p.getFirst() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
