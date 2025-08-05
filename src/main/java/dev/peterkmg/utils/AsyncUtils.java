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
import dev.peterkmg.simulation.animals.Animal;
import dev.peterkmg.simulation.farm.Cell;

public class AsyncUtils {
  public static class AnimalThreadFactory implements ThreadFactory {

    private int idx = 0;
    private int dogCount;
    private int sheepCount;

    public AnimalThreadFactory(int dCnt, int sCnt) {
      this.dogCount = dCnt;
      this.sheepCount = sCnt;
    }

    @Override
    public Thread newThread(Runnable r) {
      var t = new Thread(r);
      var label = idx < dogCount ? "Dog" : "Sheep";
      t.setName(String.format("AnimalThreadPool-%s-%d", label, idx++));
      return t;
    }
  }

  public static class ResultSubscriber implements Subscriber<Cell> {
    private final String name;

    private Subscription subscription;
    private Consumer<Cell> onNextCallback;
    private Runnable onCompleteCallback;

    public ResultSubscriber(String name, Consumer<Cell> onNext, Runnable onComplete) {
      this.name = name;
      this.onNextCallback = onNext;
      this.onCompleteCallback = onComplete;
    }

    @Override
    public void onSubscribe(Subscription s) {
      this.subscription = s;
      s.request(1);
    }

    @Override
    public void onNext(Cell cell) {
      this.onNextCallback.accept(cell);
      this.subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
      System.err.println(String.format("Error in %s: %s", name, t.getMessage()));
    }

    @Override
    public void onComplete() {
      this.onCompleteCallback.run();
    }

    public void cancelSubscription() {
      this.subscription.cancel();
    }
  }

  public static class AnimalExecutor extends ScheduledThreadPoolExecutor {
    private final Runnable onShutdown;
    private final int interval;

    public AnimalExecutor(int dCnt, int sCnt, Animal[] dogs, Animal[] sheep, int ival,
        Runnable onShut) {
      super(dCnt + sCnt, new AnimalThreadFactory(dCnt, sCnt));
      // jumping through hoops to name threads
      this.interval = ival;
      this.onShutdown = onShut;
      this.startProcessing(Stream.of(dogs, sheep).flatMap(Arrays::stream).toList());
    }

    private void startProcessing(List<Animal> animals) {
      animals.forEach(a -> this.scheduleAtFixedRate(a, 0, this.interval, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
      if (!Simulation.getInstance().isFinished())
        return;

      this.shutdownSelf();
      Thread.ofVirtual().start(this.onShutdown);
    }

    private synchronized void shutdownSelf() {
      if (this.isTerminating() || this.isTerminated() || this.isShutdown())
        return;

      this.shutdown();
    }
  }
}
