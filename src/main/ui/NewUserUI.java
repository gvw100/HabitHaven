package ui;

import javax.swing.*;
import java.awt.*;

import static ui.Constants.*;

// Represents the user interface for a new user
public class NewUserUI extends JPanel {
    private JTextField usernameField;
    private JButton submit;
    private JButton back;

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

    public void setSubmitListener(Runnable toHabits) {
        submit.addActionListener(e -> {
            if (usernameField.getText().length() > MAX_NAME_LENGTH) {
                JOptionPane.showMessageDialog(this, "Maximum username length is"
                        + MAX_NAME_LENGTH + " characters.", "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
            } else if (!usernameField.getText().isBlank()) {
                back.setEnabled(false);
                submit.setEnabled(false);
                toHabits.run();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setBackListener(Runnable toStart) {
        back.addActionListener(e -> {
            back.setEnabled(false);
            submit.setEnabled(false);
            toStart.run();
        });
    }

    public String getText() {
        return usernameField.getText();
    }

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
}
