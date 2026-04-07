package gis.data.survey.component.internet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStatusBottomSheet(viewModel: NetworkViewModel) {
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }
    )
    val snackbarHostState = remember { SnackbarHostState() }

    val shouldShowSheet by remember { derivedStateOf { networkStatus is NetworkStatus.Disconnected } }

    LaunchedEffect(networkStatus) {
        if (shouldShowSheet) {
            coroutineScope.launch { sheetState.show() }
        } else {
            coroutineScope.launch { sheetState.hide() }
        }
    }

    if (shouldShowSheet) {
        ModalBottomSheet(
            onDismissRequest = { /* Non-dismissible */ },
            sheetState = sheetState,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No Internet",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please check your connection. This will auto-hide when you're back online.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}