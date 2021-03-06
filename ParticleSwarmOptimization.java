import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

public class ParticleSwarmOptimization {

    private static final double[] MAX = {20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0};
    private static final double[] MIN = {-20.0, -20.0, -20.0, -20.0, -20.0, -20.0, -20.0, -20.0, -20.0, -20.0 };
    private static final double INERTIA_WEIGHT = 1.0;
    private static final double COGNITIVE_WEIGHT = 2.0;
    private static final double SOCIAL_WEIGHT = 2.5;
    private static final double[] gBest = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static final int gBestFitness = 0;
    private final int iterationNo;

    private static final int NUM_ITERATIONS = 10;

    private CopyOnWriteArrayList<Particle> particles;
    private PlayerSkeleton.ConcurrentExecutor concurrentExecutor = new PlayerSkeleton.ConcurrentExecutor(new
            ForkJoinPool());

    public void printParticleData(int n) {
        double[] position = particles.get(n).getPosition();
        double[] velocity = particles.get(n).getVelocity();
        int fitness = particles.get(n).getFitness();
        double[] pBest = particles.get(n).getPBest();
        int pBestFitness = particles.get(n).getPBestFitness();

//        System.out.println("Particle "  + n + " Local Best Position: ");
//
//        for (int i = 0; i < pBest.length; i++) {
//            System.out.print(pBest[i] + ", ");
//        }

        System.out.println("Particle "  + n + " Local Best Fitness: " + pBestFitness);
    }

    public void printSwarmData() {

        System.out.println("------ Current num of particles: " + particles.size());
        for (int i = 0; i < particles.size(); i++) {
            printParticleData(i);
        }

        System.out.print("Global Best Position: ");

        for (int i = 0; i < Particle.gBest.length; i++) {
            System.out.print(Particle.gBest[i] + ", ");
        }
        System.out.println();
        System.out.println("Global Best Fitness: " + Particle.gBestFitness);

        System.out.println("============");
    }

    public void runOneIteration() {
//        for (int i = 0; i < particles.size(); i++) {
//            particles.get(i).update();
//        }
        concurrentExecutor.map(EVAL_PARTICLE, particles, new ArrayList<Particle>());
    }

    public void runAndPrintIterations(int num) {
        for (int i = 0; i < num; i++) {
            System.out.println("HPSOGA Iteration: " + iterationNo + " PSO Iteration: " + i);
            runOneIteration();
            printSwarmData();
        }
    }

    public ParticleSwarmOptimization(Population p, int i) {
        Particle.MAX = MAX;
        Particle.MIN = MIN;
        Particle.INERTIA_WEIGHT = INERTIA_WEIGHT;
        Particle.COGNITIVE_WEIGHT = COGNITIVE_WEIGHT;
        Particle.SOCIAL_WEIGHT = SOCIAL_WEIGHT;
        Particle.gBest = gBest;
        Particle.gBestFitness = gBestFitness;
        particles = p.chromosomes;
        iterationNo = i;
    }

    public ArrayList<Particle> run() {
        runAndPrintIterations(Constants.PSO_ITERATIONS);
        ArrayList<Particle> result = new ArrayList<>();
        result.addAll(particles);

        return result;
    }

    private static final PlayerSkeleton.Mapper<Particle, Particle> EVAL_PARTICLE = new PlayerSkeleton.Mapper<Particle, Particle>() {
        @Override
        public Particle evaluate(Particle particle) {
//            System.out.println("Evaluating particle " + particle.toString());
            particle.update();
            return particle;
        }
    };
}
