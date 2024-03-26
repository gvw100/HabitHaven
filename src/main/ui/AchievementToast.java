package ui;

import javafx.util.Pair;
import model.achievement.Achievement;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ui.Constants.*;

// Inspiration taken from: https://www.codejava.net/coding/how-to-play-back-audio-in-java-with-examples
// Represents the JPanel of an achievement toast
public class AchievementToast extends JPanel {
    private Lock lock = new ReentrantLock();
    private JLabel title;
    private JLabel description;
    private boolean achievementToastsEnabled;

    // EFFECTS: initializes achievement toast panel with placeholder text
    public AchievementToast(boolean achievementToastsEnabled) {
        this.achievementToastsEnabled = achievementToastsEnabled;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(WINDOW_WIDTH, 500));
        setupPlaceholderToast();
    }

    public void setAchievementToastsEnabled(boolean achievementToastsEnabled) {
        this.achievementToastsEnabled = achievementToastsEnabled;
    }

    // MODIFIES: this
    // EFFECTS: queues an achievement to be displayed
    public void add(Pair<String, Achievement> achievement) {
        if (!HabitApp.appIsOpen() || !achievementToastsEnabled) {
            return;
        }
        displayToast(achievement);
    }

    // MODIFIES: this
    // EFFECTS: setups up placeholder text in the toast
    private void setupPlaceholderToast() {
        title = new JLabel("Placeholder", BRONZE_TOAST, SwingConstants.LEFT);
        title.setFont(MEDIUM_FONT);
        title.setForeground(FONT_COLOUR);
        title.setBackground(APP_COLOUR_LIGHT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalGlue());
        description = new JLabel("Description");
        description.setFont(SMALL_FONT);
        description.setForeground(FONT_COLOUR);
        description.setBackground(APP_COLOUR_LIGHT);
        description.setAlignmentX(CENTER_ALIGNMENT);
        add(description);
        JPanel empty = new JPanel();
        empty.setBackground(APP_COLOUR_LIGHT);
        empty.setPreferredSize(new Dimension(WINDOW_WIDTH, PADDING));
        add(empty);
        setVisible(false);
    }

    // EFFECTS: returns icon depending on the achievement tier
    private ImageIcon getTierIcon(Pair<String, Achievement> achievement) {
        switch (achievement.getValue().getTier()) {
            case BRONZE:
                return BRONZE_TOAST;
            case SILVER:
                return SILVER_TOAST;
            case GOLD:
                return GOLD_TOAST;
            default:
                return PLATINUM_TOAST;
        }
    }

    // MODIFIES: this
    // EFFECTS: first attempts to acquire the lock, once successful, displays achievement toast,
    //          then waits for 4 seconds, lock ensures achievements are displayed sequentially at even intervals
    private void displayToast(Pair<String, Achievement> achievement) {
        Thread thread = new Thread(() -> {
            try {
                lock.lock();
                if (HabitApp.appIsOpen() && achievementToastsEnabled) {
                    updateToast(achievement);
                    int red = APP_COLOUR_LIGHT.getRed();
                    int green = APP_COLOUR_LIGHT.getGreen();
                    int blue = APP_COLOUR_LIGHT.getBlue();
                    setBackground(new Color(red, green, blue));
                    setVisible(true);
                    playSound();
                    Thread.sleep(4000);
                    setVisible(false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        thread.start();
    }

    // EFFECTS: plays the achievement sound effect
    private void playSound() {
        try {
            File soundFile = new File(ACHIEVEMENT_SOUND);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioInputStream.getFormat();
            Line.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(AudioSystem.getAudioInputStream(new File(ACHIEVEMENT_SOUND)));
            audioClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the text in achievement toast based on given achievement
    private void updateToast(Pair<String, Achievement> achievement) {
        Runnable runnable = () -> {
            ImageIcon icon = getTierIcon(achievement);
            title.setIcon(icon);
            title.setText(achievement.getKey() + " - " + achievement.getValue().getName());
            description.setText(achievement.getValue().getDescription());
        };
        SwingUtilities.invokeLater(runnable);
    }

    // MODIFIES: this, g
    // EFFECTS: overridden to give achievement toast a gradient from left edge to right edge
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2d = (Graphics2D) g;
        GradientPaint gradientPaint = new GradientPaint(0, (float) 0,
                APP_COLOUR.brighter(), getWidth(), getHeight(), APP_COLOUR_LIGHT.brighter());
        graphics2d.setPaint(gradientPaint);
        graphics2d.fillRect(0, 0, getWidth(), getHeight());
    }
}