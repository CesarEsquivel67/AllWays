package com.allways.hackaton.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    navController: NavController,
    placeId: String,
    viewModel: ReviewsViewModel = viewModel()
) {
    val reviews by viewModel.reviews.collectAsState()
    val averages by viewModel.averages.collectAsState()

    LaunchedEffect(placeId) { viewModel.load(placeId) }

    val featureLabels = mapOf(
        "hasBraille" to "Braille signage",
        "hasCaneBumps" to "Cane bumps",
        "hasAssistanceButton" to "Assistance button",
        "hasWheelchairRamp" to "Wheelchair ramp",
        "hasAccessibleParking" to "Accessible parking",
        "hasAccessibleBathroom" to "Accessible bathroom",
        "hasAudioSignals" to "Audio signals"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews & Accessibility") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Community Accessibility Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        featureLabels.forEach { (key, label) ->
                            val pct = averages[key] ?: 0f
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                LinearProgressIndicator(
                                    progress = { pct },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                )
                                Text(
                                    text = " ${(pct * 100).toInt()}%",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Individual Comments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(reviews) { review ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        if (review.photoUrl.isNotBlank()) {
                            AsyncImage(
                                model = review.photoUrl,
                                contentDescription = "Review photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = review.userName,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.upvote(review.infoId) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ThumbUp,
                                            contentDescription = "Upvote",
                                            tint = if (review.userHasUpvoted)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text("${review.upvotes}")
                                }
                            }
                            if (review.comment.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.comment,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}