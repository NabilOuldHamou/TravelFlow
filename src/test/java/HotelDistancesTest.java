import fr.univtours.Instance;
import fr.univtours.models.Solution;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class HotelDistancesTest {

    @Test
    public void hotelDistancesTest() {
        Instance instance = new Instance("ressources/instance16.txt");
        Solution solution = new Solution(instance);
        Random r = new Random();

        double rayon = instance.getTravelDistances()[1] / 2;
        var scores = solution.hotelScores(rayon);
        System.out.println(String.format("Rayon : %.2f ---------", rayon));
        for (var s : scores) {
            System.out.println(String.format(
                    "Hotel %s : Score %.2f",
                    scores.indexOf(s), s.getSecond()
            ));
        }
    }

}
