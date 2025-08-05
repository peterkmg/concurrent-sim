package dev.peterkmg.simulation.farm;

public enum CellType {
  EMPTY,
  WALL,
  GATE,
  SHEEP,
  DOG;

  public String getLabel() {
    return switch (this) {
      case SHEEP -> "Sheep";
      case DOG -> "Dog";
      default -> "";
    };
  }

  public String getColor() {
    return switch (this) {
      case SHEEP -> "#A8D1D1";
      case DOG -> "#DF8A8A";
      case WALL -> "#DED6CE";
      case GATE -> "#F1F7B5";
      default -> "#FFFFFF";
    };
  }
}
