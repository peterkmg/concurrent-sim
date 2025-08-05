package dev.peterkmg.simulation.animals;

import lombok.Getter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import dev.peterkmg.simulation.Simulation;
import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.CellType;
import dev.peterkmg.simulation.farm.Farm;
import dev.peterkmg.utils.StreamUtils;

public abstract class AbstractAnimal implements Runnable {

  private final Farm farm;

  protected int row, col;

  @Getter
  private final CellType type;
  private final int ownerId;

  public AbstractAnimal(Farm farm, int row, int col, CellType type, int ownerId) {
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
    var self = farm.getCell(row, col);
    var hood = self.getNeighborhood();

    acquireCellLocks(hood);

    try {
      moveSelf(self, hood);
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

    if (destinations.isEmpty()) { return; }

    move(self, destinations.get(ThreadLocalRandom.current().nextInt(destinations.size())));
  }

  protected void move(Cell srcCell, Cell dstCell) {
    var dstType = dstCell.getCellType();

    srcCell.setCellType(srcCell.getOriginalCellType());
    dstCell.setCellType(type);
    dstCell.setOwner(ownerId);

    row = dstCell.getRow();
    col = dstCell.getCol();

    submitResult(srcCell, dstCell, dstType);
  }

  private void submitResult(Cell srcCell, Cell dstCell, CellType dstType) {
    var s = Simulation.getInstance();
    s.submitResult(srcCell, dstCell);
    if (dstType == CellType.GATE && type == CellType.SHEEP) { s.setFinished(true); }
  }

}
