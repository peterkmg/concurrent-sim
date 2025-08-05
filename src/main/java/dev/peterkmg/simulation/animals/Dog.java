package dev.peterkmg.simulation.animals;

import java.util.List;
import java.util.function.Predicate;

import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.CellType;
import dev.peterkmg.simulation.farm.Farm;

public class Dog extends AbstractAnimal {

  public Dog(Farm farm, int row, int col, int ownerId) {
    super(farm, row, col, CellType.DOG, ownerId);
  }

  @Override
  protected void moveSelf(Cell self, List<Cell> hood) {
    Predicate<Cell> predicate = c -> c.getCellType() != CellType.DOG
        && c.getCellType() != CellType.SHEEP && c.getZoneId() != 5;
    moveSelfRandomly(predicate, self, hood);
  }
}
