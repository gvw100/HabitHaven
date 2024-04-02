package ui.card;

import javafx.util.Pair;
import model.Habit;
import model.HabitManager;
import model.achievement.Achievement;
import model.achievement.AchievementManager;
import ui.AchievementToast;
import ui.card.habit.HabitUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static ui.Constants.*;
import static ui.Constants.APP_COLOUR;
import static ui.card.HabitManagerUI.changeMade;

// Represents the habit list panel
public class HabitListUI extends JPanel {
    private HabitManager habitManager;
    private JPanel habitsPanel;
    private JPanel archivedPanel;
    private JScrollPane parentRegular;
    private JScrollPane parentArchived;
    private CardLayout parentCardLayout;
    private JPanel parentPanel;
    private AchievementToast achievementToast;
    private Runnable toCreateHabit;

    // EFFECTS: constructs a HabitListUI panel
    public HabitListUI(HabitManager habitManager, CardLayout parentCardLayout,
                       JPanel parentPanel, AchievementToast achievementToast, Runnable toCreateHabit) {
        this.habitManager = habitManager;
        this.parentCardLayout = parentCardLayout;
        this.parentPanel = parentPanel;
        this.achievementToast = achievementToast;
        this.toCreateHabit = toCreateHabit;
        setupPanel();
    }

    // MODIFIES: this
    // EFFECTS: setups the HabitListUI panel
    private void setupPanel() {
        UIManager.put("TabbedPane.selected", APP_COLOUR.brighter().brighter().brighter());
        setLayout(new GridLayout(1, 1));
        setBackground(APP_COLOUR);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        tabbedPane.setBackground(APP_COLOUR);
        tabbedPane.setForeground(FONT_COLOUR);
        tabbedPane.setFont(MEDIUM_FONT);
        tabbedPane.setMinimumSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, WINDOW_HEIGHT));
        setupRegularList();
        setupArchivedList();
        tabbedPane.addTab("Habits", HABIT_ICON, parentRegular, "Your Habits");
        tabbedPane.addTab("Archived Habits", null, parentArchived, "Your Archived Habits");
        add(tabbedPane);
    }

    // MODIFIES: this
    // EFFECTS: setups the habits tab of tabbed pane - non-archived habits
    private void setupRegularList() {
        parentRegular = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        habitsPanel = new JPanel();
        setupGeneralList(parentRegular, habitsPanel, false);
    }

    // MODIFIES: this
    // EFFECTS: setups the archived tab of the tabbed pane - archived habits
    private void setupArchivedList() {
        parentArchived = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        archivedPanel = new JPanel();
        setupGeneralList(parentArchived, archivedPanel, true);
    }

    // MODIFIES: parent, list
    // EFFECTS: setups either the archived or regular habits tab
    private void setupGeneralList(JScrollPane parent, JPanel list, boolean isArchived) {
        parent.getVerticalScrollBar().setUnitIncrement(10);
        list.setLayout(new GridBagLayout());
        list.setBackground(APP_COLOUR);
        List<Habit> selectedHabits = isArchived ? getArchivedHabits() : getRegularHabits();
        setupRows(selectedHabits, isArchived);
        parent.setViewportView(list);
    }

    // MODIFIES: this
    // EFFECTS: updates habit list panel
    public void updateHabitList() {
        habitsPanel.removeAll();
        archivedPanel.removeAll();
        setupRows(getRegularHabits(), false);
        setupRows(getArchivedHabits(), true);
        updateUI();
    }

    // EFFECTS: returns all the archived habits
    private List<Habit> getArchivedHabits() {
        return getHabits(true);
    }

    // EFFECTS: returns all the regular habits
    private List<Habit> getRegularHabits() {
        return getHabits(false);
    }

    // EFFECTS: returns either all the archived habits or the regular habits
    private List<Habit> getHabits(boolean isArchived) {
        List<Habit> allHabits = habitManager.getHabits();
        List<Habit> selectedHabits = new ArrayList<>();
        for (Habit habit : allHabits) {
            if (isArchived == habit.isArchived()) {
                selectedHabits.add(habit);
            }
        }
        return selectedHabits;
    }

    // MODIFIES: this
    // EFFECTS: adds habit rows to either the regular list tab or the archived list tab
    private void setupRows(List<Habit> habits, boolean isArchived) {
        JPanel panel;
        if (isArchived) {
            panel = archivedPanel;
        } else {
            panel = habitsPanel;
        }
        String text = isArchived ? "Archived Habits" : HabitManager.getUsername() + "'s Habits";
        setupFirstRows(panel, text);
        for (Habit habit : habits) {
            setupRow(panel, habit, isArchived);
        }
        if (habits.size() == 0) {
            JPanel noHabits = getNoHabits(isArchived);
            GridBagConstraints constraints = getNoHabitsConstraints();
            panel.add(noHabits, constraints);
        }
        JPanel emptySpace = makeGap(WINDOW_WIDTH - SIDE_BAR_WIDTH, 50);
        emptySpace.setBackground(APP_COLOUR);
        GridBagConstraints constraints = getEmptySpaceConstraints();
        panel.add(emptySpace, constraints);
    }

    // MODIFIES: panel
    // EFFECTS: setups first rows of habit list panel
    private void setupFirstRows(JPanel panel, String text) {
        addTitleRow(panel, text);
        addHeadingRow(panel);
    }

    // MODIFIES: panel
    // EFFECTS: adds title row to panel
    private void addTitleRow(JPanel panel, String text) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        JLabel title = new JLabel(text);
        title.setFont(HUGE_FONT);
        title.setForeground(FONT_COLOUR);
        titlePanel.add(title);
        titlePanel.setBackground(APP_COLOUR);
        GridBagConstraints titleConstraints = getTitleConstraints();
        panel.add(titlePanel, titleConstraints);
    }

    // MODIFIES: panel
    // EFFECTS: adds heading row to panel
    private void addHeadingRow(JPanel panel) {
        GridBagConstraints nameConstraints = getHeadingElementConstraints(0);
        GridBagConstraints completedConstraints = getHeadingElementConstraints(1);
        GridBagConstraints periodConstraints = getHeadingElementConstraints(2);
        GridBagConstraints notificationConstraints = getHeadingElementConstraints(3);
        GridBagConstraints addConstraints = getHeadingElementConstraints(4);
        JPanel namePanel = setupHeadingElementPanel();
        JPanel completedPanel = setupHeadingElementPanel();
        JPanel periodPanel = setupHeadingElementPanel();
        JPanel notificationsPanel = setupHeadingElementPanel();
        namePanel.add(setupLabel("Habit Name"));
        completedPanel.add(setupLabel("Progress"));
        periodPanel.add(setupLabel("Period"));
        notificationsPanel.add(setupLabel("Notifications"));
        panel.add(namePanel, nameConstraints);
        panel.add(completedPanel, completedConstraints);
        panel.add(periodPanel, periodConstraints);
        panel.add(notificationsPanel, notificationConstraints);
        panel.add(setupAddPanel(), addConstraints);
    }

    // EFFECTS: returns a JPanel for each heading element
    private JPanel setupHeadingElementPanel() {
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new FlowLayout());
        headingPanel.setBackground(APP_COLOUR);
        return headingPanel;
    }

    // EFFECTS: returns grid bag constraints for title
    private GridBagConstraints getTitleConstraints() {
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.gridwidth = 5;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        return titleConstraints;
    }

    /// EFFECTS: returns grid bag constraints for heading row
    private GridBagConstraints getHeadingElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // EFFECTS: returns JPanel containing clickable ADD_ICON
    private JPanel setupAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout());
        addPanel.setBackground(APP_COLOUR);
        addPanel.setPreferredSize(new Dimension(65, 65));
        addPanel.add(setupAddLabel());
        return addPanel;
    }

    // EFFECTS: returns JLabel containing clickable ADD_ICON
    private JLabel setupAddLabel() {
        JLabel add = new JLabel(ADD_ICON);
        add.setFont(MEDIUM_FONT);
        add.setPreferredSize(new Dimension(50, 50));
        add.addMouseListener(new MouseAdapter() {
            // EFFECTS: switches screens to createHabitUI
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(toCreateHabit);
            }

            // MODIFIES: this
            // EFFECTS: sets add icon to be slightly enlarged
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> add.setIcon(ADD_ICON_HOVER));
            }

            // MODIFIES: this
            // EFFECTS: sets add icon back to regular size
            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> add.setIcon(ADD_ICON));
            }
        });
        return add;
    }

    // EFFECTS: returns a label with the given text
    private JLabel setupLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        return label;
    }

    // MODIFIES: panel
    // EFFECTS: setups a row for the given habit and adds it to habitsPanel
    private void setupRow(JPanel panel, Habit habit, boolean isArchived) {
        JPanel empty = makeGap(WINDOW_WIDTH - SIDE_BAR_WIDTH, 1);
        GridBagConstraints fillingConstraints = getHabitRowFillingConstraints();
        panel.add(empty, fillingConstraints);
        JPanel namePanel = setupHabitPanel(habit.getName(), habit.isPeriodComplete());
        JPanel progressPanel =
                setupHabitPanel(habit.getNumSuccess() + " / " + habit.getFrequency(), habit.isPeriodComplete());
        JPanel periodPanel = setupHabitPanel(getPeriodString(habit), habit.isPeriodComplete());
        JPanel notificationsPanel = setupNotificationsPanel(habit, isArchived);
        JPanel deletePanel = setupDeletePanel(habit);
        HabitRowListener listener = new HabitRowListener(new JPanel[]
                {namePanel, progressPanel, periodPanel, notificationsPanel, deletePanel}, habit, isArchived);
        addCustomListener(namePanel, progressPanel, periodPanel, notificationsPanel, deletePanel, listener);
        panel.add(namePanel, getHabitElementConstraints(0));
        panel.add(progressPanel, getHabitElementConstraints(1));
        panel.add(periodPanel, getHabitElementConstraints(2));
        panel.add(notificationsPanel, getHabitElementConstraints(3));
        panel.add(deletePanel, getHabitElementConstraints(4));
    }

    // EFFECTS: returns grid bag constraints for empty space to fill up the bottom of grid bag layout
    private GridBagConstraints getEmptySpaceConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        return constraints;
    }

    // EFFECTS: returns JPanel to be displayed when there are no habits
    private JPanel getNoHabits(boolean isArchived) {
        JPanel noHabits = new JPanel();
        noHabits.setLayout(new FlowLayout());
        JLabel label = setupLabel(isArchived ? "No Archived Habits!" : "No Habits Yet!");
        label.setFont(BIG_FONT);
        noHabits.add(label);
        noHabits.setBackground(APP_COLOUR);
        noHabits.setPreferredSize(new Dimension(WINDOW_WIDTH - SIDE_BAR_WIDTH, 100));
        return noHabits;
    }

    // EFFECTS: returns grid bag constraints of no habit message
    private GridBagConstraints getNoHabitsConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // MODIFIES: namePanel, completedPanel periodPanel, notificationsPanel, deletePanel
    // EFFECTS: adds custom listener to all components in habit row to establish hover behaviour
    private void addCustomListener(JPanel namePanel, JPanel completedPanel, JPanel periodPanel,
                                   JPanel notificationsPanel, JPanel deletePanel,
                                   HabitRowListener listener) {
        namePanel.addMouseListener(listener);
        completedPanel.addMouseListener(listener);
        periodPanel.addMouseListener(listener);
        notificationsPanel.addMouseListener(listener);
        deletePanel.addMouseListener(listener);
    }

    // EFFECTS: returns grid bag constraints for white filling at the top of each habit row
    private GridBagConstraints getHabitRowFillingConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        return constraints;
    }

    // EFFECTS: returns grid bag constraints for each element in habit row
    private GridBagConstraints getHabitElementConstraints(int x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.ipady = 20;
        return constraints;
    }

    // EFFECTS: returns period string in title case ("Daily", "Weekly", etc.)
    private String getPeriodString(Habit habit) {
        String lower = habit.getPeriod().toString().toLowerCase();
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    // EFFECTS: returns notifications panel with clickable icon to toggle notifications on/off
    private JPanel setupNotificationsPanel(Habit habit, boolean isArchived) {
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new FlowLayout());
        notificationsPanel.setBackground(
                habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
        notificationsPanel.add(setupNotificationsLabel(habit, isArchived));
        notificationsPanel.setPreferredSize(new Dimension(30, 50));
        return notificationsPanel;
    }

    // EFFECTS: returns label containing either BELL_ON or BELL_OFF depending on whether notifications are enabled
    private JLabel setupNotificationsLabel(Habit habit, boolean isArchived) {
        JLabel notifications = new JLabel(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF);
        notifications.setFont(MEDIUM_FONT);
        notifications.setPreferredSize(new Dimension(30, 30));
        if (!isArchived) {
            setupNotificationsListener(notifications, habit);
        }
        return notifications;
    }

    // REQUIRES: !habit.isArchived()
    // MODIFIES: notifications
    // EFFECTS: adds listener to notifications label
    private void setupNotificationsListener(JLabel notifications, Habit habit) {
        notifications.addMouseListener(new MouseAdapter() {
            // MODIFIES: this
            // EFFECTS: prompts user to toggle notifications, if yes option is selected, notifications are toggled and
            //          habit list panel is updated
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> {
                    String message = habit.isNotifyEnabled() ? "Disable notifications?" : "Enable notifications?";
                    if (JOptionPane.showConfirmDialog(null, message, "Notifications", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        habit.toggleNotifyEnabled();
                        changeMade();
                        updateHabitList();
                    }
                });
            }

            // MODIFIES: this
            // EFFECTS: sets notifications icon to slightly enlarged icon
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> notifications.setIcon(habit.isNotifyEnabled() ? BELL_ON_HOVER : BELL_OFF_HOVER));
            }

            // MODIFIES: this
            // EFFECTS: sets notifications icon to regular sized icon
            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> notifications.setIcon(habit.isNotifyEnabled() ? BELL_ON : BELL_OFF));
            }
        });
    }

    // EFFECTS: returns small horizontal white bar that serves to subdivide each habit row
    private JPanel makeGap(int width, int height) {
        JPanel empty = new JPanel();
        empty.setBackground(FONT_COLOUR);
        empty.setMaximumSize(new Dimension(width, height));
        empty.setMinimumSize(new Dimension(width, height));
        empty.setPreferredSize(new Dimension(width, height));
        return empty;
    }

    // EFFECTS: returns habit row panel, background colour is green if current period complete
    private JPanel setupHabitPanel(String text, boolean isPeriodComplete) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(isPeriodComplete ? SUCCESS_GREEN : APP_COLOUR);
        panel.setPreferredSize(new Dimension((WINDOW_WIDTH - SIDE_BAR_WIDTH) / 6, 50));
        JLabel label = new JLabel(text);
        label.setFont(MEDIUM_FONT);
        label.setForeground(FONT_COLOUR);
        label.setBackground(isPeriodComplete ? SUCCESS_GREEN : APP_COLOUR);
        panel.add(label);
        return panel;
    }

    // EFFECTS: returns panel containing DELETE_ICON
    private JPanel setupDeletePanel(Habit habit) {
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new FlowLayout());
        deletePanel.setBackground(habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
        deletePanel.add(setupDeleteLabel(habit));
        deletePanel.setPreferredSize(new Dimension(30, 50));
        return deletePanel;
    }

    // EFFECTS: returns delete label containing DELETE_ICON
    private JLabel setupDeleteLabel(Habit habit) {
        JLabel delete = new JLabel(DELETE_ICON);
        delete.setFont(MEDIUM_FONT);
        delete.setPreferredSize(new Dimension(30, 30));
        setupDeleteListener(delete, habit);
        return delete;
    }

    // MODIFIES: delete
    // EFFECTS: adds listener to delete label
    private void setupDeleteListener(JLabel delete, Habit habit) {
        delete.addMouseListener(new MouseAdapter() {
            // MODIFIES: this
            // EFFECTS: user prompted to delete habit, if deleted, habit list panel updated
            @Override
            public void mouseClicked(MouseEvent e) {
                invokeLater(() -> deleteHabit(habit));
            }

            // MODIFIES: this
            // EFFECTS: sets delete icon to red DELETE_ICON_HOVER
            @Override
            public void mouseEntered(MouseEvent e) {
                invokeLater(() -> delete.setIcon(DELETE_ICON_HOVER));
            }

            // MODIFIES: this
            // EFFECTS: sets delete icon to white DELETE_ICON
            @Override
            public void mouseExited(MouseEvent e) {
                invokeLater(() -> delete.setIcon(DELETE_ICON));
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: prompts user to delete habit, if yes option selected, then delete the habit and update the habit list
    //          panel
    private void deleteHabit(Habit habit) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this habit?",
                "Delete Habit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            habitManager.deleteHabit(habit);
            changeMade();
            updateHabitList();
        }
    }

    // Helper class to ensure that all elements in habit row have the same hover behaviour and click behaviour
    public class HabitRowListener extends MouseAdapter {
        JPanel[] panels;
        Habit habit;
        boolean isArchived;
        JPopupMenu regularMenu;
        JPopupMenu archivedMenu;

        // EFFECTS: constructs a habit row listener with the given habit row elements and habit
        public HabitRowListener(JPanel[] panels, Habit habit, boolean isArchived) {
            this.panels = panels;
            this.habit = habit;
            this.isArchived = isArchived;
            setupPopupMenus();
        }

        // MODIFIES: this
        // EFFECTS: processes left-clicks and right-clicks on a habit row
        @Override
        public void mouseClicked(MouseEvent e) {
            invokeLater(() -> {
                if (isArchived) {
                    archivedHabitsClicked(e);
                } else {
                    regularHabitsClicked(e);
                }
            });
        }

        // MODIFIES: this
        // EFFECTS: changes background of all subcomponents in row to lighter colour
        @Override
        public void mouseEntered(MouseEvent e) {
            invokeLater(() -> {
                for (JPanel panel : panels) {
                    panel.setBackground(
                            habit.isPeriodComplete() ? SUCCESS_GREEN_LIGHT :
                                    APP_COLOUR_LIGHT);
                }
            });
        }

        // MODIFIES: this
        // EFFECTS: changes background of all subcomponents in row to regular colour
        @Override
        public void mouseExited(MouseEvent e) {
            invokeLater(() -> {
                for (JPanel panel : panels) {
                    panel.setBackground(habit.isPeriodComplete() ? SUCCESS_GREEN : APP_COLOUR);
                }
            });
        }

        // MODIFIES: this
        // EFFECTS: setups popup menus shown on right click
        private void setupPopupMenus() {
            setupRegularPopupMenu();
            setupArchivedPopupMenu();
        }

        // MODIFIES: this
        // EFFECTS: setups regular popup menu
        private void setupRegularPopupMenu() {
            regularMenu = new JPopupMenu();
            regularMenu.add(getFinishItem());
            regularMenu.add(getCloneItem());
            regularMenu.add(getArchiveItem("Archive the Habit"));
            regularMenu.add(getDeleteItem());
        }

        // MODIFIES: this
        // EFFECTS: setups archived popup menu
        private void setupArchivedPopupMenu() {
            archivedMenu = new JPopupMenu();
            archivedMenu.add(getCloneItem());
            archivedMenu.add(getArchiveItem("Unarchive the Habit"));
            archivedMenu.add(getDeleteItem());
        }

        // EFFECTS: returns finish item in popup menu
        private JMenuItem getFinishItem() {
            JMenuItem item = new JMenuItem("Finish the Habit");
            // MODIFIES: this
            // EFFECTS: if habit is finished successfully, get newly achieved achievements and add them to
            //          achievement queue, updates habit list panel
            item.addActionListener((e) -> {
                List<Achievement> current = habit.getAchievements();
                if (habit.finishHabit()) {
                    invokeLater(() -> {
                        changeMade();
                        List<Achievement> newlyAchieved = AchievementManager.getNewlyAchieved(
                                current, habit.getHabitStats(), habit.getPeriod());
                        for (Achievement achievement : newlyAchieved) {
                            achievementToast.add(new Pair<>(habit.getName(), achievement));
                        }
                        updateHabitList();
                    });
                }
            });
            return item;
        }

        // EFFECTS: returns clone item in popup menu
        private JMenuItem getCloneItem() {
            JMenuItem item = new JMenuItem("Clone the Habit");
            // MODIFIES: this
            // EFFECTS: creates a new habit with the same properties as current habit and adds it to
            //          habitManager, updates habit list panel
            item.addActionListener((e) -> invokeLater(() -> {
                habitManager.addHabit(new Habit(habit.getName(), habit.getDescription(),
                        habit.getPeriod(), habit.getFrequency(), habit.isNotifyEnabled(), habit.getClock()));
                changeMade();
                updateHabitList();
            }));
            return item;
        }

        // EFFECTS: returns archive item in popup menu with the given text
        private JMenuItem getArchiveItem(String text) {
            JMenuItem item = new JMenuItem(text);
            // MODIFIES: this
            // EFFECTS: toggle isArchived for the current habit and updates habit list panel
            item.addActionListener((e) -> invokeLater(() -> {
                habit.toggleIsArchived();
                changeMade();
                updateHabitList();
            }));
            return item;
        }

        // EFFECTS: returns delete item in popup menu
        private JMenuItem getDeleteItem() {
            Image icon = DELETE_ICON_HOVER.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
            ImageIcon deleteIcon = new ImageIcon(icon);
            JMenuItem item = new JMenuItem("Delete the Habit", deleteIcon);
            // MODIFIES: this
            // EFFECTS: deletes the habit from habitManager and updates habit list panel
            item.addActionListener((e) -> invokeLater(() -> deleteHabit(habit)));
            return item;
        }

        // MODIFIES: this
        // EFFECTS: if user right clicks archived habit, show archived menu at the mouse position
        private void archivedHabitsClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                archivedMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        // MODIFIES: this
        // EFFECTS: if user left clicks regular habit, go to habit screen
        //          if user right clicks regular habit, show regular habit menu at mouse position
        private void regularHabitsClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                HabitUI habitUI = new HabitUI(habit, achievementToast);
                parentPanel.add(habitUI, "habit");
                parentCardLayout.show(parentPanel, "habit");
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                regularMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}