package edit_distance;

import benchmark.BenchmarkRunner;

public class Benchmarking {
    public static void main(String[] args) {
        BenchmarkRunner benchmarkRunner = new BenchmarkRunner(true);
        benchmarkRunner.benchmarkAllAnnotatedMethodsAndGetListOfResults();
        System.out.println(benchmarkRunner);
    }
}
