public class ForestObserverTest {

    public static void main(String[] args) {
        testObserverReceivesUpdate();
        testRemovedObserverReceivesNoUpdates();
        testRejectsNullObserver();

        System.out.println("All ForestObserver tests passed.");
    }

    private static void testObserverReceivesUpdate() {
        Forest forest = new Forest(1, 1);
        int[] notifications = {0};

        ForestObserver observer = updatedForest -> {
            check(
                    updatedForest == forest,
                    "Observer should receive the updated forest"
            );

            notifications[0]++;
        };

        forest.addObserver(observer);
        forest.update();

        check(
                notifications[0] == 1,
                "Observer should be notified once after an update"
        );
    }

    private static void testRemovedObserverReceivesNoUpdates() {
        Forest forest = new Forest(1, 1);
        int[] notifications = {0};

        ForestObserver observer =
                updatedForest -> notifications[0]++;

        forest.addObserver(observer);
        forest.removeObserver(observer);
        forest.update();

        check(
                notifications[0] == 0,
                "Removed observer should not receive updates"
        );
    }

    private static void testRejectsNullObserver() {
        Forest forest = new Forest(1, 1);
        boolean exceptionThrown = false;

        try {
            forest.addObserver(null);
        } catch (IllegalArgumentException exception) {
            exceptionThrown = true;
        }

        check(
                exceptionThrown,
                "Adding a null observer should throw an exception"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}