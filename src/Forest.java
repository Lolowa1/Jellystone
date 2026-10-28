import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Forest {

    private Cell[][] grid;

    private double temperature;
    private double rainfall;
    private double aridity;

    private Wind wind;

    private Random random = new Random();
    private final List<ForestObserver> observers = new ArrayList<>();

    public Forest(int rows, int columns) {

        grid = new Cell[rows][columns];

        temperature = 25.0;
        rainfall = 0.2;
        aridity = 0.5;

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                grid[row][col] = new Cell(row, col);

                // 80% chance of there being a tree
                if (random.nextDouble() < 0.8) {

                    double spreadability =
                            0.3 + random.nextDouble() * 0.7;

                    grid[row][col].plantTree(
                            new Tree(spreadability)
                    );
                }
            }
        }
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public long getBurningTreeCount() {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Cell::hasTree)
                .map(Cell::getTree)
                .filter(Tree::isBurning)
                .count();
    }

    public Map<CellState, Long> getCellStateCounts() {
        Map<CellState, Long> counts = Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(
                        this::classifyCell,
                        () -> new EnumMap<>(CellState.class),
                        Collectors.counting()
                ));

        for (CellState state : CellState.values()) {
            counts.putIfAbsent(state, 0L);
        }

        return counts;
    }

    private CellState classifyCell(Cell cell) {
        if (!cell.hasTree()) {
            return CellState.EMPTY;
        }

        if (cell.getTree().isBurning()) {
            return CellState.BURNING;
        }

        return CellState.HEALTHY;
    }

    public void addObserver(ForestObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ForestObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ForestObserver observer : new ArrayList<>(observers)) {
            observer.forestUpdated(this);
        }
    }

    public void setWind(Wind wind) {
        this.wind = wind;
        wind.applyWind(this);
    }

    public Wind getWind() {
        return wind;
    }

    public void update() {
        spreadFire();
        advanceBurningTrees();
        notifyObservers();
    }

    private void advanceBurningTrees() {

        for (Cell[] row : grid) {

            for (Cell cell : row) {

                if (!cell.hasTree()) {
                    continue;
                }

                Tree tree = cell.getTree();

                if (tree.isBurning()) {
                    tree.advanceBurning();
                }

                if (!tree.isAlive()) {
                    cell.removeTree();
                }
            }
        }
    }

    private void spreadFire() {

        boolean[][] igniteNext =
                new boolean[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                Cell cell = grid[row][col];

                if (cell.hasTree() &&
                    cell.getTree().isBurning()) {

                    trySpread(row, col, row - 1, col, igniteNext);
                    trySpread(row, col, row + 1, col, igniteNext);
                    trySpread(row, col, row, col - 1, igniteNext);
                    trySpread(row, col, row, col + 1, igniteNext);
                    trySpread(row, col, row - 1, col - 1, igniteNext);
                    trySpread(row, col, row - 1, col + 1, igniteNext);
                    trySpread(row, col, row + 1, col - 1, igniteNext);
                    trySpread(row, col, row + 1, col + 1, igniteNext);
                }
            }
        }

        // Ignite AFTER checking the entire grid
        for (int row = 0; row < grid.length; row++) {

            for (int col = 0; col < grid[row].length; col++) {

                if (igniteNext[row][col]) {
                    grid[row][col].getTree().ignite();
                }
            }
        }
    }

    private void trySpread(
        int sourceRow,
        int sourceCol,
        int row,
        int col,
        boolean[][] igniteNext) {

        if (row < 0 ||
            row >= grid.length ||
            col < 0 ||
            col >= grid[0].length) {

            return;
        }

        Cell target = grid[row][col];

        if (!target.hasTree()) {
            return;
        }

        if (target.getTree().isBurning()) {
            return;
        }

        double chance =
                target.getTree().getSpreadability();

        if (wind != null) {

            chance += wind.getSpreadModifier(
                    sourceCol,
                    sourceRow,
                    col,
                    row
            );
}

        chance += aridity * 0.2;

        chance += (temperature / 100.0) * 0.2;

        chance -= rainfall * 0.2;

        chance = Math.max(0.0, Math.min(1.0, chance));

        if (random.nextDouble() < chance) {
            igniteNext[row][col] = true;
        }
    }

    public void display() {

        for (Cell[] row : grid) {

            for (Cell cell : row) {

                if (!cell.hasTree()) {
                    System.out.print(". ");

                } else if (cell.getTree().isBurning()) {
                    System.out.print("X ");

                } else {
                    System.out.print("T ");
                }
            }

            System.out.println();
        }
    }
}