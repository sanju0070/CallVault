/*
 * CallVault: FOSS call recording, self-contained over embedded ADB
 *  Copyright (C) 2026-present The CallVault Authors
 *  This software is licensed under the GNU General Public License v3 or later, with additional terms as permitted under Section 7.
 *  The full license text is available in the LICENSE file at the root of this project.
 *  This software is distributed WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.baba.callvault.system.updates

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.baba.callvault.data.AppPreferences
import com.baba.callvault.utils.AppLogger
import java.util.concurrent.TimeUnit

/**
 * Ensures the daily [UpdateCheckWorker] matches the "check for updates" preference. Mirrors the
 * idempotent [com.baba.callvault.system.storage.RetentionScheduler] pattern; call from
 * Application.onCreate and whenever the preference changes.
 */
object UpdateScheduler {

    private const val TAG = "CV:UpdateScheduler"
    private const val WORK_NAME = "cv_update_check"
    private const val CHECK_NOW_WORK_NAME = "cv_update_check_now"
    // Every 6 h, not 24 (2026-09-29): a user who rarely opens the app still learns of a release the
    // same day. A single conditional GitHub GET, well inside the 60/h anonymous rate limit.
    private const val PERIOD_HOURS = 6L

    /**
     * Minimum gap between check-on-open triggers, so relaunches can't hammer the GitHub API. 30 min
     * (was 6 h): the point of the on-open check is that opening the app surfaces a release promptly,
     * which a 6 h gate defeated for anyone who had opened it earlier the same day.
     */
    private const val CHECK_ON_OPEN_THROTTLE_MS = 30 * 60 * 1000L

    /** Unique name of the user-initiated install work; the Home banner observes its state. */
    const val INSTALL_WORK_NAME = "cv_update_install"

    fun apply(context: Context) {
        // Update checks are removed in this build: cancel anything an earlier install scheduled.
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_NAME)
        workManager.cancelUniqueWork(CHECK_NOW_WORK_NAME)
        workManager.cancelUniqueWork(INSTALL_WORK_NAME)
    }

    /** No-op: this build never checks for updates. */
    fun checkNowIfDue(context: Context) = Unit

    /** No-op: this build never downloads or installs updates. */
    fun enqueueInstallNow(context: Context) = Unit
}
