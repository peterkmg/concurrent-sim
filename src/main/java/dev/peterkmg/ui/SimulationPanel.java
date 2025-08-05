package dev.peterkmg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import dev.peterkmg.config.AppConfig;
import dev.peterkmg.simulation.Simulation;
import dev.peterkmg.simulation.farm.Cell;
import dev.peterkmg.simulation.farm.CellType;
import dev.peterkmg.utils.AsyncUtils.ResultSubscriber;

public class SimulationPanel extends JPanel {

  private final JPanel header = new JPanel();
  private final JPanel board = new JPanel();
  private final JPanel footer = new JPanel();

  private final Runnable invokeMenuCallback;

  private ResultSubscriber resultSubscriber;

  private boolean isRunning = false;

  private JButton btnStart;
  private JButton btnStop;
  private JButton btnReset;

  public SimulationPanel(Runnable invokeMenuCallback) {
    super(new BorderLayout(20, 20));
    this.invokeMenuCallback = invokeMenuCallback;

    setupSelf();
    setupHeader();
    setupFooter();
  }

  private void setupSelf() {
    add(header, BorderLayout.NORTH);
    add(board, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
  }

  private void setupHeader() {
    btnStart = new JButton("Start Simulation");
    btnStart.addActionListener(e -> toggleStartStop());
    header.add(btnStart);

    btnStop = new JButton("Stop Simulation");
    btnStop.addActionListener(e -> toggleStartStop());
    btnStop.setEnabled(false);
    header.add(btnStop);

    btnReset = new JButton("Reset Simulation");
    btnReset.addActionListener(e -> resetSimulation());
    header.add(btnReset);
  }

  private void setupFooter() {
    var btnMenu = new JButton("Back to menu");
    btnMenu.addActionListener(e -> {
      if (isRunning) { toggleStartStop(); }
      shutdownSubscriber();
      invokeMenuCallback.run(); // return to menu
    });

    footer.add(btnMenu);
  }

  private void toggleStartStop() {
    if (isRunning) {
      stopSimulation();
    } else {
      startSimulation();
    }

    isRunning = !isRunning;
  }

  private void startSimulation() {
    Simulation.getInstance().startSimulation();
    btnStart.setEnabled(false);
    btnReset.setEnabled(false);
    btnStop.setEnabled(true);
  }

  private void stopSimulation() {
    Simulation.getInstance().stopSimulation();
    btnStop.setEnabled(false);
    btnStart.setEnabled(true);
    btnReset.setEnabled(true);
  }

  private void resetSimulation() {
    Simulation.getInstance().resetSimulation();
    shutdownSubscriber();
    initSimulationGUI();
  }

  private Border createBorder(int row, int col, int rows, int cols, CellType ct) {
    // there's probably a way to do it smarter
    if (row < rows - 1 && col < cols - 1 && ct != CellType.GATE) {
      return BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY);
    } else if (row < rows - 1 && col == cols - 1 && ct != CellType.GATE) {
      return BorderFactory.createMatteBorder(1, 1, 0, 1, Color.GRAY);
    } else if (row == rows - 1 && col < cols - 1 && ct != CellType.GATE) {
      return BorderFactory.createMatteBorder(1, 1, 1, 0, Color.GRAY);
    } else if (row == rows - 1 && col == cols - 1 && ct != CellType.GATE) {
      return BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
    } else if (ct == CellType.GATE && row == 0) {
      return BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY);
    } else if (ct == CellType.GATE && col == 0) {
      return BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY);
    } else if (ct == CellType.GATE && (row == rows - 1 || col == cols - 1)) {
      return BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY);
    } else {
      return BorderFactory.createEmptyBorder();
    }
  }

  public void initSimulationGUI() {
    var s = Simulation.getInstance();

    var rows = s.getRows();
    var cols = s.getCols();

    board.removeAll();
    var layout = new GridLayout(rows, cols);
    board.setLayout(layout);

    Arrays.stream(s.getField()).flatMap(Arrays::stream).forEach(cell -> {
      var ct = cell.getCellType();
      String text = "";
      if (ct == CellType.DOG) {
        text = String.valueOf(cell.getOwner());
      } else if (ct == CellType.SHEEP) { text = String.valueOf((char) cell.getOwner()); }
      var color = Color.decode(ct.getColor());
      var label = new JLabel(text, JLabel.CENTER);
      label.setOpaque(true);
      label.setFont(AppConfig.FONT_SMALL);
      label.setBackground(color);
      label.setBorder(createBorder(cell.getRow(), cell.getCol(), rows, cols, ct));
      board.add(label);
    });

    btnStart.setEnabled(true);
    btnStop.setEnabled(false);
    btnReset.setEnabled(true);

    board.revalidate();
    board.repaint();

    initSubscriber();
  }

  private void initSubscriber() {
    resultSubscriber =
        new ResultSubscriber("UISubscriber", this::subscriberUpdate, this::subscriberFinish);
    Simulation.getInstance().subscribe(resultSubscriber);
  }

  private void subscriberUpdate(Cell cell) {
    var idx = cell.getRow() * Simulation.getInstance().getCols() + cell.getCol();
    var label = (JLabel) board.getComponent(idx);

    var text = switch (cell.getCellType()) {
      case DOG -> String.valueOf(cell.getOwner());
      case SHEEP -> String.valueOf((char) cell.getOwner());
      default -> "";
    };

    label.setText(text);
    label.setBackground(Color.decode(cell.getCellType().getColor()));
  }

  private void subscriberFinish() {
    isRunning = false;
    btnStart.setEnabled(false);
    btnStop.setEnabled(false);
    btnReset.setEnabled(true);
    JOptionPane.showMessageDialog(this, "Simulation finished successfully.", "Complete",
        JOptionPane.WARNING_MESSAGE);
  }

  private void shutdownSubscriber() { resultSubscriber.cancelSubscription(); }

}
