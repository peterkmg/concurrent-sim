package dev.peterkmg.utils;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import dev.peterkmg.simulation.Simulation;
import dev.peterkmg.simulation.animals.AbstractAnimal;
import dev.peterkmg.simulation.farm.Cell;

public class AsyncUtils {
  public static class AnimalThreadFactory implements ThreadFactory {

    private int idx = 0;
    private int dogsCount;
    private int sheepCount;

    public AnimalThreadFactory(int dogsCount, int sheepCount) {
      this.dogsCount = dogsCount;
      this.sheepCount = sheepCount;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      var thread = new Thread(runnable);
      var label = idx < dogsCount ? "Dog" : "Sheep";
      thread.setName(String.format("AnimalThreadPool-%s-%d", label, idx++));
      return thread;
    }
  }

  public static class ResultSubscriber implements Subscriber<Cell> {
    private final String name;

    private Subscription subscription;
    private Consumer<Cell> onNextCallback;
    private Runnable onCompleteCallback;

    public ResultSubscriber(String name, Consumer<Cell> onNext, Runnable onComplete) {
      this.name = name;
      onNextCallback = onNext;
      onCompleteCallback = onComplete;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
      this.subscription = subscription;
      subscription.request(1);
    }

    @Override
    public void onNext(Cell cell) {
      onNextCallback.accept(cell);
      subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
      System.err.println(String.format("Error in %s: %s", name, throwable.getMessage()));
    }

    @Override
    public void onComplete() { onCompleteCallback.run(); }

    public void cancelSubscription() { subscription.cancel(); }
  }

  public static class AnimalExecutor extends ScheduledThreadPoolExecutor {
    private final Runnable onShutdownCallback;
    private final int interval;

    public AnimalExecutor(
        int dogsCount, int sheepCount,
        AbstractAnimal[] dogs, AbstractAnimal[] sheep,
        int interval, Runnable onShutdown) {
      super(dogsCount + sheepCount, new AnimalThreadFactory(dogsCount, sheepCount));
      // jumping through hoops to name threads
      this.interval = interval;
      onShutdownCallback = onShutdown;
      startProcessing(Stream.of(dogs, sheep).flatMap(Arrays::stream).toList());
    }

    private void startProcessing(List<AbstractAnimal> animals) {
      animals.forEach(a -> scheduleAtFixedRate(a, 0, interval, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
      if (!Simulation.getInstance().isFinished()) { return; }
      shutdownSelf();
      Thread.ofVirtual().start(onShutdownCallback);
    }

    private synchronized void shutdownSelf() {
      if (isTerminating() || isTerminated() || isShutdown()) { return; }
      shutdown();
    }
  }
}
