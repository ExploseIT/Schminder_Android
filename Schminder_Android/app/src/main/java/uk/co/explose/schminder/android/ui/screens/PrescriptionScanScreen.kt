package uk.co.explose.schminder.android.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo

import androidx.camera.core.CameraSelector
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import uk.co.explose.schminder.android.model.mpp.m_medication

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrescriptionScanScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalContext.current as? Activity

    LockOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    val outputText = remember { mutableStateOf("Scan a prescription...") }
    val parsedMeds = remember { mutableStateOf<List<m_medication>>(emptyList()) }
    val scanComplete = remember { mutableStateOf(false) }

    val cameraPermission = android.Manifest.permission.CAMERA
    val permissionState = rememberPermissionState(permission = cameraPermission)

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                                    val meds = parseMedicationsFromOcr(rawText)
                                    if (meds.size >= 1) {
                                        outputText.value = rawText
                                        parsedMeds.value = meds
                                        scanComplete.value = true
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
        if (scanComplete.value) {
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
                    Text("• ${med.med_name}", color = Color.White)
                    Text("  Route: ${med.med_route}", color = Color.White)
                    Text("  Dosage: ${med.med_dosage}", color = Color.White)
                    Text("  Frequency: ${med.med_frequency}", color = Color.White)
                    Text("  Start: ${med.med_startDate}", color = Color.White)
                    med.med_stopAfter?.let {
                        Text("  Stop After: $it", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(onClick = {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    navController.popBackStack()
                    // TODO: Send parsedMeds.value to API or next screen
                }) {
                    Text("Done")
                }
            }
        }
    }
}

fun parseMedicationsFromOcr(text: String): List<m_medication> {
    val lines = text.lines()

    val startIndex = lines.indexOfFirst { it.contains("Active Medications", ignoreCase = true) }
    val endIndex = lines.indexOfFirst { it.contains("People Completing Record", ignoreCase = true) }

    if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) return emptyList()

    val medsSection = lines.subList(startIndex + 1, endIndex)
        .filter { it.isNotBlank() }

    val medications = mutableListOf<m_medication>()
    var current: MutableList<String> = mutableListOf()

    fun flush() {
        if (current.isEmpty()) return

        val combined = current.joinToString(" ")
        val dateRegex = Regex("""\d{2}-[A-Za-z]{3}-\d{4}""")
        val date = dateRegex.find(combined)?.value ?: ""
        val name = Regex("""(?<=\d{4}\s)(.+?)(?=Route:)""").find(combined)?.value?.trim() ?: ""
        val route = Regex("""Route:\s*(\w+(?: \w+)?)""").find(combined)?.groupValues?.get(1) ?: ""
        val dosage = Regex("""Give\s+([\w\d\s.,%/]+)""").find(combined)?.groupValues?.get(1)
            ?: Regex("""Use\s+([\w\d\s.,%/]+)""").find(combined)?.groupValues?.get(1) ?: ""
        val frequency = Regex("""Frequency:\s*(.+?)($|Stop|Use)""").find(combined)?.groupValues?.get(1)?.trim() ?: ""
        val stopAfter = Regex("""Stop after\s+([\w\s]+)""").find(combined)?.groupValues?.get(1)?.trim()

        medications.add(
            m_medication(
                med_name = name,
                med_route = route,
                med_dosage = dosage,
                med_frequency = frequency,
                med_startDate = date,
                med_stopAfter = stopAfter
            )
        )
        current.clear()
    }

    for (line in medsSection) {
        if (line.matches(Regex("""\d{2}-[A-Za-z]{3}-\d{4}.*"""))) {
            flush()
        }
        current.add(line)
    }

    flush() // capture last block

    return medications
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
