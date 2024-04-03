# HabitHaven 

## Overview

The ability to form good habits is integral. HabitHaven is an application that 
aids users in forming good habits.  

## Target Audience

My application is meant for anyone desiring to transform their life by forming good
habits.

## Features
- Create and manage a list of habits
- Customize a habit by frequency - daily, weekly, or monthly
- Customizable notifications - users receive customizable reminders for each habit
- Gamify habit formation
  - Achievements for reaching milestones
- Ability to view in-depth statistics for a specific habit (completion rate, number of 
completions, etc.)
## Personal Significance
 This project is of interest to me as a student interested in growth. Even now, I can 
 think of many small habits that I can strive for. An app would help organize the 
 habit formation process, bringing the user one step closer to transforming their
 life.
 ## User Stories (All Implemented)
- As a user, I want to be able to add a habit to my list of habits and specify the
name, description, period, and frequency. 
- As a user, I want to be able to finish and undo finishing a habit.
- As a user, I want to be able to view a list of all my habits.
- As a user, I want to be able to edit my habits.
- As a user, I want to be able to change my username.
- As a user, I want to be able to delete my habits.
- As a user, I want to be able to delete all my habits at once.
- As a user, I want to be able to view in-depth statistics for each habit.
- As a user, I want to be able to view lifetime statistics for all of my active habits.
- As a user, I want to have the option to receive notifications for each habit.
- As a user, I want to have the option to either customize the notifications or keep the default settings.
- As a user, I want to be able to disable all my notifications at once.
- As a user, I want to be given the option to load my habits from file (if I so choose).
- As a user, when I quit the application with unsaved changes, I want to be reminded to save my habits to file and have the option to do so or not.
- Or, as a user, I want to be able to have the option to enable auto-save.
- As a user, I want to be given the option to export my save file to my computer.
- As a user, I want to be given the option to import a save file from my computer.
- As a user, I want to be able to view my achievements for each habit.
- As a user, I want to be shown an achievement toast when I earn a new achievement.
- As a user, I want to be able to toggle on/off achievement toasts.
- As a user I want to have the option to make the application exit on close or hide on close.
- As a user, I want to be able to archive and unarchive a habit. 
- As a user, I want to be able to make a copy of a habit.
## Instructions for Grader
- You can add habits to your habit list in one of two ways: 
  - Click the "Create Habit" option in the sidebar.
  - Click the + button in the last column of the heading row of the habit list.
- You can view a specific habit by clicking the corresponding row in the habit list.
  - You can edit, complete, and undo completing your habit in the "Habit" tab of the tabbed pane.
  - You can view habit statistics in the "Statistics" tab. 
  - You can customize and view notifications in the "Notifications" tab.
  - You can view habit achievements in the "Achievements" tab.
- You can delete a habit by clicking the garbage can icon in the appropriate row of the habit list. 
- Alternatively, you can delete a habit by right-clicking and selecting the "Delete the Habit" option.
- You can delete all habits by clicking the "Settings" option in the sidebar and clicking "Delete All Habits."
- You can make a copy of a habit by right-clicking and selecting the "Clone the Habit" option.
- You can archive a habit by right-clicking and selecting the "Archive the Habit" option.
- You can unarchive a habit by right-clicking a habit in the "Archived Habits" tab and selecting the "Unarchive the Habit" option.
- Hide on close and exit on close
  - You can toggle the application to exit on close or hide on close. If you click a toast notification while the application is hidden, the application is re-opened back to where you left off.
- You can locate my visual component in many places:
  - After earning an achievement, you can hear a sound effect being played and an achievement toast being displayed. These toasts can be toggled on and off in the "Settings" sidebar option.
  - Once my application launches, the HabitHaven logo can be seen above the two buttons.
  - The HabitHaven logo can be seen on the sidebar. 
  - Icons for each option on the sidebar can be seen on the sidebar. 
  - Once within a habit, icons can be seen within each of the tabs of the tabbed pane.
  - Icons for each achievement tier can be seen in the habit achievements.
  - Some more icons can be seen in the settings sidebar option.
- You can save the state of my application in one of three ways: 
  - Click the "Save to File" option in the sidebar.
  - Close the application with unsaved changes - you will be prompted to save, click "Yes".
  - Export your save file to your pc by clicking the "Export to File" button in the "Settings" sidebar option.
  - Turn on auto save in the "Settings" sidebar option.
- You can reload the state of my application in one of two ways:
  - Press the "Load from File" button in the start screen.
  - Click the "Import from File" button to select a save file on your pc.
## Phase 4: Task 2
```
Mon Apr 01 17:00:18 PDT 2024
Added new habit "drink water" with id 63695479-23a9-48ad-af24-28b809c6056f to habit manager
Mon Apr 01 17:00:39 PDT 2024
Name of habit with id fe931308-f19c-4545-b4c7-25edf0c82274 changed to "eetcode"
Mon Apr 01 17:00:40 PDT 2024
Name of habit with id fe931308-f19c-4545-b4c7-25edf0c82274 changed to "Leetcode"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "j"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "jo"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "jog"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "jogg"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "joggi"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "joggin"
Mon Apr 01 17:01:16 PDT 2024
Description of habit with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to "jogging"
Mon Apr 01 17:01:33 PDT 2024
Notifications of habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a enabled
Mon Apr 01 17:01:36 PDT 2024
Notifications of habit "Leetcode" with id fe931308-f19c-4545-b4c7-25edf0c82274 disabled
Mon Apr 01 17:01:40 PDT 2024
Notifications of habit "Daily Math 200" with id 450aff2e-9574-485f-a641-72aefa75574a disabled
Mon Apr 01 17:01:56 PDT 2024
Frequency of habit "Daily Math 200" with id 450aff2e-9574-485f-a641-72aefa75574a changed to 2
Mon Apr 01 17:02:13 PDT 2024
Period of habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a changed to DAILY
Mon Apr 01 17:02:22 PDT 2024
Habit "do yoga" with id bf05ed23-1737-46cd-a2a9-2cacf4cd6392 unarchived
Mon Apr 01 17:02:30 PDT 2024
Habit "drink water" with id 63695479-23a9-48ad-af24-28b809c6056f archived
Mon Apr 01 17:02:46 PDT 2024
Habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a completed
Mon Apr 01 17:02:46 PDT 2024
Habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a completed
Mon Apr 01 17:02:46 PDT 2024
Habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a completed for the period
Mon Apr 01 17:02:52 PDT 2024
Habit "jogging" with id 3c97de3e-d851-4e95-a017-6b7c761b549a uncompleted
Mon Apr 01 17:03:17 PDT 2024
Username set to "Gregor"
Mon Apr 01 17:03:23 PDT 2024
Auto save turned off
Mon Apr 01 17:03:26 PDT 2024
Auto save turned on
Mon Apr 01 17:03:28 PDT 2024
Auto save turned off
Mon Apr 01 17:03:36 PDT 2024
Achievement toasts turned off
Mon Apr 01 17:03:36 PDT 2024
Achievement toasts turned on
Mon Apr 01 17:03:40 PDT 2024
Application set to hide on close
Mon Apr 01 17:03:41 PDT 2024
Application set to exit on close
Mon Apr 01 17:03:50 PDT 2024
Removed habit "drink water" with id 63695479-23a9-48ad-af24-28b809c6056f from habit manager
```
## Phase 4: Task 3
There are several areas in which I can potentially improve the design of my application. In particular, the cohesion
of many of my classes could be improved. For instance, my HabitUI class has two distinct roles - to act as the container
for each constituent tab (Habit tab, Statistics tab, Notifications tab, Achievements tab) but also to 
implement the habit tab. Cohesion could be improved by splitting the HabitUI into two separate
classes - one acting as the container JPanel and one dealing only with the Habit tab. A similar argument could be made 
for my HabitListUI class. There really are three responsibilities here: coordinating the tabs, setting up the regular 
habits tab, and setting up the archived habits tab. A potential solution is to create an abstract class for all the 
common elements between the regular tab and the archived tab. A container class responsible for setting up the tabs and 
coordinating them would instantiate both concrete classes that extend the abstract class.

In my HabitRemindersUI class and subclasses, a common feature is that JSpinners always get committed before they are 
processed (after a button press). When implementing this behaviour, it was very tedious to find every relevant button 
listener across the four classes and ensure that all relevant JSpinners are committed. Even now, I still cannot be fully 
certain that all the JSpinners get committed when they need to be, causing some potential inconsistency. To deal with 
this issue, I could create a custom listener that takes has a field of a list of JSpinners. I could add this listener 
to each submit button and override actionPerformed for each specific button. Furthermore, while I attempted to create a 
general purpose commitSpinners method in my HabitRemindersUI abstract class, it only takes two JSpinners
as parameters. A potential solution would be to have the commitSpinners method take a list of JSpinners as a parameter,
establishing a single point of control.