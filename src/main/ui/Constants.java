package ui;

import javax.swing.*;
import java.awt.*;

public class Constants {
    public static final int WINDOW_WIDTH = 1300;
    public static final int WINDOW_HEIGHT = 800;
    public static final int SIDE_BAR_WIDTH = 200;
    // LOGO from https://www.canva.com/templates/EAFvMagh96A-colorful-modern-infinity-technology-free-logo/
    public static final ImageIcon LOGO = new ImageIcon("./data/LOGO.png");
    public static final ImageIcon LOGO_ICON = new ImageIcon("./data/icon.png");
    public static final ImageIcon TRANSPARENT_ICON = new ImageIcon("./data/transparent_icon.png");
    // DELETE_ICON from https://www.flaticon.com/free-icons/delete Delete icons created by Ilham Fitrotul Hayat - Flaticon
    public static final ImageIcon DELETE_ICON = new ImageIcon("./data/delete.png");
    // DELETE_ICON_HOVER from https://www.flaticon.com/free-icons/delete Delete icons created by IYAHICON - Flaticon
    public static final ImageIcon DELETE_ICON_HOVER = new ImageIcon("./data/delete_hover.png");
    // ADD_ICON from https://www.flaticon.com/free-icons/create Create icons created by nahumam - Flaticon
    public static final ImageIcon ADD_ICON = new ImageIcon("./data/add.png");
    public static final ImageIcon ADD_ICON_HOVER = new ImageIcon("./data/add.png");
    // BELL_ON from https://www.flaticon.com/free-icons/notification Notification icons created by Freepik - Flaticon
    public static final ImageIcon BELL_ON = new ImageIcon("./data/bell_on.png");
    // BELL_OFF from https://www.flaticon.com/free-icons/alarm-off Alarm off icons created by IYAHICON - Flaticon
    public static final ImageIcon BELL_OFF = new ImageIcon("./data/bell_off.png");
    public static final int LOGO_WIDTH = 300;
    public static final int LOGO_HEIGHT = 300;
    public static final int LARGE_BUTTON_WIDTH = 275;
    public static final int LARGE_BUTTON_HEIGHT = 75;
    public static final int PADDING = 35;
    public static final Color APP_COLOUR = Color.decode("#100d28");
    public static final Color SIDEBAR_COLOUR = Color.decode("#302c34");
    public static final Color FONT_COLOUR = Color.WHITE;
    public static final String APP_FONT = "Arial";
    public static final Font HUGE_FONT = new Font(APP_FONT, Font.PLAIN, 60);
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
    public static final int MAX_REMINDERS_PER_DAY = 15;

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
}
