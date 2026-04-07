package gis.data.survey.uiScreen.dashboard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import gis.data.survey.viewModel.ServiceViewModel

@Composable
fun Services(
    navController: NavHostController,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    WebViewScreen("https://assigntechnosoft.com/Home/Contact")
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    var isLoading by remember { mutableStateOf(true) } // Track loading state

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Configure WebView settings
                    settings.javaScriptEnabled = true // Enable JavaScript (use cautiously)
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false

                    // Set WebViewClient to handle page navigation and loading
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            url: String
                        ): Boolean {
                            view.loadUrl(url) // Load URLs within WebView
                            return true
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true // Show loader when page starts loading
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false // Hide loader when page finishes loading
                            super.onPageFinished(view, url)
                        }
                    }
                    // Load the URL
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize() // Responsive layout
        )

        // Show loading indicator while isLoading is true
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center) // Center the loader
                    .size(50.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}