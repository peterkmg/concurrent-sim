package dev.peterkmg.ui;

import javax.swing.JFrame;

import dev.peterkmg.config.AppConfig;

public class MainWindow extends JFrame {

  private final ContentPanel mainPanel = new ContentPanel();

  public MainWindow() {
    super(AppConfig.APP_NAME);

    this.setupSelf();
  }

  private void setupSelf() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setResizable(false);

    this.add(mainPanel);

    this.pack();

    // pop at the center of the screen
    var w = this.getWidth();
    var h = this.getHeight();
    var p = this.getLocation();
    this.setLocation(p.x - w / 2, p.y - h / 2);
  }
}
