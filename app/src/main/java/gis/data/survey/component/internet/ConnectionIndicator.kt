package gis.data.survey.component.internet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionIndicator(status: NetworkStatus) {
    Canvas(modifier = Modifier.size(50.dp)) {
        when (status) {
            is NetworkStatus.Connected -> drawCircle(color = Color.Green)
            is NetworkStatus.LowSpeed -> drawCircle(color = Color.Yellow)
            is NetworkStatus.Disconnected -> drawCircle(color = Color.Red)
        }
    }
}