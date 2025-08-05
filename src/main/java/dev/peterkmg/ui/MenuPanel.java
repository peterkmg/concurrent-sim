package dev.peterkmg.ui;

import java.util.Arrays;
import java.util.function.Consumer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import dev.peterkmg.config.AppConfig;
import dev.peterkmg.utils.CompUtils;

public class MenuPanel extends JPanel {

  private final Consumer<int[]> invokeGameCallback;

  /**
   * options[0] - width options[1] - height options[2] - dogs options[3] - sheep
   */
  private final int[] options = new int[] {AppConfig.FIELD_SZ_DEFAULT, AppConfig.FIELD_SZ_DEFAULT,
      AppConfig.DOG_DEFAULT, AppConfig.SHEEP_DEFAULT, AppConfig.UPDATE_INTERVAL_DEFAULT};

  public MenuPanel(Consumer<int[]> invokeGameCallback) {
    super(new BorderLayout());
    this.invokeGameCallback = invokeGameCallback;

    this.setupSelf();

    this.setupHeader();
    this.setupContent();
    this.setupFooter();
  }

  private void setupSelf() {
    var m = AppConfig.CONTENT_MARGIN;
    var border = BorderFactory.createEmptyBorder(m, m, m, m);
    this.setBorder(border);
  }

  private void setupHeader() {
    var headerLabel = CompUtils.createBigLabel(AppConfig.APP_NAME, null, true);
    this.add(headerLabel, BorderLayout.NORTH);
  }

  private void setupContent() {
    var midPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));

    var mh = (AppConfig.APP_WIDTH - AppConfig.CONTENT_MARGIN) / 4;
    var mv = (AppConfig.APP_HEIGHT - AppConfig.CONTENT_MARGIN) / 12;
    var midPanelBorder = BorderFactory.createEmptyBorder(mv, mh, mv, mh);
    midPanel.setBorder(midPanelBorder);

    var mpLabel = CompUtils.createMediumLabel("Select simulation options:", null, true);
    midPanel.add(mpLabel);

    var wPanel = new JPanel(new BorderLayout());
    var wSpinner = new JSpinner(new SpinnerNumberModel(AppConfig.FIELD_SZ_DEFAULT,
        AppConfig.FIELD_SZ_MIN, AppConfig.FIELD_SZ_MAX, AppConfig.FIELD_SZ_STEP));
    var wLabel = CompUtils.createSmallLabel("Field width:", wSpinner, false);

    wPanel.add(wLabel, BorderLayout.WEST);
    wPanel.add(wSpinner, BorderLayout.EAST);

    var hPanel = new JPanel(new BorderLayout());
    var hSpinner = new JSpinner(new SpinnerNumberModel(AppConfig.FIELD_SZ_DEFAULT,
        AppConfig.FIELD_SZ_MIN, AppConfig.FIELD_SZ_MAX, AppConfig.FIELD_SZ_STEP));
    var hLabel = CompUtils.createSmallLabel("Field height:", hSpinner, false);

    hPanel.add(hLabel, BorderLayout.WEST);
    hPanel.add(hSpinner, BorderLayout.EAST);

    var dPanel = new JPanel(new BorderLayout());
    var dSpinner = new JSpinner(
        new SpinnerNumberModel(AppConfig.DOG_DEFAULT, AppConfig.DOG_MIN, AppConfig.DOG_MAX, 1));
    var dLabel = CompUtils.createSmallLabel("Dogs amount:", dSpinner, false);

    dPanel.add(dLabel, BorderLayout.WEST);
    dPanel.add(dSpinner, BorderLayout.EAST);

    var sPanel = new JPanel(new BorderLayout());
    var sSpinner = new JSpinner(new SpinnerNumberModel(AppConfig.SHEEP_DEFAULT, AppConfig.SHEEP_MIN,
        AppConfig.SHEEP_MAX, 1));
    var sLabel = CompUtils.createSmallLabel("Sheep amount:", sSpinner, false);

    sPanel.add(sLabel, BorderLayout.WEST);
    sPanel.add(sSpinner, BorderLayout.EAST);

    var uPanel = new JPanel(new BorderLayout());
    var uSpinner =
        new JSpinner(new SpinnerNumberModel(AppConfig.UPDATE_INTERVAL_DEFAULT, 1, 1000, 20));
    var uLabel = CompUtils.createSmallLabel("Update interval (ms):", uSpinner, false);

    uPanel.add(uLabel, BorderLayout.WEST);
    uPanel.add(uSpinner, BorderLayout.EAST);

    var btnPanel = new JPanel();
    var btnStart =
        CompUtils.createButton("To Simulation", e -> this.invokeGameCallback.accept(this.options));
    btnPanel.add(btnStart, BorderLayout.CENTER);
    var btnExit = CompUtils.createButton("Exit", e -> System.exit(0));
    btnPanel.add(btnExit, BorderLayout.SOUTH);

    uSpinner.addChangeListener(e -> this.options[4] = (int) uSpinner.getValue());
    dSpinner.addChangeListener(e -> this.options[2] = (int) dSpinner.getValue());
    sSpinner.addChangeListener(e -> this.options[3] = (int) sSpinner.getValue());
    wSpinner.addChangeListener(e -> {
      var val = (int) wSpinner.getValue();
      options[0] = val;

      var max = Math.min(AppConfig.SHEEP_TOTAL_MAX, ((int) val / 3) * ((int) options[1] / 3));
      var model = (SpinnerNumberModel) sSpinner.getModel();
      if (model.getNumber().intValue() > max)
        model.setValue(max);
      model.setMaximum(max);
    });

    hSpinner.addChangeListener(e -> {
      var val = (int) hSpinner.getValue();
      options[1] = val;

      var max = Math.min(AppConfig.SHEEP_TOTAL_MAX, ((int) options[0] / 3) * ((int) val / 3));
      var model = (SpinnerNumberModel) sSpinner.getModel();
      if (model.getNumber().intValue() > max)
        model.setValue(max);
      model.setMaximum(max);
    });

    Arrays.stream(new JPanel[] {wPanel, hPanel, dPanel, sPanel, uPanel, btnPanel}).forEach(p -> {
      p.setPreferredSize(new Dimension(200, 28));
      midPanel.add(p);
    });

    this.add(midPanel, BorderLayout.CENTER);
  }

  private void setupFooter() {
    var footerLabel = CompUtils.createSmallLabel("Made by PK", null, true);
    this.add(footerLabel, BorderLayout.SOUTH);
  }

}
