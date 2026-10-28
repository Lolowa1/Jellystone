import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ForestStatisticsPanel
        extends JPanel
        implements ForestObserver {

    private final JLabel statisticsLabel;
    private int updateCount;

    public ForestStatisticsPanel(Forest forest) {
        if (forest == null) {
            throw new IllegalArgumentException(
                    "Forest cannot be null"
            );
        }

        statisticsLabel = new JLabel();
        updateCount = 0;

        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(new Color(35, 35, 35));
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        statisticsLabel.setForeground(Color.WHITE);
        statisticsLabel.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 14)
        );

        add(statisticsLabel);
        updateStatistics(forest);
    }

    @Override
    public void forestUpdated(Forest forest) {
        updateCount++;
        updateStatistics(forest);
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public String getStatisticsText() {
        return statisticsLabel.getText();
    }

    private void updateStatistics(Forest forest) {
        Map<CellState, Long> counts =
                forest.getCellStateCounts();

        statisticsLabel.setText(
                "Tick: " + updateCount
                        + "  |  Healthy: "
                        + counts.get(CellState.HEALTHY)
                        + "  |  Burning: "
                        + counts.get(CellState.BURNING)
                        + "  |  Empty/burned: "
                        + counts.get(CellState.EMPTY)
        );
    }
}