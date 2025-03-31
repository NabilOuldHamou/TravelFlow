package fr.univtours;

import fr.univtours.models.Solution;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Instance i = new Instance("ressources/instance1.txt");
        //System.out.println(i.toString());

        //for (double[] row : i.getDistances())
          //  System.out.println(Arrays.toString(row));

        //  System.out.println(Arrays.toString(row));
        //System.out.println(i.getNodes().length);
       Solution test1 = new Solution(i);
      //  System.out.println( test1.toString());
        test1.printRoutes();
        test1.kpgr();
        test1.printRoutes();
    }

}
