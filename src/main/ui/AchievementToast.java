package ui;

import javafx.util.Pair;
import model.achievement.Achievement;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ui.Constants.*;

// Inspiration taken from: https://www.codejava.net/coding/how-to-play-back-audio-in-java-with-examples and
// https://www.tutorialspoint.com/Create-Toast-Message-in-Java-Swing
public class AchievementToast extends JPanel {
    private LinkedBlockingQueue<Pair<String, Achievement>> achievementQueue;
    private Lock lock = new ReentrantLock();
    private JLabel title;
    private JLabel description;

    public AchievementToast() {
        achievementQueue = new LinkedBlockingQueue<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(WINDOW_WIDTH, 500));
        setupPlaceholderToast();
    }

    public void add(Pair<String, Achievement> achievement) {
        if (!HabitApp.appIsOpen()) {
            return;
        }
        try {
            achievementQueue.put(achievement);
            displayToast();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    private void displayToast() {
        Thread thread = new Thread(() -> {
            try {
                lock.lock();
                updateToast(achievementQueue.take());
                int red = APP_COLOUR_LIGHT.getRed();
                int green = APP_COLOUR_LIGHT.getGreen();
                int blue = APP_COLOUR_LIGHT.getBlue();
                setBackground(new Color(red, green, blue, 255));
                setVisible(true);
                playSound();
                Thread.sleep(4000);
                setVisible(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
        thread.start();
    }

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

    private void updateToast(Pair<String, Achievement> achievement) {
        Runnable runnable = () -> {
            ImageIcon icon = getTierIcon(achievement);
            title.setIcon(icon);
            title.setText(achievement.getKey() + " - " + achievement.getValue().getName());
            description.setText(achievement.getValue().getDescription());
        };
        SwingUtilities.invokeLater(runnable);
    }

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
