package ui.card;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for a new user
public class NewUserUI extends JPanel {
    private JTextField usernameField;
    private JButton submit;
    private JButton back;

    // EFFECTS: constructs a new user panel
    public NewUserUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel(LOGO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel welcome = new JLabel("Welcome to HabitHaven!");
        welcome.setFont(BIG_FONT);
        welcome.setForeground(FONT_COLOUR);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel prompt = new JLabel("Please enter your username:");
        prompt.setFont(MEDIUM_FONT);
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        prompt.setForeground(FONT_COLOUR);
        usernameField = new JTextField();
        usernameField.setFont(MEDIUM_FONT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMinimumSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
        usernameField.setMaximumSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
        submit = new JButton("Submit");
        makeButton(submit, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, BIG_FONT);
        back = new JButton("Back");
        makeButton(back, LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT, BIG_FONT);
        addComponentsToNewUser(logo, welcome, prompt, usernameField, submit, back);
    }

    // MODIFIES: this
    // EFFECTS: adds submit listener to submit button
    public void setSubmitListener(Runnable toHabits) {
        // MODIFIES: this
        // EFFECTS: if username length > MAX_NAME_LENGTH, show error message and set text to "",
        //          if username length is blank, show error message and set text to "",
        //          otherwise, disable the back and submit buttons, and switch to habits screen
        submit.addActionListener(e -> invokeLater(() -> {
            if (usernameField.getText().length() > MAX_NAME_LENGTH) {
                JOptionPane.showMessageDialog(this, "Maximum username length is"
                        + MAX_NAME_LENGTH + " characters.", "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
            } else if (usernameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
            } else {
                back.setEnabled(false);
                submit.setEnabled(false);
                toHabits.run();
            }
        }));
    }

    // MODIFIES: this
    // EFFECTS: adds back listener to back button
    public void setBackListener(Runnable toStart) {
        // MODIFIES: this
        // EFFECTS: disables back and submit button and go back to start screen
        back.addActionListener(e -> invokeLater(() -> {
            back.setEnabled(false);
            submit.setEnabled(false);
            toStart.run();
        }));
    }

    // EFFECTS: returns text in usernameField
    public String getText() {
        return usernameField.getText();
    }

    // MODIFIES: this
    // EFFECTS: adds components to new user screen
    private void addComponentsToNewUser(JLabel logo, JLabel welcome, JLabel prompt, JTextField usernameField,
                                        JButton submit, JButton back) {
        setBackground(APP_COLOUR);
        add(logo);
        add(welcome);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(prompt);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(usernameField);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(submit);
        add(Box.createRigidArea(new Dimension(0, PADDING)));
        add(back);
    }

    // MODIFIES: this, g
    // EFFECTS: setups gradient of title screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setupStartingGradient(g, getWidth(), getHeight());
    }
}