# Queue Simulator

Java implementation of a **G/G/n/k queue simulation system**.  
This project is part of a practical assignment focused on simulating simple queues and analyzing their performance under different configurations.  

## 📌 Overview

This simulator models **general arrival and service distributions** (`G/G/n/k`) with customizable parameters.  
It allows you to experiment with **different queue settings** by defining the number of servers, queue capacity, and random arrival/service times within configurable ranges.

The simulation produces statistical results such as:  

- Queue configuration (`G/G/servers/capacity`)  
- Arrival and service time ranges  
- State distribution table (time spent in each state and corresponding probabilities)  
- Number of clients lost due to queue capacity limits  
- Total simulation time  

## 🚀 How to Run

### 1. Compile the project
```bash
javac Main.java
```

### 2. Run the simulation
```bash
java Main run modelo.yml
```

## ⚙️ Configuration File

The simulator uses a YAML file (`modelo.yml`) to define queue parameters:

- `servers`: Number of servers in the system  
- `capacity`: Maximum number of clients allowed in the queue (including those being served)  
- `minArrival` / `maxArrival`: Minimum and maximum inter-arrival times  
- `minService` / `maxService`: Minimum and maximum service times  

### Example `modelo.yml`
```yaml
!PARAMETERS

rndnumbersPerSeed: 100000
seeds:
- 41

arrivals: 
   Queue1: 2.0

queues: 
   Queue1: 
      servers: 2
      capacity: 5
      minArrival: 2.0
      maxArrival: 5.0
      minService: 3.0
      maxService: 5.0
```

## 📊 Example Output

```
*********************************************************
Queue:   Queue1 (G/G/1/5)
Arrival: 2.0 ... 5.0
Service: 3.0 ... 5.0
*********************************************************
   State               Time          Probability
      0             3.9046                0.00%
      1            27.2276                0.03%
      2           108.6892                0.13%
      3          7419.7764                3.97%
      4         98430.3752               52.72%
      5         80569.8635               43.15%

Number of losses: 6685
```

## 📂 Project Structure

```
M4_SMA/
├── Main.java      # Main simulation code
├── modelo.yml     # Queue configuration file
└── README.md      # This documentation
```

---

## 🎯 Assignment Context

For validation purposes, besides the source code, you must also deliver the **simulation results** for the following queues:

- `G/G/1/5`, arrivals in [2, 5], service in [3, 5]  
- `G/G/2/5`, arrivals in [2, 5], service in [3, 5]  

Simulation requirements:
- Start with an **empty queue** and the **first customer arriving at time 2.0**  
- Run the simulation with **100,000 random numbers**  
- Report:  
  - Probability distribution of queue states  
  - Accumulated times per state  
  - Number of lost customers (if any)  
  - Total simulation time  
