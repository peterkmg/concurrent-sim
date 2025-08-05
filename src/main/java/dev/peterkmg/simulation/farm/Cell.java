package dev.peterkmg.simulation.farm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {
  @Getter
  private final int row, col, zoneId;

  @Getter
  private final CellType originalCellType;

  @Getter
  @Setter
  private CellType cellType;

  @Getter
  @Setter
  private List<Cell> neighborhood;

  @Getter
  @Setter
  private int owner;

  @Getter
  private final Lock lock = new ReentrantLock(true);

  public Cell(int row, int col, CellType cellType, int zoneId) {
    this.row = row;
    this.col = col;
    this.zoneId = zoneId;
    this.originalCellType = cellType;
    this.cellType = cellType;
  }
}
