# ElderCare

<p align="center">
  <img src="app/src/main/res/drawable/eldercare_logo.jpg" alt="ElderCare Logo" width="140" />
</p>

<p align="center">
  ElderCare is an Android app for elderly self-health monitoring and caregiver support.
  <br/>
  It combines reminders, medication tracking, health readings, notifications, and account management in one accessible app.
</p>

---

## Overview

ElderCare is built for two user roles:

- `Elderly` - manage medications, appointments, health readings, and notifications
- `Caregiver` - monitor elderly medication compliance and health reading updates

The app is designed with readability-focused UI decisions and simplified user flows.

---

## Core Features

### Authentication

- Role selection (`Elderly` or `Caregiver`)
- Sign in and registration per role
- Forgot password flow using Firebase Authentication email reset

### Elderly Dashboard

- Home with:
  - `Reminders` (pending medications and appointments)
  - `Medication Tracker` (status-focused tracker entries)
- Set Reminder area:
  - Save monthly health reading
  - Schedule medication
  - Schedule appointment
- Notifications:
  - Reading Results entry point
  - Missed Medications entry point
- Health History:
  - Reading Results by month
  - Missed Medications by month

### Medication Logic (Current Behavior)

- Newly scheduled medication appears in `Reminders` as pending
- It does **not** immediately appear in `Medication Tracker`
- Medication appears in tracker only after status is:
  - `DONE` (taken)
  - `MISSED` (not taken)
- Status colors:
  - Green = taken (`DONE`)
  - Red = missed (`MISSED`)

### Caregiver Dashboard

- Pending Medication Tracker for monitored users
- Elderly Health Readings view
- Notifications access:
  - Reading Results
  - Missed Medications

### Notification System

- Local reminder alarms for medications and appointments
- Missed medication periodic checks via WorkManager
- Configurable channel behavior (sound/vibration preferences)
- Full-screen capable reminder alert presentation

### Settings and Support

- Manage profile
- Notification controls
- Caregiver settings
- Security and change password
- Privacy controls and policy
- About/help, FAQs, user guide, contact support

---

## Technology Stack

- `Kotlin`
- `Jetpack Compose` + `Material 3`
- `Navigation Compose`
- `Firebase Authentication`
- `Firebase Firestore`
- `Firebase Cloud Messaging`
- `WorkManager`
- `Hilt` (dependency injection)

Build targets:

- `minSdk 26`
- `targetSdk 35`
- `compileSdk 35`
- Java/Kotlin target `17`

---

## Project Structure

```text
app/
  src/main/java/com/eldercare/app/
    ui/
      auth/          # Login, Register, Forgot Password, Role Selection
      dashboard/     # Elderly/Caregiver dashboards, reminders, health views
      navigation/    # Routes and NavHost graph
      settings/      # Settings and support screens
      theme/         # Colors, typography, theming helpers
    data/
      model/         # Domain/data models
      repository/    # Auth repository layer
    notification/    # Reminder scheduling, notification channels, workers
    di/              # Hilt modules
```

---

## Setup Guide

## 1) Prerequisites

- Android Studio (latest stable recommended)
- JDK 17
- Android SDK with API 35
- Firebase project

## 2) Firebase Config

- Place your Firebase config file at:
  - `app/google-services.json`
- Enable in Firebase Console:
  - Authentication (Email/Password)
  - Firestore Database
  - Cloud Messaging (optional but recommended)

## 3) Build and Run

From Android Studio:

- Open project
- Sync Gradle
- Run on emulator/device

From terminal:

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

---

## Data Collections (Firestore)

Typical collections used:

- `users`
- `reminders`
- `health_readings`

Reminder documents include medication status fields used for tracker logic:

- `type` (`medication` or `appointment`)
- `isCompleted` (`true`/`false`)
- `medicationStatus` (`PENDING`, `DONE`, `MISSED`)

---

## UI and Accessibility Direction

The app prioritizes elderly readability:

- Larger text in key screens
- Clear screen titles and segmented sections
- Simple, direct instructions (especially auth and password recovery)
- Color-based medication outcome indicators (green/red)

---

## Notes

- Firebase default password reset email template editing may be restricted depending on account/project setup.
- If template editing is blocked, ElderCare still provides clear in-app guidance for the reset process.

---

## Documentation

- Contribution guide: `CONTRIBUTING.md`
- Feature reference: `docs/FEATURES.md`
- Screenshot checklist: `docs/SCREENSHOTS.md`

---

## License

This project is licensed under the MIT License. See `LICENSE`.
