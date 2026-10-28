import java.util.Map;

public class ForestStatisticsTest {

    public static void main(String[] args) {
        testCountsCellStates();
        testIncludesStatesWithZeroCount();

        System.out.println("All Forest statistics tests passed.");
    }

    private static void testCountsCellStates() {
        Forest forest = new Forest(1, 3);

        for (Cell cell : forest.getGrid()[0]) {
            cell.removeTree();
        }

        forest.getGrid()[0][0].plantTree(new Tree(0.5));

        Tree burningTree = new Tree(0.5);
        burningTree.ignite();
        forest.getGrid()[0][1].plantTree(burningTree);

        Map<CellState, Long> counts = forest.getCellStateCounts();

        check(counts.get(CellState.HEALTHY) == 1L,
                "Forest should contain one healthy cell");
        check(counts.get(CellState.BURNING) == 1L,
                "Forest should contain one burning cell");
        check(counts.get(CellState.EMPTY) == 1L,
                "Forest should contain one empty cell");
    }

    private static void testIncludesStatesWithZeroCount() {
        Forest forest = new Forest(1, 1);
        forest.getGrid()[0][0].removeTree();

        Map<CellState, Long> counts = forest.getCellStateCounts();

        check(counts.get(CellState.EMPTY) == 1L,
                "Forest should contain one empty cell");
        check(counts.get(CellState.HEALTHY) == 0L,
                "Healthy state should be present with a zero count");
        check(counts.get(CellState.BURNING) == 0L,
                "Burning state should be present with a zero count");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}