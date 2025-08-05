package dev.peterkmg.simulation.farm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.peterkmg.simulation.animals.Dog;
import dev.peterkmg.simulation.animals.Sheep;
import dev.peterkmg.utils.StreamUtils;

public class Farm {

  @Getter
  private final int rows, cols;
  @Getter
  private final Cell[][] field;
  @Getter
  private final Dog[] dogs;
  @Getter
  private final Sheep[] sheep;

  public Farm(int rows, int cols, int dogsCount, int sheepCount) {
    this.rows = rows;
    this.cols = cols;

    field = initField(rows, cols);

    assignNeighborhoods();

    dogs = initDogs(dogsCount);
    sheep = initSheep(sheepCount);
  }

  private Cell[][] initField(int rows, int cols) {
    // order t, b, l, r
    var gates = IntStream.range(0, 4)
        .map(i -> ThreadLocalRandom.current().nextInt(1, i < 2 ? cols - 2 : rows - 2)).toArray();

    return IntStream.range(0, rows).mapToObj(row -> IntStream.range(0, cols).mapToObj(col -> {
      // unreadable zone calculation, 1-9, 5 is the center one
      // center zone are smaller than side zones, ex 5|4|5 = 14
      var zoneId = 3 * (row < (rows + 1) / 3 ? 0 : (row < rows - (rows + 1) / 3 ? 1 : 2))
          + (col < (cols + 1) / 3 ? 0 : (col < cols - (cols + 1) / 3 ? 1 : 2)) + 1;

      if (row == 0) {
        return new Cell(row, col, gates[0] == col ? CellType.GATE : CellType.WALL, zoneId);
      } else if (row == rows - 1) {
        return new Cell(row, col, gates[1] == col ? CellType.GATE : CellType.WALL, zoneId);
      } else if (col == 0) {
        return new Cell(row, col, gates[2] == row ? CellType.GATE : CellType.WALL, zoneId);
      } else if (col == cols - 1) {
        return new Cell(row, col, gates[3] == row ? CellType.GATE : CellType.WALL, zoneId);
      } else {
        return new Cell(row, col, CellType.EMPTY, zoneId);
      }
    }).toArray(Cell[]::new)).toArray(Cell[][]::new);
  }

  private void assignNeighborhoods() {
    Arrays.stream(field).flatMap(Arrays::stream).parallel().forEach(c -> {
      var list = IntStream.range(-1, 2)
          .mapToObj(dr -> IntStream.range(-1, 2)
              .filter(dc -> c.getRow() + dr >= 0 && c.getRow() + dr < rows && c.getCol() + dc >= 0
                  && c.getCol() + dc < cols)
              .mapToObj(dc -> field[c.getRow() + dr][c.getCol() + dc]).toList())
          .flatMap(List::stream).toList();

      c.setNeighborhood(list);
    });
  }

  public Dog[] initDogs(int dogsCount) {
    var minDogsPerZone = dogsCount < 9 ? 1 : dogsCount / 9;

    // distribute dogs to zones equally
    var dCells = IntStream.range(1, 10).filter(i -> i != 5).limit(dogsCount).boxed()
        .collect(StreamUtils.toShuffledList()).stream()
        .map(i -> Arrays.stream(field).flatMap(Arrays::stream).filter(c -> c.getZoneId() == i)
            .collect(StreamUtils.toShuffledList()).subList(0, minDogsPerZone))
        .flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));

    // distribute rest of the dogs randomly
    if (dogsCount > dCells.size()) {
      dCells.addAll(Arrays.stream(field).flatMap(Arrays::stream)
          .filter(c -> !dCells.contains(c) && c.getZoneId() != 5)
          .collect(StreamUtils.toShuffledList()).subList(0, dogsCount - dCells.size()));
    }

    int[] id = {0};

    return dCells.stream().map(c -> new Dog(this, c.getRow(), c.getCol(), id[0]++))
        .toArray(Dog[]::new);
  }

  public Sheep[] initSheep(int sheepCount) {
    var sCells = Arrays.stream(field).flatMap(Arrays::stream).filter(c -> c.getZoneId() == 5)
        .collect(StreamUtils.toShuffledList()).subList(0, sheepCount);

    int[] id = {'A'};
    return sCells.stream().map(c -> new Sheep(this, c.getRow(), c.getCol(), id[0]++))
        .toArray(Sheep[]::new);
  }

  public Cell getCell(int row, int col) { return field[row][col]; }

  public int getDogsCount() { return dogs.length; }
  public int getSheepCount() { return sheep.length; }

}
