package dev.peterkmg.simulation;

import java.util.Arrays;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.Farm;
import dev.peterkmg.utils.AsyncUtils.AnimalExecutor;

public class Simulation {

  private static Simulation instance;

  private SubmissionPublisher<Cell> resultPublisher;

  public AnimalExecutor dogsExec;
  public AnimalExecutor sheepExec;
  public AnimalExecutor animalExec;

  private Farm farm;

  private int updateInterval;
  public boolean isFinished = false;

  private Simulation() {}

  public static Simulation getInstance() {
    if (instance == null)
      instance = new Simulation();
    return instance;
  }

  public void initSimulation(int rows, int cols, int dogsCount, int sheepCount, int update) {
    this.updateInterval = update;
    this.farm = new Farm(rows, cols, dogsCount, sheepCount);
    this.resultPublisher = new SubmissionPublisher<>();
  }

  public void resetSimulation() {
    var rows = this.farm.getRows();
    var cols = this.farm.getCols();
    var dogsCount = this.farm.getDogsCount();
    var sheepCount = this.farm.getSheepCount();
    this.initSimulation(rows, cols, dogsCount, sheepCount, this.updateInterval);
  }

  public void startSimulation() {
    this.isFinished = false;
    var dogs = this.farm.getDogs();
    var dogsCount = this.farm.getDogsCount();
    var sheep = this.farm.getSheep();
    var sheepCount = this.farm.getSheepCount();

    this.animalExec = new AnimalExecutor(dogsCount, sheepCount, dogs, sheep, this.updateInterval,
        this::onExecShutdown);
  }

  private void onExecShutdown() {
    try {
      var at = this.animalExec.awaitTermination(this.updateInterval, TimeUnit.MILLISECONDS);
      if (!at) {
        System.err.println("Animal thread pool did not shutdown gracefully");
        this.animalExec.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.err.println("Error while shutting down thread pools");
      Thread.currentThread().interrupt();
    } finally {
      this.resultPublisher.close();
    }
  }

  public void stopSimulation() {
    try {
      this.animalExec.shutdown();
      if (!this.animalExec.awaitTermination(this.updateInterval, TimeUnit.MILLISECONDS)) {
        System.err.println("Animal thread pool did not shutdown gracefully");
        this.animalExec.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.err.println("Error while shutting down thread pools");
      Thread.currentThread().interrupt();
    }
  }

  public Cell[][] getField() {
    return farm.getField();
  }

  public int getRows() {
    return farm.getRows();
  }

  public int getCols() {
    return farm.getCols();
  }

  public void submitResult(Cell... cells) {
    Arrays.stream(cells).forEach(resultPublisher::submit);
  }

  public void subscribe(Subscriber<Cell> subscriber) {
    this.resultPublisher.subscribe(subscriber);
  }

  public synchronized boolean isFinished() {
    return this.isFinished;
  }

  public synchronized void setFinished(boolean isFinished) {
    this.isFinished = isFinished;
  }
}
