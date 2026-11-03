import java.nio.file.*;
import java.util.*;

public class RegressionTest {
    private static int checks;
    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) throw new AssertionError(message);
    }
    private static State state(double danger) {
        ArrayList<Predator> predators = new ArrayList<>();
        for (String name : List.of("fox", "dingo", "feral pig", "wedge-tailed eagle"))
            predators.add(new Predator(name, danger, 0));
        return new State("VIC", predators);
    }
    private static Simulation simulate(State state, int guards, long seed) {
        Simulation result = new Simulation(new Farm("Sample Farm", "VIC", 80, 20),
                                           state, guards, new Random(seed));
        result.runSimulation();
        return result;
    }
    public static void main(String[] args) throws Exception {
        Animal sheep = new Sheep();
        sheep.setKilledBy("fox");
        check(sheep.getKilledBy().equals("fox"), "cause of death must be recorded");
        check(!new SheepWithCause("fox").isAlive(), "cause constructor must mark animal dead");
        Simulation safe = simulate(state(0), 0, 42);
        check(safe.getAverageCost() == 0, "no predators and no guards must cost zero");
        check(safe.getAverageSheepLost() + safe.getAverageLambsLost() == 0, "safe flock survives");
        Simulation certain = simulate(state(1), 0, 42);
        check(certain.getAverageSheepLost() == 80 && certain.getAverageLambsLost() == 20,
              "certain first predator kills each animal exactly once");
        check(certain.getAverageCost() == 17000, "loss valuation must match 80*150 + 20*250");
        check(certain.getAverageKillsByPredator().get("fox") == 100, "no duplicate kills");
        check(certain.getAverageKillsByPredator().get("dingo") == 0, "dead animals cannot die twice");
        for (int guards = 0; guards <= 2; guards++) {
            Simulation a = simulate(state(.08), guards, 42);
            Simulation b = simulate(state(.08), guards, 42);
            check(a.getAverageCost() == b.getAverageCost(), "seed must reproduce cost");
            check(a.getAverageSheepLost() <= 80 && a.getAverageLambsLost() <= 20
                  && a.getAverageAlpacasLost() <= guards, "losses bounded by population");
            double kills = a.getAverageKillsByPredator().values().stream()
                            .mapToDouble(Double::doubleValue).sum();
            check(Math.abs(kills - a.getAverageSheepLost() - a.getAverageLambsLost()
                           - a.getAverageAlpacasLost()) < 1e-9, "kill accounting must balance");
        }
        Simulation guarded = simulate(state(0), 2, 42);
        check(guarded.getAverageCost() >= 1800 && guarded.getAverageCost() < 2200,
              "two surviving guards cost 1000 plus two maintenance amounts in [400,600)");
        check(new FileIO().readPredatorFile().size() == 4, "four states must load");
        Path bad = Files.createTempFile("predators-invalid", ".txt");
        try {
            for (String row : List.of("VIC,NaN,0,0,0", "VIC,2,0,0,0", "VIC,no,0,0,0", "VIC,0")) {
                Files.writeString(bad, row);
                boolean rejected = false;
                try { new FileIO(bad.toString()).readPredatorFile(); }
                catch (IllegalArgumentException e) { rejected = true; }
                check(rejected, "invalid data must fail instead of silently becoming zero");
            }
        } finally { Files.deleteIfExists(bad); }
        System.out.println("PASS: " + checks + " regression checks");
    }
    private static class SheepWithCause extends Animal {
        SheepWithCause(String cause) { super(cause); }
        public int getValue() { return 150; }
        public double getDeathProbability(double p) { return p; }
        public String getType() { return TYPE_SHEEP; }
    }
}
