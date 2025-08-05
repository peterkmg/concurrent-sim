package dev.peterkmg.utils;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;

import dev.peterkmg.config.AppConfig;

public class CompUtils {

  public static JButton createButton(String text, ActionListener listener) {
    var btn = new JButton(text);
    btn.addActionListener(listener);
    return btn;
  }

  public static JLabel createLabel(String text, boolean centered) {
    return centered ? new JLabel(text, JLabel.CENTER) : new JLabel(text);
  }

  public static JLabel createLabel(String text, Component labelFor, boolean centered) {
    var label = createLabel(text, centered);
    if (labelFor != null) { label.setLabelFor(labelFor); }
    return label;
  }

  public static JLabel createSmallLabel(String text, Component labelFor, boolean centered) {
    var label = createLabel(text, labelFor, centered);
    label.setFont(AppConfig.FONT_SMALL);
    return label;
  }

  public static JLabel createMediumLabel(String text, Component labelFor, boolean centered) {
    var label = createLabel(text, labelFor, centered);
    label.setFont(AppConfig.FONT_MEDIUM);
    return label;
  }

  public static JLabel createBigLabel(String text, Component labelFor, boolean centered) {
    var label = createLabel(text, labelFor, centered);
    label.setFont(AppConfig.FONT_LARGE);
    return label;
  }

}
