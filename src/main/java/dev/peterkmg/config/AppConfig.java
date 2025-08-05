package dev.peterkmg.config;

import lombok.experimental.UtilityClass;

import java.awt.Font;

@UtilityClass
public class AppConfig {
  public final String APP_NAME = "ConcurrentSim";

  public final int APP_WIDTH = 640;
  public final int APP_HEIGHT = 480;

  public final int FONT_SIZE_SMALL = 14;
  public final int FONT_SIZE_MEDIUM = 22;
  public final int FONT_SIZE_LARGE = 26;

  public final String FONT_NAME = "Arial";

  public final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_SMALL);
  public final Font FONT_MEDIUM = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_MEDIUM);
  public final Font FONT_LARGE = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_LARGE);

  public final int CONTENT_MARGIN = 20;

  public final int UPDATE_INTERVAL_DEFAULT = 200; // ms

  public final int FIELD_SZ_DEFAULT = 14;
  public final int FIELD_SZ_MIN = 11;
  public final int FIELD_SZ_MAX = 23;
  public final int FIELD_SZ_STEP = 3;

  public final int DOG_DEFAULT = 5;
  public final int DOG_MIN = 0;
  public final int DOG_MAX = 30;

  public final int SHEEP_DEFAULT = 10;
  public final int SHEEP_MIN = 1;
  public final int SHEEP_MAX = 16;
  public final int SHEEP_TOTAL_MAX = 26; // letters in the alphabet
}
