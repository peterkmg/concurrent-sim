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

  public AnimalExecutor animalExec;

  private Farm farm;
  private int updateInterval;
  private boolean finished = false;

  private Simulation() {}

  public static Simulation getInstance() {
    if (instance == null) { instance = new Simulation(); }
    return instance;
  }

  public void initSimulation(int rows, int cols, int dogsCount, int sheepCount, int update) {
    updateInterval = update;
    farm = new Farm(rows, cols, dogsCount, sheepCount);
    resultPublisher = new SubmissionPublisher<>();
  }

  public void resetSimulation() {
    var rows = getRows();
    var cols = getCols();
    var dogsCount = farm.getDogsCount();
    var sheepCount = farm.getSheepCount();
    initSimulation(rows, cols, dogsCount, sheepCount, updateInterval);
  }

  public void startSimulation() {
    finished = false;
    var dogs = farm.getDogs();
    var dogsCount = farm.getDogsCount();
    var sheep = farm.getSheep();
    var sheepCount = farm.getSheepCount();

    animalExec = new AnimalExecutor(dogsCount, sheepCount, dogs, sheep, updateInterval,
        this::onExecShutdown);
  }

  private void onExecShutdown() {
    try {
      var at = animalExec.awaitTermination(updateInterval, TimeUnit.MILLISECONDS);
      if (!at) {
        System.err.println("Animal thread pool did not shutdown gracefully");
        animalExec.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.err.println("Error while shutting down thread pools");
      Thread.currentThread().interrupt();
    } finally {
      resultPublisher.close();
    }
  }

  public void stopSimulation() {
    try {
      animalExec.shutdown();
      if (!animalExec.awaitTermination(updateInterval, TimeUnit.MILLISECONDS)) {
        System.err.println("Animal thread pool did not shutdown gracefully");
        animalExec.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.err.println("Error while shutting down thread pools");
      Thread.currentThread().interrupt();
    }
  }

  public Cell[][] getField() { return farm.getField(); }

  public int getRows() { return farm.getRows(); }
  public int getCols() { return farm.getCols(); }

  public void submitResult(Cell... cells) { Arrays.stream(cells).forEach(resultPublisher::submit); }

  public void subscribe(Subscriber<Cell> subscriber) { resultPublisher.subscribe(subscriber); }

  public synchronized boolean isFinished() { return finished; }
  public synchronized void setFinished(boolean isFinished) { finished = isFinished; }
}
