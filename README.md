# ConcurrentSim

Simulation for educational purposes demonstrating deadlock-free concurrent programming using the dining philosophers approach.

## Concept

Dogs herd sheep on a farm grid where each cell has a lock. Animals must acquire locks on their neighborhood before moving, preventing deadlocks through ordered lock acquisition - similar to the classic dining philosophers problem.

- **Dogs** patrol outer zones, avoiding sheep areas
- **Sheep** flee from dogs and try to escape through gates
- **Hundreds of threads** (one per animal) run concurrently without deadlocks
- **Real-time GUI** visualizes the thread-safe simulation

## Technical Highlights

- **ReentrantLocks** with fair scheduling on each farm cell
- **Ordered lock acquisition** prevents circular wait conditions
- **ScheduledThreadPoolExecutor** coordinates animal movement
- **Flow API** provides real-time UI updates

Built with Java 21, Maven, and Swing to demonstrate safe multi-threaded programming concepts.
