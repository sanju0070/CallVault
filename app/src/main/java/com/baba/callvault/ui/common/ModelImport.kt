/*
 * CallVault (modified build): models are imported from a file, never downloaded.
 *  This software is licensed under the GNU General Public License v3 or later (see LICENSE).
 */

package com.baba.callvault.ui.common

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.baba.callvault.R
import com.baba.callvault.transcription.model.DownloadableModel
import com.baba.callvault.transcription.model.ModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Returns an action that opens the system file picker and installs the chosen file as [model].
 * The file must be the exact model file (size and SHA-256 are checked); [onFinished] runs afterwards
 * whether or not it worked, so the caller can re-read what is on disk.
 */
@Composable
fun rememberModelImporter(model: DownloadableModel, onFinished: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    ModelRepository.importFromUri(context.applicationContext, model, uri)
                }
                Toast.makeText(
                    context,
                    if (ok) R.string.model_import_done else R.string.model_import_failed,
                    Toast.LENGTH_LONG
                ).show()
                onFinished()
            }
        }
    }
    return remember(launcher) { { launcher.launch(arrayOf("*/*")) } }
}
