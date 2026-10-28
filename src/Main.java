import java.awt.BorderLayout;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {

    public static void main(String[] args) {

        Forest forest = new Forest(40, 60);

        Wind wind = new Wind(30, "EAST", 30, 20, 15);
        forest.setWind(wind);

        Random random = new Random();

        IgnitionSource source;
        Lightning lightning = null;

        int choice = random.nextInt(3);

        if (choice == 0) {
            lightning = new Lightning();
            source = lightning;
            System.out.println("Ignition source: Lightning");

        } else if (choice == 1) {
            source = new Arson(20, 20);
            System.out.println("Ignition source: Arson");

        } else {
            source = new Backburning(20);
            System.out.println("Ignition source: Backburning");
        }

        try {
            source.ignite(forest);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            System.out.println(
                    "Ignition failed: " + exception.getMessage()
            );
        }

        JFrame window = new JFrame("Forest Fire Simulation");

        ForestPanel forestPanel =
                new ForestPanel(forest, lightning);

        ForestStatisticsPanel statisticsPanel =
                new ForestStatisticsPanel(forest);

        forest.addObserver(forestPanel);
        forest.addObserver(statisticsPanel);

        window.setLayout(new BorderLayout());
        window.add(forestPanel, BorderLayout.CENTER);
        window.add(statisticsPanel, BorderLayout.SOUTH);

        window.setSize(900, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Timer timer = new Timer(
                500,
                event -> forest.update()
        );

        timer.start();
    }
}