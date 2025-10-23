# 🌝 HabitHaven

## Overview

**HabitHaven** is a personal productivity app designed to help users build and maintain positive habits through structured tracking, reminders, and gamification. It provides a visually organized and engaging way to stay consistent and celebrate progress.

## Key Features

* **Comprehensive habit management:** create, edit, clone, archive, and delete habits
* **Flexible scheduling:** set habits as daily, weekly, or monthly
* **Custom reminders:** fully configurable notifications per habit
* **Gamification system:** earn achievements for reaching milestones
* **Analytics dashboard:** view completion rates and detailed statistics for each habit
* **Backup & persistence:** import/export progress and enable auto-save for convenience
* **User customization:** modify username, toggle visual/audio effects, and control app behavior (exit or hide on close)

## Motivation

As a student focused on personal growth, I wanted to create a tool that reinforces consistency and organization — two qualities essential to self-improvement. HabitHaven represents both a technical challenge and a reflection of my interest in behavioral design.

## Tech Stack

* **Language:** Java
* **UI Framework:** Swing
* **Architecture:** Modular MVC structure with data persistence via file serialization
* **Tools:** IntelliJ IDEA, Gradle

## Implementation Highlights

* Built a **multi-tabbed UI system** (Habits, Statistics, Notifications, Achievements) with custom icons and toast notifications.
* Developed a **notification system** supporting both default and user-defined reminders.
* Designed an **achievement engine** that triggers visual/audio feedback upon milestones.
* Implemented **auto-save and import/export** functionality to persist user data across sessions.
* Focused on **clean code and extensibility**, planning future refactors to improve class cohesion and abstraction.

## Future Improvements

* Refactor large UI classes into smaller, more cohesive components.
* Introduce a unified listener system for better input consistency.
* Explore migrating to a modern UI toolkit such as JavaFX or Jetpack Compose Desktop.
