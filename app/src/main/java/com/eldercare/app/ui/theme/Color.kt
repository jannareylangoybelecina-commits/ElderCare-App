package com.eldercare.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Colors (from design mockups) ───────────────────────
val ElderCareBlue = Color(0xFF2B7EC1)          // Primary blue from logo
val ElderCareGreen = Color(0xFF6BBF59)         // Green from logo / buttons
val ElderCareDarkBlue = Color(0xFF1A3A5C)      // Dark navy for headings
val ElderCareGray = Color(0xFF7A8A99)          // Subtitle gray
val ElderCareLightBlue = Color(0xFFD6EAF8)     // Light blue gradient at bottom
val ElderCareLightGreen = Color(0xFFB8E6B0)    // Light green for caregiver button

// ── NEW Design Colors ────────────────────────────────────────
val ElderCareReminderGreen = Color(0xFFCCF6DA) // #CCF6DA - Reminders & Medication Tracker base
val ElderCareElderlyBlue = Color(0xFFA2CAE8)   // #A2CAE8 - Elderly role button
val MedicationMissedRed = Color(0xFFE53935)    // Red for missed medication status
val MedicationTakenGreen = Color(0xFF43A047)   // Green for taken medication status

// ── Primary Palette ──────────────────────────────────────────
val PrimaryLight = ElderCareBlue
val PrimaryDark = Color(0xFF7BB8E0)
val OnPrimaryLight = Color(0xFFFFFFFF)
val OnPrimaryDark = Color(0xFF002D4F)

// ── Secondary Palette (Green) ────────────────────────────────
val SecondaryLight = ElderCareGreen
val SecondaryDark = Color(0xFF8CD47E)
val OnSecondaryLight = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xFF1B3A14)

// ── Tertiary ─────────────────────────────────────────────────
val TertiaryLight = Color(0xFF4A9BC7)
val TertiaryDark = Color(0xFF8BC4E0)

// ── Backgrounds ──────────────────────────────────────────────
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF0F1A19)
val SurfaceDark = Color(0xFF1A2B2A)

// ── Error ────────────────────────────────────────────────────
val ErrorLight = Color(0xFFD32F2F)
val ErrorDark = Color(0xFFEF9A9A)

// ── Outlines & Surface Variants ──────────────────────────────
val OutlineLight = Color(0xFFCCD6DD)
val OutlineDark = Color(0xFF546E7A)
val SurfaceVariantLight = Color(0xFFF0F4F8)
val SurfaceVariantDark = Color(0xFF253332)

// ── Text Colors ──────────────────────────────────────────────
val OnBackgroundLight = ElderCareDarkBlue
val OnBackgroundDark = Color(0xFFE3E2E6)
val OnSurfaceLight = ElderCareDarkBlue
val OnSurfaceDark = Color(0xFFE3E2E6)
