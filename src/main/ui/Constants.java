package ui;

import javax.swing.*;
import java.awt.*;

public class Constants {
    public static final int WINDOW_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int WINDOW_HEIGHT = Math.round(Toolkit.getDefaultToolkit().getScreenSize().height * 0.99f);
    public static final int SIDE_BAR_WIDTH = 200;
    // ACHIEVEMENT_SOUND by Robtop Games
    public static final String ACHIEVEMENT_SOUND = "./data/achievement.wav";
    // LOGO from https://www.canva.com/templates/EAFvMagh96A-colorful-modern-infinity-technology-free-logo/
    public static final ImageIcon LOGO = new ImageIcon("./data/logo.png");
    public static final ImageIcon LOGO_ICON = new ImageIcon("./data/logo_icon.png");
    public static final ImageIcon TRANSPARENT_ICON = new ImageIcon("./data/transparent_icon.png");
    // DELETE_ICON from https://icons8.com/icon/DU8dSXkvLUkx/trash by Icons8
    public static final ImageIcon DELETE_ICON = new ImageIcon("./data/delete.png");
    // DELETE_ICON_HOVER from https://www.flaticon.com/free-icons/delete Delete icons created by IYAHICON - Flaticon
    public static final ImageIcon DELETE_ICON_HOVER = new ImageIcon("./data/delete_hover.png");
    // ADD_ICON from https://icons8.com/icon/1501/plus by Icons8
    public static final ImageIcon ADD_ICON = new ImageIcon("./data/add.png");
    public static final ImageIcon ADD_ICON_HOVER = new ImageIcon("./data/add.png");
    // BELL_ON from https://icons8.com/icon/9xjQNFjDCyFj/alarm by Icons8
    public static final ImageIcon BELL_ON = new ImageIcon("./data/bell_on.png");
    public static final ImageIcon BELL_ON_HOVER = new ImageIcon("./data/bell_on.png");
    // BELL_OFF from https://www.flaticon.com/free-icons/alarm-off Alarm off icons created by IYAHICON - Flaticon
    public static final ImageIcon BELL_OFF = new ImageIcon("./data/bell_off.png");
    public static final ImageIcon BELL_OFF_HOVER = new ImageIcon("./data/bell_off.png");
    // LIST_ICON from https://icons8.com/icon/5T76ABfXaynZ/list by Icons8
    public static final ImageIcon LIST_ICON = new ImageIcon("./data/list.png");
    // STATS_ICON from https://icons8.com/icon/wdfmkgweCGDk/analytics by Icons8
    public static final ImageIcon STATS_ICON = new ImageIcon("./data/stats.png");
    // SAVE_ICON from https://icons8.com/icon/btak6xPsAuHB/save by Icons8
    public static final ImageIcon SAVE_ICON = new ImageIcon("./data/save.png");
    public static final ImageIcon SAVE_OFF_ICON = new ImageIcon("./data/save_off.png");
    // SETTINGS_ICON from https://icons8.com/icon/Zydyx4gBcOrY/settings by Icons8
    public static final ImageIcon SETTINGS_ICON = new ImageIcon("./data/settings.png");
    // CREDITS_ICON from https://icons8.com/icon/ts5e28mdzD3N/quote-left by Icons8
    public static final ImageIcon CREDITS_ICON =  new ImageIcon("./data/credits.png");
    // HABIT_ICON from https://icons8.com/icon/5NjoP1iD5kon/rocket by Icons8
    public static final ImageIcon HABIT_ICON = new ImageIcon("./data/rocket.png");
    // TROPHY_ICON from https://icons8.com/icon/ISeAs3TMaXUN/trophy by Icons8
    public static final ImageIcon TROPHY_ICON = new ImageIcon("./data/trophy.png");
    // Achievement icons from
    // https://www.freepik.com/free-vector/game-badges-buttons-hexagon-frame-with-wings_37205228.htm#page=3&query=bronze%20silver%20gold%20platinum&position=35&from_view=keyword&track=ais&uuid=66e3a10c-974e-4d4e-9234-d8dc58f37278 by upklyak on Freepik
    public static final ImageIcon BRONZE_ICON = new ImageIcon("./data/bronze.png");
    public static final ImageIcon SILVER_ICON = new ImageIcon("./data/silver.png");
    public static final ImageIcon GOLD_ICON = new ImageIcon("./data/gold.png");
    public static final ImageIcon PLATINUM_ICON = new ImageIcon("./data/platinum.png");
    public static final ImageIcon BRONZE_TOAST = new ImageIcon("./data/bronze.png");
    public static final ImageIcon SILVER_TOAST = new ImageIcon("./data/silver.png");
    public static final ImageIcon GOLD_TOAST = new ImageIcon("./data/gold.png");
    public static final ImageIcon PLATINUM_TOAST = new ImageIcon("./data/platinum.png");
    public static final int LOGO_WIDTH = 300;
    public static final int LOGO_HEIGHT = 300;
    public static final int LARGE_BUTTON_WIDTH = 275;
    public static final int LARGE_BUTTON_HEIGHT = 75;
    public static final int PADDING = 35;
    public static final Color APP_COLOUR = Color.decode("#100d28");
    public static final Color APP_COLOUR_LIGHT = APP_COLOUR.brighter().brighter().brighter();
    public static final Color SUCCESS_GREEN = Color.GREEN.darker().darker().darker();
    public static final Color SUCCESS_GREEN_LIGHT = SUCCESS_GREEN.brighter();
    public static final Color SIDEBAR_COLOUR = Color.decode("#1c76c7").darker().darker().darker();
    public static final Color FONT_COLOUR = Color.WHITE;
    public static final String APP_FONT = "Arial";
    public static final Font HUGE_FONT = new Font(APP_FONT, Font.PLAIN, 55);
    public static final Font BIG_FONT = new Font(APP_FONT, Font.PLAIN, 40);
    public static final Font MEDIUM_FONT = new Font(APP_FONT, Font.PLAIN, 20);
    public static final Font SMALL_FONT = new Font(APP_FONT, Font.PLAIN, 18);
    public static final int TEXT_FIELD_WIDTH = 275;
    public static final int TEXT_FIELD_HEIGHT = 50;
    public static final int MAX_NAME_LENGTH = 20;
    public static final int MAX_HABIT_NAME_LENGTH = 30;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_FREQUENCY = 15;
    public static final int MONTH_MAX_DAYS = 31;
    public static final String HABIT_MANAGER_STORE = "./data/habitManager.json";

    public static void makeButton(JButton button, int width, int height, Font font) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        button.setMinimumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusable(false);
        button.setFont(font);
        button.setForeground(FONT_COLOUR);
        button.setContentAreaFilled(false);
    }

    public static void setupStartingGradient(Graphics g, int width, int height) {
        Graphics2D graphics2d = (Graphics2D) g;
        g.fillRect(0, 0, width, height);
        GradientPaint gradientPaint = new GradientPaint(
                (float) 0.65 * width, (float) 0.60 * height, APP_COLOUR, width, height, APP_COLOUR_LIGHT);
        graphics2d.setPaint(gradientPaint);
        graphics2d.fillRect(0, 0, width, height);
    }
}
