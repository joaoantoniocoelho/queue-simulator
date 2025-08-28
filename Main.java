import java.io.*;
import java.util.*;

public class Main {

    static class QueueCfg {
        int servers;
        int capacity;
        double minArrival, maxArrival;
        double minService, maxService;
    }

    static class LCG {
        private final long a = 1664525L, c = 1013904223L, m = (1L << 32);
        private long state;
        private final long limit;
        private long used = 0;

        LCG(long seed, long limit) {
            this.state = seed % m;
            this.limit = limit;
        }

        boolean hasNext() { return used < limit; }

        double next01() {
            state = (a * state + c) % m;
            used++;
            return (double) state / (double) m;
        }
    }

    enum EvType { ARRIVAL, DEPARTURE }

    static class Event implements Comparable<Event> {
        EvType type;
        double time;
        Event(EvType t, double tm){ type = t; time = tm; }
        public int compareTo(Event o){ return Double.compare(this.time, o.time); }
    }

    static class SingleQueueSim {
        QueueCfg cfg;
        LCG rng;

        double now = 0.0;
        PriorityQueue<Event> calendar = new PriorityQueue<>();
        PriorityQueue<Double> departures = new PriorityQueue<>();
        int inSystem = 0;
        long losses = 0;

        double[] timeInState;
        double lastEvtTime = 0.0;

        SingleQueueSim(QueueCfg cfg, LCG rng) {
            this.cfg = cfg; this.rng = rng;
            this.timeInState = new double[cfg.capacity + 1];
        }

        double u(double lo, double hi){
            if (!rng.hasNext()) return hi;
            return lo + rng.next01() * (hi - lo);
        }

        void scheduleNextArrival(double base){
            double ia = u(cfg.minArrival, cfg.maxArrival);
            calendar.add(new Event(EvType.ARRIVAL, base + ia));
        }

        void scheduleDeparture(double base){
            double st = u(cfg.minService, cfg.maxService);
            double t  = base + st;
            departures.add(t);
            calendar.add(new Event(EvType.DEPARTURE, t));
        }

        void run() {
            scheduleNextArrival(0.0);

            while (rng.hasNext() && !calendar.isEmpty()) {
                Event e = calendar.poll();

                double dt = e.time - lastEvtTime;
                timeInState[inSystem] += dt;
                lastEvtTime = e.time;
                now = e.time;

                if (e.type == EvType.ARRIVAL) {
                    scheduleNextArrival(e.time);
                    if (inSystem >= cfg.capacity) {
                        losses++;
                    } else {
                        inSystem++;
                        if (departures.size() < cfg.servers) {
                            scheduleDeparture(now);
                        }
                    }
                } else {
                    if (!departures.isEmpty() && Math.abs(departures.peek() - e.time) < 1e-9) {
                        departures.poll();
                    }
                    if (inSystem > 0) inSystem--;
                    if (inSystem >= cfg.servers) {
                        scheduleDeparture(now);
                    }
                }
            }

            double total = 0;
            for (double t : timeInState) total += t;

            System.out.println("*********************************************************");
            System.out.printf("Queue:   Queue1 (G/G/%d/%d)%n", cfg.servers, cfg.capacity);
            System.out.printf("Arrival: %.1f ... %.1f%n", cfg.minArrival, cfg.maxArrival);
            System.out.printf("Service: %.1f ... %.1f%n", cfg.minService, cfg.maxService);
            System.out.println("*********************************************************");
            System.out.printf("%6s %18s %20s%n", "State", "Time", "Probability");
            for (int s=0; s<timeInState.length; s++) {
                double p = total > 0 ? (100.0 * timeInState[s] / total) : 0.0;
                System.out.printf("%6d %18.4f %19.2f%%%n", s, timeInState[s], p);
            }
            System.out.println();
            System.out.printf("Number of losses: %d%n", losses);
        }
    }

    static QueueCfg parseYaml(String file) throws Exception {
        QueueCfg cfg = new QueueCfg();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("servers:")) cfg.servers = Integer.parseInt(line.split(":")[1].trim());
                if (line.startsWith("capacity:")) cfg.capacity = Integer.parseInt(line.split(":")[1].trim());
                if (line.startsWith("minArrival:")) cfg.minArrival = Double.parseDouble(line.split(":")[1].trim());
                if (line.startsWith("maxArrival:")) cfg.maxArrival = Double.parseDouble(line.split(":")[1].trim());
                if (line.startsWith("minService:")) cfg.minService = Double.parseDouble(line.split(":")[1].trim());
                if (line.startsWith("maxService:")) cfg.maxService = Double.parseDouble(line.split(":")[1].trim());
            }
        }
        return cfg;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2 || !args[0].equals("run")) {
            System.out.println("Uso: java Main run modelo.yml");
            return;
        }
        QueueCfg cfg = parseYaml(args[1]);
        LCG rng = new LCG(41, 100000);
        SingleQueueSim sim = new SingleQueueSim(cfg, rng);
        sim.run();
    }
}
