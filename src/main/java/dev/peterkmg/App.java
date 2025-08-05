package dev.peterkmg;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import dev.peterkmg.ui.MainWindow;

public class App {
  public static void main(String[] args) {
    // set style
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      System.err.println("Failed to set UI style.");
    }

    SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
  }
}
