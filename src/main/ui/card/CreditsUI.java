package ui.card;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;

// Represents the JPanel for displaying credits
public class CreditsUI extends JPanel {
    private static final List<Triplet<String, String, ImageIcon>> CREDITS = List.of(
            new Triplet<>("Achievement toast sound effect and icon by RobTop Games",
                    "https://www.robtopgames.com/",
                    ACHIEVEMENT_ON),
            new Triplet<>("Exit icon by Icons8",
                    "https://icons8.com/icon/8119/logout",
                    EXIT_ICON),
            new Triplet<>("Hide icon by Icons8",
            "https://icons8.com/icon/Onxve3d9Aj1r/hide",
                    HIDE_ICON),
            new Triplet<>("HabitHaven logo from Canva",
                    "https://www.canva.com/templates/EAFvMagh96A-colorful-modern-infinity-technology-free-logo/",
                    TRANSPARENT_ICON),
            new Triplet<>("White delete icon by Icons8",
                    "https://icons8.com/icon/DU8dSXkvLUkx/trash",
                    DELETE_ICON),
            new Triplet<>("Red delete icon from Flaticon created by IYAHICON",
                    "https://www.flaticon.com/free-icons/delete",
                    DELETE_ICON_HOVER),
            new Triplet<>("Add icon by Icons8",
                    "https://icons8.com/icon/1501/plus",
                    ADD_ICON),
            new Triplet<>("White bell icon by Icons8",
                    "https://icons8.com/icon/9xjQNFjDCyFj/alarm",
                    BELL_ON),
            new Triplet<>("Red bell icon from Flaticon created by IYAHICON",
                    "https://www.flaticon.com/free-icons/alarm-off",
                    BELL_OFF),
            new Triplet<>("List icon by Icons8",
                    "https://icons8.com/icon/5T76ABfXaynZ/list",
                    LIST_ICON),
            new Triplet<>("Stats icon by Icons8",
                    "https://icons8.com/icon/wdfmkgweCGDk/analytics",
                    STATS_ICON),
            new Triplet<>("Save icon by Icons8",
                    "https://icons8.com/icon/btak6xPsAuHB/save",
                    SAVE_ICON),
            new Triplet<>("Settings icon by Icons8",
                    "https://icons8.com/icon/Zydyx4gBcOrY/settings",
                    SETTINGS_ICON),
            new Triplet<>("Credits icon by Icons8",
                    "https://icons8.com/icon/ts5e28mdzD3N/quote-left",
                    CREDITS_ICON),
            new Triplet<>("Rocket icon by Icons8",
                    "https://icons8.com/icon/5NjoP1iD5kon/rocket",
                    HABIT_ICON),
            new Triplet<>("Trophy icon by Icons8",
                    "https://icons8.com/icon/ISeAs3TMaXUN/trophy",
                    TROPHY_ICON),
            new Triplet<>("Achievement tier icons by upklyak on Freepik",
                    "https://www.freepik.com/free-vector/game-badges-buttons-hexagon-frame-with-wings_37205228.htm#page=3&query=bronze%20silver%20gold%20platinum&position=35&from_view=keyword&track=ais&uuid=66e3a10c-974e-4d4e-9234-d8dc58f37278",
                    PLATINUM_ICON));
    private static final Color LINK = Color.BLUE.brighter().brighter().brighter().brighter();

    // EFFECTS: constructs a CreditsUI panel
    public CreditsUI() {
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups credits panel
    private void setupPanel() {
        setBackground(APP_COLOUR);
        setLayout(new GridLayout(1, 1));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(APP_COLOUR);
        setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setViewportView(mainPanel);
        setupComponents(mainPanel);
        add(scrollPane);
    }

    // MODIFIES: mainPanel
    // EFFECTS: setups components and adds them to mainPanel
    private void setupComponents(JPanel mainPanel) {
        setupTitle(mainPanel);
        setupRows(mainPanel);
    }

    // MODIFIES: mainPanel
    // EFFECTS: setups title panel and adds to mainPanel
    private void setupTitle(JPanel mainPanel) {
        JPanel title = new JPanel();
        title.setLayout(new FlowLayout());
        title.setBackground(APP_COLOUR);
        JLabel titleLabel = new JLabel("Credits");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(HUGE_FONT);
        titleLabel.setForeground(FONT_COLOUR);
        title.add(titleLabel);
        mainPanel.add(title, getTitleConstraints());
    }

    // EFFECTS: returns grid bag constraints for title panel
    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING * 2, 0);
        return constraints;
    }

    // MODIFIES: mainPanel
    // EFFECTS: setups rows of credits
    private void setupRows(JPanel mainPanel) {
        for (Triplet<String, String, ImageIcon> triplet : CREDITS) {
            setupRow(triplet, mainPanel);
        }
    }

    // Inspiration from https://www.codejava.net/java-se/swing/how-to-create-hyperlink-with-jlabel-in-java-swing
    // MODIFIES: mainPanel
    // EFFECTS: adds credit row to mainPanel based on given triplet
    private void setupRow(Triplet<String, String, ImageIcon> triplet, JPanel mainPanel) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout());
        row.setBackground(APP_COLOUR);
        JLabel rowLabel = new JLabel(triplet.getFirst(), triplet.getThird(), SwingConstants.CENTER);
        rowLabel.setFont(MEDIUM_FONT);
        rowLabel.setForeground(LINK);
        rowLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setupRowListener(triplet, rowLabel);
        row.add(rowLabel);
        mainPanel.add(row, getRowConstraints());
    }

    // MODIFIES: rowLabel
    // EFFECTS: adds listener to rowLabel
    private void setupRowListener(Triplet<String, String, ImageIcon> triplet, JLabel rowLabel) {
        rowLabel.addMouseListener(new MouseAdapter() {
            // EFFECTS: takes user to link given by triplet.getValue()
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(triplet.getSecond()));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            // MODIFIES: rowLabel
            // EFFECTS: underlines rowLabel
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> underlineLink(triplet, rowLabel));
            }

            // MODIFIES: rowLabel
            // EFFECTS: undo underlining rowLabel
            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> undoUnderlineLink(triplet, rowLabel));
            }
        });
    }

    // MODIFIES: rowLabel
    // EFFECTS: underlines rowLabel
    private void underlineLink(Triplet<String, String, ImageIcon> triplet, JLabel rowLabel) {
        rowLabel.setText("<html><a href=''>" + triplet.getFirst() + "</a></html>");
    }

    // MODIFIES: rowLabel
    // EFFECTS: undo underlining rowLabel
    private void undoUnderlineLink(Triplet<String, String, ImageIcon> triplet, JLabel rowLabel) {
        rowLabel.setText(triplet.getFirst());
    }

    // EFFECTS: returns grid bag constraint for a credit row
    private GridBagConstraints getRowConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }

    // Inspiration from https://stackoverflow.com/questions/6010843/java-how-to-store-data-triple-in-a-list
    // Helper class to store a triplet of values
    private static class Triplet<F, S, T> {
        private final F first;
        private final S second;
        private final T third;

        public Triplet(F first, S second, T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }

        public T getThird() {
            return third;
        }
    }
}
