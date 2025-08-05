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

    this.setupSelf();
    this.setupHeader();
    this.setupBoard();
    this.setupFooter();
  }

  private void setupSelf() {
    this.add(header, BorderLayout.NORTH);
    this.add(board, BorderLayout.CENTER);
    this.add(footer, BorderLayout.SOUTH);
  }

  private void setupHeader() {
    this.btnStart = new JButton("Start Simulation");
    this.btnStart.addActionListener(e -> this.toggleStartStop());
    header.add(this.btnStart);

    this.btnStop = new JButton("Stop Simulation");
    this.btnStop.addActionListener(e -> this.toggleStartStop());
    this.btnStop.setEnabled(false);
    header.add(this.btnStop);

    this.btnReset = new JButton("Reset Simulation");
    this.btnReset.addActionListener(e -> this.resetSimulation());
    header.add(this.btnReset);
  }

  private void setupBoard() {
    // board.setOpaque(true);
    // board.setBackground(Color.WHITE);
  }

  private void setupFooter() {
    var btnMenu = new JButton("Back to menu");
    btnMenu.addActionListener(e -> {
      if (this.isRunning)
        this.toggleStartStop();

      this.shutdownSubscriber();
      this.invokeMenuCallback.run(); // return to menu
    });
    footer.add(btnMenu);
  }

  private void toggleStartStop() {
    if (this.isRunning) {
      this.stopSimulation();
    } else {
      this.startSimulation();
    }
    this.isRunning = !this.isRunning;
  }

  private void startSimulation() {
    Simulation.getInstance().startSimulation();
    this.btnStart.setEnabled(false);
    this.btnReset.setEnabled(false);
    this.btnStop.setEnabled(true);
  }

  private void stopSimulation() {
    Simulation.getInstance().stopSimulation();
    this.btnStop.setEnabled(false);
    this.btnStart.setEnabled(true);
    this.btnReset.setEnabled(true);
  }

  private void resetSimulation() {
    Simulation.getInstance().resetSimulation();
    this.shutdownSubscriber();
    this.initSimulationGUI();
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

    this.board.removeAll();
    var layout = new GridLayout(rows, cols);
    this.board.setLayout(layout);

    Arrays.stream(s.getField()).flatMap(Arrays::stream).forEach(cell -> {
      var ct = cell.getCellType();
      String text = "";
      if (ct == CellType.DOG) {
        text = String.valueOf(cell.getOwner());
      } else if (ct == CellType.SHEEP) {
        text = String.valueOf((char) cell.getOwner());
      }
      var color = Color.decode(ct.getColor());
      var label = new JLabel(text, JLabel.CENTER);
      label.setOpaque(true);
      label.setFont(AppConfig.FONT_SMALL);
      label.setBackground(color);
      label.setBorder(this.createBorder(cell.getRow(), cell.getCol(), rows, cols, ct));
      board.add(label);
    });

    this.btnStart.setEnabled(true);
    this.btnStop.setEnabled(false);
    this.btnReset.setEnabled(true);

    this.board.revalidate();
    this.board.repaint();

    this.initSubscriber();
  }

  private void initSubscriber() {
    this.resultSubscriber =
        new ResultSubscriber("UISubscriber", this::subscriberUpdate, this::subscriberFinish);
    Simulation.getInstance().subscribe(this.resultSubscriber);
  }

  private void subscriberUpdate(Cell cell) {
    var idx = cell.getRow() * Simulation.getInstance().getCols() + cell.getCol();
    var label = (JLabel) this.board.getComponent(idx);
    var text = "";
    if (cell.getCellType() == CellType.DOG) {
      text = String.valueOf(cell.getOwner());
    } else if (cell.getCellType() == CellType.SHEEP) {
      text = String.valueOf((char) cell.getOwner());
    }
    label.setText(text);
    label.setBackground(Color.decode(cell.getCellType().getColor()));
  }

  private void subscriberFinish() {
    this.isRunning = false;
    this.btnStart.setEnabled(false);
    this.btnStop.setEnabled(false);
    this.btnReset.setEnabled(true);
    JOptionPane.showMessageDialog(this, "Simulation finished successfully.", "Complete",
        JOptionPane.WARNING_MESSAGE);
  }

  private void shutdownSubscriber() {
    this.resultSubscriber.cancelSubscription();
  }

}
