# ElderCare Features and Functionalities

This document provides a clean reference of the system's implemented capabilities.

## 1) User Roles

### Elderly

- Access elderly dashboard and reminders
- Save monthly health readings
- Schedule medications and appointments
- View notifications, missed medications, and reading history

### Caregiver

- Access caregiver dashboard
- Review elderly pending medications
- Review elderly health readings
- View reading result and missed medication sections

## 2) Authentication

- Role-based login entry
- Registration flow per selected role
- Forgot password flow via Firebase Authentication email reset
- Session-based role routing after successful login

## 3) Reminder and Medication System

- Medication reminders are saved in Firestore
- Appointment reminders are saved in Firestore
- Local alarm scheduling for reminder alerts
- Reminder alert screen supports action handling:
  - Mark as `DONE`
  - Mark as `MISSED` (medications)

### Medication Status Flow

- `PENDING` when scheduled
- `DONE` when user marks medication as completed
- `MISSED` when explicitly missed or detected overdue by worker logic

## 4) Medication Tracker Behavior

- `Reminders` shows pending medications and appointments
- `Medication Tracker` shows only handled medications:
  - Green style for `DONE`
  - Red style for `MISSED`

## 5) Health Reading Features

- Save blood pressure, date, weight, and heart rate
- Monthly uniqueness behavior for health reading records
- Reading results grouped and navigable by month

## 6) Notifications

- Notification channels for:
  - Medication reminders
  - Appointment reminders
  - Missed medication alerts
- Sound/vibration preference support
- Missed medication periodic scan through WorkManager

## 7) Settings and Support

- Manage profile
- Notification controls
- Security and change password
- Privacy controls and policy
- About/help pages:
  - User guide
  - FAQs
  - Contact support

## 8) UI/Accessibility Direction

- Large and clear section titles on key screens
- Larger text for elderly readability in critical flows
- Friendly and simplified wording in forgot password flow
- Clear color indicators for medication outcomes
