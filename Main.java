// É necessário ter a biblioteca SnakeYAML no seu projeto.
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class Main {
    // --- Início das classes internas (Queue, LCG, Event, QueueNetworkSim) ---
    static class Queue {
        int id;
        String name;
        int servers;
        int capacity;
        double minService, maxService;
        double minArrival, maxArrival;
        int inSystem = 0;
        long losses = 0;
        double[] timeInState;
        PriorityQueue<Double> departures = new PriorityQueue<>();

        Queue(int id, String name, int servers, int capacity, double minService, double maxService, double minArrival, double maxArrival) {
            this.id = id;
            this.name = name;
            this.servers = servers;
            this.capacity = capacity;
            this.minService = minService;
            this.maxService = maxService;
            this.minArrival = minArrival;
            this.maxArrival = maxArrival;
            this.timeInState = new double[capacity + 1];
        }

        void printStats(double totalSimTime) {
            System.out.printf("**************** Fila %s (G/G/%d/%d) ****************%n", name, servers, capacity);
            System.out.printf("Service Time: %.1f ... %.1f%n", minService, maxService);
            System.out.println("---------------------------------------------------------");
            System.out.printf("%6s %20s %20s%n", "State", "Time", "Probability");
            double totalStateTime = 0;
            for (double t : timeInState) totalStateTime += t;
            for (int s = 0; s < timeInState.length; s++) {
                double p = totalStateTime > 0 ? (100.0 * timeInState[s] / totalStateTime) : 0.0;
                System.out.printf("%6d %20.4f %19.2f%%%n", s, timeInState[s], p);
            }
            System.out.printf("Perdas de clientes: %d%n", losses);
            System.out.println("*********************************************************");
        }
    }

    static class LCG {
        private final long a = 1664525L, c = 1013904223L, m = (1L << 32);
        private long state;
        private final long limit;
        private long used = 0;
        LCG(long seed, long limit) { this.state = seed % m; this.limit = limit; }
        boolean hasNext() { return used < limit; }
        double next01() { state = (a * state + c) % m; used++; return (double) state / (double) m; }
    }

    enum EvType { ARRIVAL, DEPARTURE }

    static class Event implements Comparable<Event> {
        EvType type; double time; int queueId;
        Event(EvType t, double tm, int qId) { type = t; time = tm; queueId = qId; }
        @Override public int compareTo(Event o) { return Double.compare(this.time, o.time); }
    }

    static class QueueNetworkSim {
        List<Queue> queues;
        double[][] routingMatrix;
        LCG rng;
        double now = 0.0;
        PriorityQueue<Event> calendar = new PriorityQueue<>();
        double lastEvtTime = 0.0;

        QueueNetworkSim(List<Queue> queues, double[][] routing, LCG rng) {
            this.queues = queues;
            this.routingMatrix = routing;
            this.rng = rng;
        }

        double u(double lo, double hi) { if (!rng.hasNext()) return hi; return lo + rng.next01() * (hi - lo); }

        void scheduleExternalArrival(double baseTime) {
            if (!rng.hasNext()) return;
            Queue entryQueue = queues.get(0);
            double interArrivalTime = u(entryQueue.minArrival, entryQueue.maxArrival);
            calendar.add(new Event(EvType.ARRIVAL, baseTime + interArrivalTime, 0));
        }

        void scheduleDeparture(Queue q, double baseTime) { if (!rng.hasNext()) return; double serviceTime = u(q.minService, q.maxService); double departureTime = baseTime + serviceTime; q.departures.add(departureTime); calendar.add(new Event(EvType.DEPARTURE, departureTime, q.id)); }

        void routeCustomer(int fromQueueId, double currentTime) {
            double rand = rng.next01();
            double cumulativeProb = 0.0;
            for (int toQueueId = 0; toQueueId < routingMatrix[fromQueueId].length; toQueueId++) {
                cumulativeProb += routingMatrix[fromQueueId][toQueueId];
                if (rand < cumulativeProb) {
                    calendar.add(new Event(EvType.ARRIVAL, currentTime, toQueueId));
                    return;
                }
            }
        }

        void run() {
            calendar.add(new Event(EvType.ARRIVAL, 1.5, 0));
            while (rng.hasNext() && !calendar.isEmpty()) {
                Event e = calendar.poll();
                double dt = e.time - lastEvtTime;
                for (Queue q : queues) { if (q.inSystem <= q.capacity) q.timeInState[q.inSystem] += dt; }
                lastEvtTime = e.time;
                now = e.time;
                Queue currentQueue = queues.get(e.queueId);
                if (e.type == EvType.ARRIVAL) {
                    if (e.queueId == 0) scheduleExternalArrival(e.time);
                    if (currentQueue.inSystem >= currentQueue.capacity) {
                        currentQueue.losses++;
                    } else {
                        currentQueue.inSystem++;
                        if (currentQueue.departures.size() < currentQueue.servers) scheduleDeparture(currentQueue, now);
                    }
                } else {
                    if (!currentQueue.departures.isEmpty() && Math.abs(currentQueue.departures.peek() - e.time) < 1e-9) currentQueue.departures.poll();
                    currentQueue.inSystem--;
                    if (currentQueue.inSystem >= currentQueue.servers) scheduleDeparture(currentQueue, now);
                    routeCustomer(currentQueue.id, now);
                }
            }
            System.out.println("Relatório Final da Simulação");
            System.out.println("=========================================================");
            for (Queue q : queues) { q.printStats(now); System.out.println(); }
            System.out.printf("Tempo global da simulação: %.4f%n", now);
            System.out.println("=========================================================");
        }
    }
    // --- Fim das classes internas ---

    static class SimConfig {
        long rndNumbersPerSeed;
        List<Long> seeds;
        List<Queue> queues;
        double[][] routingMatrix;
    }

    @SuppressWarnings("unchecked")
    private static SimConfig parseYamlConfig(String filename) throws Exception {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(filename);
        Map<String, Object> data = yaml.load(inputStream);
        SimConfig config = new SimConfig();
        Map<String, Object> params = (Map<String, Object>) data.get("parameters");
        config.rndNumbersPerSeed = ((Number) params.get("rndnumbersPerSeed")).longValue();
        List<Integer> intSeeds = (List<Integer>) params.get("seeds");
        config.seeds = new ArrayList<>();
        for (Integer seed : intSeeds) config.seeds.add(seed.longValue());
        Map<String, Map<String, Object>> queuesData = (Map<String, Map<String, Object>>) data.get("queues");
        config.queues = new ArrayList<>(queuesData.size());
        Map<String, Integer> queueName_to_Id = new HashMap<>();
        List<Map.Entry<String, Map<String, Object>>> sortedQueues = new ArrayList<>(queuesData.entrySet());
        sortedQueues.sort(Comparator.comparingInt(e -> (int) e.getValue().get("id")));
        for (Map.Entry<String, Map<String, Object>> entry : sortedQueues) {
            String qName = entry.getKey();
            Map<String, Object> qData = entry.getValue();
            Queue q = new Queue((int) qData.get("id"), qName, (int) qData.get("servers"), (int) qData.get("capacity"), ((Number) qData.get("minService")).doubleValue(), ((Number) qData.get("maxService")).doubleValue(), ((Number) qData.get("minArrival")).doubleValue(), ((Number) qData.get("maxArrival")).doubleValue());
            config.queues.add(q);
            queueName_to_Id.put(qName, q.id);
        }
        Map<String, Map<String, Number>> routingData = (Map<String, Map<String, Number>>) data.get("routing");
        config.routingMatrix = new double[config.queues.size()][config.queues.size()];
        if (routingData != null) {
            for (Map.Entry<String, Map<String, Number>> routeEntry : routingData.entrySet()) {
                String fromQueueName = routeEntry.getKey();
                int fromId = queueName_to_Id.get(fromQueueName);
                for (Map.Entry<String, Number> destEntry : routeEntry.getValue().entrySet()) {
                    String toQueueName = destEntry.getKey();
                    int toId = queueName_to_Id.get(toQueueName);
                    config.routingMatrix[fromId][toId] = destEntry.getValue().doubleValue();
                }
            }
        }
        return config;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2 || !args[0].equals("run")) {
            System.out.println("Uso: java Main run <arquivo_modelo.yml>");
            return;
        }
        SimConfig config = parseYamlConfig(args[1]);
        for (long seed : config.seeds) {
            System.out.printf("----------- EXECUTANDO SIMULAÇÃO COM SEED: %d -----------\n\n", seed);
            LCG rng = new LCG(seed, config.rndNumbersPerSeed);
            List<Queue> freshQueues = new ArrayList<>();
            for (Queue q_template : config.queues) {
                freshQueues.add(new Queue(q_template.id, q_template.name, q_template.servers, q_template.capacity, q_template.minService, q_template.maxService, q_template.minArrival, q_template.maxArrival));
            }
            QueueNetworkSim sim = new QueueNetworkSim(freshQueues, config.routingMatrix, rng);
            sim.run();
            System.out.println("\n");
        }
    }
}