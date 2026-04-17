# Contributing to ElderCare

Thanks for helping improve ElderCare.

This guide keeps contributions clean, consistent, and easy to review.

## Development Setup

1. Install Android Studio (latest stable).
2. Install JDK 17.
3. Clone this repository.
4. Add your Firebase config file at `app/google-services.json`.
5. Sync Gradle and run:
   - Windows: `.\gradlew.bat assembleDebug`
   - macOS/Linux: `./gradlew assembleDebug`

## Branching and Commits

- Create a feature branch from `main`:
  - `git checkout -b feature/<short-description>`
- Keep commits focused and descriptive.
- Suggested commit format:
  - `Add ...`
  - `Update ...`
  - `Fix ...`

## Code Style

- Use Kotlin + Jetpack Compose best practices.
- Keep composables small and readable.
- Use meaningful names for state and callbacks.
- Reuse existing theme colors and typography where possible.
- Prefer accessibility-friendly text sizes and contrast.

## Functional Expectations

Please preserve existing behavior unless explicitly changing it:

- Medication scheduling and tracker status flow
- Reminder and notification timing behavior
- Role-based navigation (`Elderly` vs `Caregiver`)
- Firebase Auth + Firestore data interactions

## Before You Submit a PR

- Build passes (`assembleDebug`).
- App launches and navigation works.
- Modified screens are manually tested.
- No secrets or private keys are committed.
- Docs are updated if behavior or flows changed.

## Pull Request Checklist

- Clear title and summary
- Reason for change
- Screenshots/GIFs for UI updates
- Testing notes (what you tested)
- Any migration or setup notes

## Security Notes

- Never commit credentials, API keys, or private files.
- Keep `google-services.json` safe for intended environment only.

Thank you for contributing to ElderCare.
