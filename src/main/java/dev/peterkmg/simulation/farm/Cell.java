package dev.peterkmg.simulation.farm;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {
  private final int row;
  private final int col;
  private final int zoneId;
  private final CellType originalCellType;

  private CellType cellType;
  private List<Cell> neighborhood;

  private int owner;

  private final Lock lock = new ReentrantLock(true);

  public Cell(int row, int col, CellType cellType, int zoneId) {
    this.row = row;
    this.col = col;
    this.zoneId = zoneId;
    this.cellType = cellType;
    this.originalCellType = cellType;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public int getZoneId() {
    return zoneId;
  }

  public CellType getCellType() {
    return this.cellType;
  }

  public void setCellType(CellType ct) {
    this.cellType = ct;
  }

  public CellType getOriginalCellType() {
    return originalCellType;
  }

  public List<Cell> getNeighborhood() {
    return neighborhood;
  }

  public void setNeighborhood(List<Cell> neighborhood) {
    this.neighborhood = neighborhood;
  }


  public Lock getLock() {
    return lock;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }
}
