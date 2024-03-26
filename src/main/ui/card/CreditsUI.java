package ui.card;

import javafx.util.Pair;

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
    private static final List<Pair<String, String>> CREDITS = List.of(
            new Pair<>("Achievement toast sound effect and icon by RobTop Games",
                    "https://www.robtopgames.com/"),
            new Pair<>("Exit icon by Icons8",
                    "https://icons8.com/icon/8119/logout"),
            new Pair<>("Hide icon by Icons8",
            "https://icons8.com/icon/Onxve3d9Aj1r/hide"),
            new Pair<>("HabitHaven logo from Canva",
                    "https://www.canva.com/templates/EAFvMagh96A-colorful-modern-infinity-technology-free-logo/"),
            new Pair<>("White delete icon by Icons8",
                    "https://icons8.com/icon/DU8dSXkvLUkx/trash"),
            new Pair<>("Red delete icon from Flaticon created by IYAHICON",
                    "https://www.flaticon.com/free-icons/delete"),
            new Pair<>("Add icon by Icons8",
                    "https://icons8.com/icon/1501/plus"),
            new Pair<>("White bell icon by Icons8",
                    "https://icons8.com/icon/9xjQNFjDCyFj/alarm"),
            new Pair<>("Red bell icon from Flaticon created by IYAHICON",
                    "https://www.flaticon.com/free-icons/alarm-off"),
            new Pair<>("List icon by Icons8",
                    "https://icons8.com/icon/5T76ABfXaynZ/list"),
            new Pair<>("Stats icon by Icons8",
                    "https://icons8.com/icon/wdfmkgweCGDk/analytics"),
            new Pair<>("Save icon by Icons8",
                    "https://icons8.com/icon/btak6xPsAuHB/save"),
            new Pair<>("Settings icon by Icons8",
                    "https://icons8.com/icon/Zydyx4gBcOrY/settings"),
            new Pair<>("Credits icon by Icons8",
                    "https://icons8.com/icon/ts5e28mdzD3N/quote-left"),
            new Pair<>("Rocket icon by Icons8",
                    "https://icons8.com/icon/5NjoP1iD5kon/rocket"),
            new Pair<>("Trophy icon by Icons8",
                    "https://icons8.com/icon/ISeAs3TMaXUN/trophy"),
            new Pair<>("Achievement tier icons by upklyak on Freepik",
                    "https://www.freepik.com/free-vector/game-badges-buttons-hexagon-frame-with-wings_37205228.htm#page=3&query=bronze%20silver%20gold%20platinum&position=35&from_view=keyword&track=ais&uuid=66e3a10c-974e-4d4e-9234-d8dc58f37278"));
    private static final Color LINK = Color.BLUE.brighter().brighter().brighter().brighter();

    public CreditsUI() {
        setupPanel();
    }

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

    private void setupComponents(JPanel mainPanel) {
        setupTitle(mainPanel);
        setupRows(mainPanel);
    }

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

    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING * 2, 0);
        return constraints;
    }

    private void setupRows(JPanel mainPanel) {
        for (Pair<String, String> pair : CREDITS) {
            setupRow(pair, mainPanel);
        }
    }

    // Inspiration from https://www.codejava.net/java-se/swing/how-to-create-hyperlink-with-jlabel-in-java-swing
    private void setupRow(Pair<String, String> pair, JPanel mainPanel) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout());
        row.setBackground(APP_COLOUR);
        JLabel rowLabel = new JLabel(pair.getKey());
        rowLabel.setFont(MEDIUM_FONT);
        rowLabel.setForeground(LINK);
        rowLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setupRowListener(pair, rowLabel);
        row.add(rowLabel);
        mainPanel.add(row, getRowConstraints());
    }

    private void setupRowListener(Pair<String, String> pair, JLabel rowLabel) {
        rowLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(pair.getValue()));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> underlineLink(pair, rowLabel));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> undoUnderlineLink(pair, rowLabel));
            }
        });
    }

    private void underlineLink(Pair<String, String> pair, JLabel rowLabel) {
        rowLabel.setText("<html><a href=''>" + pair.getKey() + "</a></html>");
    }

    private void undoUnderlineLink(Pair<String, String> pair, JLabel rowLabel) {
        rowLabel.setText(pair.getKey());
    }

    private GridBagConstraints getRowConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, PADDING, 0);
        return constraints;
    }
}
