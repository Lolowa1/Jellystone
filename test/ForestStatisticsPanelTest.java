public class ForestStatisticsPanelTest {

    public static void main(String[] args) {
        testDisplaysInitialStatistics();
        testRefreshesAfterForestUpdate();
        testRejectsNullForest();

        System.out.println(
                "All Forest statistics panel tests passed."
        );
    }

    private static void testDisplaysInitialStatistics() {
        Forest forest = new Forest(1, 2);

        for (Cell cell : forest.getGrid()[0]) {
            cell.removeTree();
        }

        forest.getGrid()[0][0].plantTree(new Tree(0.5));

        ForestStatisticsPanel panel =
                new ForestStatisticsPanel(forest);

        String text = panel.getStatisticsText();

        check(
                text.contains("Healthy: 1"),
                "Panel should display one healthy tree"
        );
        check(
                text.contains("Burning: 0"),
                "Panel should display zero burning trees"
        );
        check(
                text.contains("Empty/burned: 1"),
                "Panel should display one empty cell"
        );
    }

    private static void testRefreshesAfterForestUpdate() {
        Forest forest = new Forest(1, 1);
        forest.getGrid()[0][0].removeTree();
        forest.getGrid()[0][0].plantTree(new Tree(0.5));

        ForestStatisticsPanel panel =
                new ForestStatisticsPanel(forest);

        forest.addObserver(panel);
        forest.getGrid()[0][0].getTree().ignite();
        forest.update();

        check(
                panel.getUpdateCount() == 1,
                "Panel should receive one forest update"
        );
        check(
                panel.getStatisticsText().contains("Tick: 1"),
                "Panel should display the current tick"
        );
        check(
                panel.getStatisticsText().contains("Burning: 1"),
                "Panel should refresh the burning-tree count"
        );
    }

    private static void testRejectsNullForest() {
        boolean exceptionThrown = false;

        try {
            new ForestStatisticsPanel(null);
        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }

        check(
                exceptionThrown,
                "Panel should reject a null forest"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}