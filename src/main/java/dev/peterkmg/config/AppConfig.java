package dev.peterkmg.config;

import java.awt.Font;

public class AppConfig {
  public static final String APP_NAME = "Farm Simulator";

  public static final int APP_WIDTH = 640;
  public static final int APP_HEIGHT = 480;

  public static final int FONT_SIZE_SMALL = 14;
  public static final int FONT_SIZE_MEDIUM = 22;
  public static final int FONT_SIZE_LARGE = 26;

  public static final String FONT_NAME = "Arial";

  public static final Font FONT_SMALL = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_SMALL);
  public static final Font FONT_MEDIUM = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_MEDIUM);
  public static final Font FONT_LARGE = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE_LARGE);

  public static final int CONTENT_MARGIN = 20;

  public static final int UPDATE_INTERVAL_DEFAULT = 200; // ms

  public static final int FIELD_SZ_DEFAULT = 14;
  public static final int FIELD_SZ_MIN = 11;
  public static final int FIELD_SZ_MAX = 23;
  public static final int FIELD_SZ_STEP = 3;

  public static final int DOG_DEFAULT = 5;
  public static final int DOG_MIN = 0;
  public static final int DOG_MAX = 30;

  public static final int SHEEP_DEFAULT = 10;
  public static final int SHEEP_MIN = 1;
  public static final int SHEEP_MAX = 16;
  public static final int SHEEP_TOTAL_MAX = 26; // letters in the alphabet
}
