package ui.card;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

public class StartUI extends JPanel {
    private JLabel logo;
    private JButton newUserButton;
    private JButton loadUserButton;

    public StartUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(APP_COLOUR);
        logo = new JLabel(LOGO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        newUserButton = new JButton("New User");
        loadUserButton = new JButton("Load from File");
        makeButton(newUserButton, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        makeButton(loadUserButton, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, MEDIUM_FONT);
        createStartScreen(newUserButton, loadUserButton);
    }

    public void enableButtons() {
        newUserButton.setEnabled(true);
        loadUserButton.setEnabled(true);
    }

    public void setNewUserListener(Runnable toNewUser) {
        newUserButton.addActionListener(e -> invokeLater(() -> {
            newUserButton.setEnabled(false);
            loadUserButton.setEnabled(false);
            toNewUser.run();
        }));
    }

    public void setLoadUserListener(Runnable toHabits) {
        loadUserButton.addActionListener(e -> invokeLater(() -> {
            newUserButton.setEnabled(false);
            loadUserButton.setEnabled(false);
            toHabits.run();
        }));
    }

    private void createStartScreen(JButton newUserButton, JButton loadUserButton) {
        add(logo);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(newUserButton);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(loadUserButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setupStartingGradient(g, getWidth(), getHeight());
    }
}
