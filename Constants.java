public class Constants {
    public static int defaultGeneLength = 10;

    /* GA parameters */
    static double crossoverRate = 0.5;
    static  double mutationRate = 0.05;
    public static double NUM_OFFSPRING = 0.3;
    static double tournamentSize = 0.1;
    static int POPULATION_SIZE = 20;
    public static int MAX_LOST_GENERATION = 20;
    public static double maxInitialWeight = 2.0;

    // Number of runs averaged to get the fitness
    static int NUM_RUNS = 5;
    public static int MAX_ITERATIONS = 100;
    public static int PSO_ITERATIONS = 10;
    public static int MAX_MOVES = 5000;
    public static double[] defaultWeights = {-10.072815856557765, 29.233212822489993, 35.42440205833141, 155.38905355659324, 14.717078617276146, 0.0, 0.0, 0.0,
            0.0, 0.0};

    public static double MAX_HEURISTICS = Double.MAX_VALUE;
}
