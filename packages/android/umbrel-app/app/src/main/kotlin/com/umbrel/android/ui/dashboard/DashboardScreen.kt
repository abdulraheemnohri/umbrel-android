package com.umbrel.android.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrel.android.runtime.ResourceUsage
import com.umbrel.android.runtime.ResourceTracker

data class UmbrelApp(val id: String, val name: String, val status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(apps: List<UmbrelApp>, resourceTracker: ResourceTracker) {
    val usage = resourceTracker.usage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Umbrel Android", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            StatsCard(usage.value, Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Text("Running Apps", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(apps) { app ->
                    AppCard(app)
                }
            }
        }
    }
}

@Composable
fun StatsCard(usage: ResourceUsage, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Server Status", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("CPU", "${(usage.cpuUsage * 100).toInt()}%")
                StatItem("RAM", "${usage.ramUsageMb} MB")
                StatItem("Storage", "${usage.storageFreeGb} GB Free")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun AppCard(app: UmbrelApp) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        onClick = { /* Open App Settings */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(app.name.first().toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(app.name, fontWeight = FontWeight.Medium)
            Text(app.status, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
