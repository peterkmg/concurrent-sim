package dev.peterkmg.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

import dev.peterkmg.config.AppConfig;
import dev.peterkmg.simulation.Simulation;

public class ContentPanel extends JPanel {

  private final MenuPanel menuPanel = new MenuPanel(this::transitionToGame);
  private final SimulationPanel gamePanel = new SimulationPanel(this::transitionToMenu);

  private final CardLayout cardLayout = new CardLayout();

  public ContentPanel() {
    super();

    setupSelf();
    transitionToMenu();
  }

  private void setupSelf() {
    var d = new Dimension(AppConfig.APP_WIDTH, AppConfig.APP_HEIGHT);
    setMinimumSize(d);
    setPreferredSize(d);

    var m = AppConfig.CONTENT_MARGIN;
    var border = BorderFactory.createEmptyBorder(m, m, m, m);
    setBorder(border);

    setLayout(cardLayout);
    add(menuPanel, "menu");
    add(gamePanel, "game");
  }

  public void transitionToMenu() { cardLayout.show(this, "menu"); }

  public void transitionToGame(int[] opts) {
    Simulation.getInstance().initSimulation(opts[0], opts[1], opts[2], opts[3], opts[4]);
    gamePanel.initSimulationGUI();
    cardLayout.show(this, "game");
  }

}
