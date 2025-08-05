package dev.peterkmg.simulation.animals;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import dev.peterkmg.simulation.Simulation;
import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.CellType;
import dev.peterkmg.simulation.farm.Farm;
import dev.peterkmg.utils.StreamUtils;

public abstract class Animal implements Runnable {

  protected final Farm farm;
  protected final CellType type;
  protected final int ownerId;

  protected int row;
  protected int col;


  public Animal(Farm farm, int row, int col, CellType type, int ownerId) {
    this.farm = farm;
    this.ownerId = ownerId;

    this.row = row;
    this.col = col;

    this.type = type;
    farm.getCell(row, col).setCellType(type);
    farm.getCell(row, col).setOwner(ownerId);
  }

  @Override
  public void run() {
    var self = this.farm.getCell(this.row, this.col);
    var hood = self.getNeighborhood();

    this.acquireCellLocks(hood);

    try {
      this.moveSelf(self, hood);
    } finally {
      hood.forEach(n -> n.getLock().unlock());
    }
  }

  protected void acquireCellLocks(List<Cell> hood) {
    hood.forEach(c -> {
      try {
        c.getLock().lockInterruptibly();
      } catch (InterruptedException e) {
        hood.forEach(n -> n.getLock().unlock()); // try to release
        System.err.println("Animal Thread interrupted.");
        Thread.currentThread().interrupt();
      }
    });
  }

  protected abstract void moveSelf(Cell self, List<Cell> hood);

  protected void moveSelfRandomly(Predicate<Cell> predicate, Cell self, List<Cell> hood) {
    var destinations = hood.stream().filter(predicate).collect(StreamUtils.toShuffledList());

    if (destinations.isEmpty())
      return;

    var destination = destinations.get(ThreadLocalRandom.current().nextInt(destinations.size()));

    this.move(self, destination);
  }

  protected void move(Cell srcCell, Cell dstCell) {
    var dstType = dstCell.getCellType();

    srcCell.setCellType(srcCell.getOriginalCellType());
    dstCell.setCellType(this.type);
    dstCell.setOwner(this.ownerId);

    this.row = dstCell.getRow();
    this.col = dstCell.getCol();

    this.submitResult(srcCell, dstCell, dstType);
  }

  private void submitResult(Cell srcCell, Cell dstCell, CellType dstType) {
    var s = Simulation.getInstance();

    s.submitResult(srcCell, dstCell);

    if (dstType == CellType.GATE && this.type == CellType.SHEEP) {
      s.setFinished(true);
    }
  }

  public CellType getType() {
    return type;
  }
}
