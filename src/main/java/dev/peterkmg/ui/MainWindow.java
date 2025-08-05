package dev.peterkmg.ui;

import javax.swing.JFrame;

import dev.peterkmg.config.AppConfig;

public class MainWindow extends JFrame {

  private final ContentPanel mainPanel = new ContentPanel();

  public MainWindow() {
    super(AppConfig.APP_NAME);
    setupSelf();
  }

  private void setupSelf() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    add(mainPanel);

    pack();

    // pop at the center of the screen
    var w = getWidth();
    var h = getHeight();
    var p = getLocation();
    setLocation(p.x - w / 2, p.y - h / 2);
  }
}
