/*
 * CallVault (private build): features that are switched off and hidden.
 *  This software is licensed under the GNU General Public License v3 or later (see LICENSE).
 */

package com.baba.callvault.data

/**
 * Compile-time switches for features this build hides. The code behind them is left in place but
 * every way to reach it (screens, schedulers, preferences) checks these first.
 */
object FeatureFlags {
    /** Speech-to-text and AI call summaries. Off: no screens, no background work, no auto-run. */
    const val TRANSCRIPTION = false

    /** Shizuku as a recording backend. Off: embedded ADB is the only mode. */
    const val SHIZUKU = false

    /** Update-check settings (the updater itself is already removed). */
    const val UPDATES = false
}
