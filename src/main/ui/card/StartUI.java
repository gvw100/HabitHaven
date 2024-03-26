package ui.card;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel of the start screen
public class StartUI extends JPanel {
    private JLabel logo;
    private JButton newUserButton;
    private JButton loadUserButton;

    // EFFECTS: constructs title screen panel
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

    // MODIFIES: this
    // EFFECTS: enables both buttons
    public void enableButtons() {
        newUserButton.setEnabled(true);
        loadUserButton.setEnabled(true);
    }

    // MODIFIES: this
    // EFFECTS: adds listener to new user button
    public void setNewUserListener(Runnable toNewUser) {
        // MODIFIES: this
        // EFFECTS: disables both buttons and switches to new user screen
        newUserButton.addActionListener(e -> invokeLater(() -> {
            newUserButton.setEnabled(false);
            loadUserButton.setEnabled(false);
            toNewUser.run();
        }));
    }

    // MODIFIES: this
    // EFFECTS: adds listener to load user button
    public void setLoadUserListener(Runnable toHabits) {
        // MODIFIES: this
        // EFFECTS: disables both buttons and attempts to switch to habits screen
        loadUserButton.addActionListener(e -> invokeLater(() -> {
            newUserButton.setEnabled(false);
            loadUserButton.setEnabled(false);
            toHabits.run();
        }));
    }

    // MODIFIES: this
    // EFFECTS: adds components to startScreen
    private void createStartScreen(JButton newUserButton, JButton loadUserButton) {
        add(logo);
        add(Box.createRigidArea(new Dimension(0, PADDING * 2)));
        add(newUserButton);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(loadUserButton);
    }

    // MODIFIES: this, g
    // EFFECTS: sets up gradient of starting screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setupStartingGradient(g, getWidth(), getHeight());
    }
}
