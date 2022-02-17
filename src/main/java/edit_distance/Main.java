package edit_distance;

import benchmark.Benchmark;
import edit_distance.entities.EditDistanceCalculator;

import java.util.LinkedHashMap;

/**
 * Main class with example of use of {@link EditDistanceCalculator}.
 */
class Main {

    /**
     * Sample term.
     */
    private final static String SAMPLE_TERM_1 = "PLASMA";
    /**
     * Sample term.
     */
    private final static String SAMPLE_TERM_2 = "ALTRUISM";

    /**
     * Main method.
     *
     * @param args Command line args.
     */
    public static void main(String[] args) {

        EditDistanceCalculator editDistanceCalculator =
                new EditDistanceCalculator("HOME", "HOUSE");
        System.out.println("Edit Distance = " + editDistanceCalculator.getEditDistance());
        System.out.println(editDistanceCalculator.getCostMatrixToString());

        var sampleTerms = new LinkedHashMap<String, String>();
        sampleTerms.put("HOUSE", "HOME");
        sampleTerms.put("PLASMA", "ALTRUISM");
        sampleTerms.put("", "");
        sampleTerms.put("2", "");
        sampleTerms.put("1", "1");
        sampleTerms.put("Foo", "Bar");
        sampleTerms.put("abcde", "fghij");
        sampleTerms.put("RELEVANT", "ELEPHANT");
        sampleTerms.entrySet().stream()
                .map(entry -> new EditDistanceCalculator(entry.getKey(), entry.getValue()))
                .forEach(editDistanceInstance -> {
                    System.out.println();
                    System.out.println("Edit Distance = " + editDistanceInstance.getEditDistance());
                    System.out.println(editDistanceInstance.getCostMatrixToString());
                });

    }

    /**
     * Benchmark for the first version of the program.
     */
    @Benchmark
    private static void editDistanceBenchmark() {
        //noinspection ResultOfMethodCallIgnored
        new EditDistanceCalculator(SAMPLE_TERM_1, SAMPLE_TERM_2).getEditDistance();
    }

}