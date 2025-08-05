package dev.peterkmg.simulation.animals;

import java.util.List;
import java.util.function.Predicate;

import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.CellType;
import dev.peterkmg.simulation.farm.Farm;

public class Sheep extends Animal {

  public Sheep(Farm farm, int row, int col, int ownerId) {
    super(farm, row, col, CellType.SHEEP, ownerId);
  }

  @Override
  protected void moveSelf(Cell self, List<Cell> hood) {
    // choose a random dog to run away from (in case more than one around)
    var dog = hood.stream().filter(c -> c.getCellType() == CellType.DOG).findAny();

    dog.ifPresentOrElse(d -> {
      var next = hood.stream()
          .filter(c -> c.getRow() == this.row + (this.row - d.getRow())
              && c.getCol() == this.col + (this.col - d.getCol()) && c.getCellType() != CellType.DOG
              && c.getCellType() != CellType.SHEEP)
          .findAny();

      next.ifPresent(to -> this.move(self, to));
    }, () -> {
      Predicate<Cell> predicate = c -> c.getCellType() != CellType.SHEEP;
      this.moveSelfRandomly(predicate, self, hood);
    });
  }

}
