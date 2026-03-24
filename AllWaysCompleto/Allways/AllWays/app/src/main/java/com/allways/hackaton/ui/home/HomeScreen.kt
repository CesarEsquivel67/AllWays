package com.allways.hackaton.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.allways.hackaton.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    placeId: String,
    viewModel: HomeViewModel = viewModel()
) {
    val place by viewModel.place.collectAsState()

    LaunchedEffect(placeId) { viewModel.loadPlace(placeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place?.name ?: "Loading...") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            place?.let { p ->

                Text(
                    text = p.description,
                    fontSize = 15.sp
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📍 Address",
                            fontWeight = FontWeight.Bold
                        )
                        Text(p.address)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🌳 How to find it",
                            fontWeight = FontWeight.Bold
                        )
                        Text(p.indications)
                    }
                }

                HorizontalDivider()

                Button(
                    onClick = { navController.navigate(Screen.Reviews.createRoute(placeId)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("See Reviews & Accessibility Info")
                }

                OutlinedButton(
                    onClick = { navController.navigate(Screen.AddInfo.createRoute(placeId)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Add Accessibility Info")
                }

            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}