package uk.co.explose.schminder.android.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier

import androidx.camera.view.PreviewView
import androidx.camera.core.Preview

import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import uk.co.explose.schminder.android.FabItem
import uk.co.explose.schminder.android.core.AppGlobal
import uk.co.explose.schminder.android.model.mpp.m_med_indiv
import uk.co.explose.schminder.android.model.mpp.m_medication

@ExperimentalGetImage
@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrescriptionScanScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current as? Activity

    LockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED //ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    val outputText = remember { mutableStateOf("Scan a prescription...") }
    val parsedMeds = remember { mutableStateOf<List<m_med_indiv>>(emptyList()) }
    val scanComplete = remember { mutableStateOf(false) }

    val cameraPermission = android.Manifest.permission.CAMERA
    val permissionState = rememberPermissionState(permission = cameraPermission)

    val previewView = remember { PreviewView(context) }
    val knownMeds = AppGlobal.doMedsIndivInfoRead()
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when (val status = permissionState.status) {
            is PermissionStatus.Granted -> {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

                LaunchedEffect(Unit) {
                    val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        if (scanComplete.value) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val inputImage = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            recognizer.process(inputImage)
                                .addOnSuccessListener { visionText ->
                                    val rawText = visionText.text
                                    if (knownMeds != null) {
                                        val newMeds = parseMedicationsFromOcr(
                                            rawText,
                                            knownMeds.med_indiv_list
                                        )
                                        val existingMeds = parsedMeds.value

                                        val uniqueMeds = newMeds.filter { newMed ->
                                            existingMeds.none { it.med_id == newMed.med_id }
                                        }

                                        if (uniqueMeds.isNotEmpty()) {
                                            outputText.value = rawText
                                            parsedMeds.value = existingMeds + uniqueMeds
                                            // scanComplete.value = true
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    outputText.value = "OCR Failed"
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, analysis)

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                }
            }

            is PermissionStatus.Denied -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission required to scan prescriptions.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Bottom overlay UI (displayed only if scan is complete)
        if (scanComplete.value || true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 300.dp) // adjust as needed
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .verticalScroll(rememberScrollState()) // ✅ scrollable now
                    .padding(16.dp)
            ) {
                Text(text = "Detected Medications:", color = Color.White)
                parsedMeds.value.forEach { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• ${med.med_name}",
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
/*
                        IconButton(onClick = {
                            navController.navigate("add_med")
                        }) {
                            Icon(Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White)
                        }
*/
                        IconButton(onClick = {
                            // Remove this item from the list
                            parsedMeds.value = parsedMeds.value.filterNot { it.med_id == med.med_id }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        navController.popBackStack()
                        // TODO: Send parsedMeds.value to API or next screen
                    }) {
                        Text("Cancel")
                    }

                    if (parsedMeds.value.isNotEmpty()) {
                        Button(onClick = {
                            activity?.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            navController.popBackStack()
                            // TODO: Send parsedMeds.value to API or next screen
                        }) {
                            Text("Add to medication list")
                        }
                    }
                }
            }
        }
    }
}

fun parseMedicationsFromOcr(
    text: String,
    knownMeds: List<m_med_indiv>
): List<m_med_indiv> {
    val words = text.split(Regex("""\W+"""))
        .map { it.lowercase() }
        .toSet()

    return knownMeds.filter { med ->
        med.med_name.lowercase() in words
    }
}



@Composable
fun LockOrientation(orientation: Int) {
    val activity = LocalContext.current as? Activity

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = orientation
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
