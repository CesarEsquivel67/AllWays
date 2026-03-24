package com.allways.hackaton.ui.addinfo

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInfoScreen(
    navController: NavController,
    placeId: String,
    viewModel: AddInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { viewModel.setPhotoUri(it) }
    }

    val features = listOf(
        "hasBraille" to "Has Braille signage",
        "hasCaneBumps" to "Has cane / tactile bumps",
        "hasAssistanceButton" to "Has assistance button",
        "hasWheelchairRamp" to "Has wheelchair ramp",
        "hasAccessibleParking" to "Has accessible parking",
        "hasAccessibleBathroom" to "Has accessible bathroom",
        "hasAudioSignals" to "Has audio signals / alerts"
    )

    // Efecto para navegar cuando se complete el envío
    LaunchedEffect(uiState.submitted) {
        if (uiState.submitted) {
            // Esperar 2 segundos y luego navegar de vuelta
            kotlinx.coroutines.delay(2000)
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Accessibility Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Answer what you know about this place:",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            features.forEach { (key, label) ->
                AccessibilityRow(
                    label = label,
                    value = uiState.answers[key],
                    onYes = { viewModel.setAnswer(key, true) },
                    onNo = { viewModel.setAnswer(key, false) }
                )
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Add a photo (optional)", fontSize = 14.sp)

            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedImageUri == null) "📷 Choose Photo" else "📷 Change Photo")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Additional comments (optional)", fontSize = 14.sp)

            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.setComment(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Describe anything else that might help others...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ============================================
            // MOSTRAR ESTADO DE RECOMPENSA SOLANA
            // ============================================
            when (uiState.rewardStatus) {
                is RewardStatus.LOADING -> {
                    // Estado de carga
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Procesando tu recompensa...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                is RewardStatus.SUCCESS -> {
                    // Estado de éxito
                    val successStatus = uiState.rewardStatus as RewardStatus.SUCCESS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "✅ ¡Éxito!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = successStatus.message,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Ganaste ${successStatus.amount} tokens Solana 🎉",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                is RewardStatus.ERROR -> {
                    // Estado de error
                    val errorStatus = uiState.rewardStatus as RewardStatus.ERROR
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚠️ Error al procesar recompensa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = errorStatus.message,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Tu información fue guardada correctamente, pero hubo un problema con los tokens.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                else -> {}
            }

            // ============================================
            // MOSTRAR ERRORES GENERALES
            // ============================================
            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ============================================
            // BOTONES DE ACCIÓN
            // ============================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    Text("Go Back")
                }

                Button(
                    onClick = { viewModel.submit(placeId, context) },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading && !uiState.submitted
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
fun AccessibilityRow(
    label: String,
    value: Boolean?,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Row {
            FilterChip(
                selected = value == true,
                onClick = onYes,
                label = { Text("Yes") },
                modifier = Modifier.padding(end = 4.dp)
            )
            FilterChip(
                selected = value == false,
                onClick = onNo,
                label = { Text("No") }
            )
        }
    }
}