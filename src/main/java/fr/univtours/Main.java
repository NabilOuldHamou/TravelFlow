package fr.univtours;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Instance i = new Instance("ressources/instance1.txt");
        System.out.println(i.toString());

        for (double[] row : i.getDistances())
            System.out.println(Arrays.toString(row));
    }

}
