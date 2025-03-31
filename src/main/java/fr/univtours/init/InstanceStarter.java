package fr.univtours.init;

import fr.univtours.Instance;
import fr.univtours.init.strategies.KPGRStrategy;
import fr.univtours.init.strategies.MethodStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class InstanceStarter {

    private MethodStrategy method;
    private List<Instance> instances;

    public InstanceStarter(String method, List<String> files) {
        this.instances = new ArrayList<>();
        files.forEach(f -> {
            this.instances.add(new Instance(f));
        });

        switch (method) {
            case "kpgr":
                this.method = new KPGRStrategy();
        }
    }

    public void solve() {
        for (Instance instance : this.instances) {
            method.solve();
        }
    }

}
