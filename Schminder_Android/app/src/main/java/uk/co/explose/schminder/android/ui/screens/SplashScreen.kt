
package uk.co.explose.schminder.android.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope


import uk.co.explose.schminder.android.core.AppRepo
import uk.co.explose.schminder.android.repo.Resource

@Composable
fun SplashScreen(appScope: CoroutineScope, onComplete: () -> Unit, onRetry: () -> Unit) {

    var loadingComplete by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var hasError by remember { mutableStateOf(false) }

    fun retry() {
        (context as? Activity)?.let { activity ->
            AppRepo.loadAppData(
                activity = activity,
                appScope = appScope,
                onComplete = {
                    hasError = false
                    loadingComplete = true
                },
                onError = {
                    hasError = true
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        retry()
    }

    // Navigate once loading completes
    LaunchedEffect(loadingComplete) {
        if (loadingComplete) {
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Schminder", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            if (hasError) {
                Button(onClick = {
                    retry()
                    hasError = false
                }) {
                    Text("Data load failure - click to retry")
                }
            }
            else {
                CircularProgressIndicator()
            }
        }
    }
}




