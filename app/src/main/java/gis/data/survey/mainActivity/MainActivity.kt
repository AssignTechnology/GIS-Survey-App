package gis.data.survey.mainActivity


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import gis.data.survey.component.internet.NetworkStatusBottomSheet
import gis.data.survey.component.internet.NetworkViewModel
import gis.data.survey.component.internet.NetworkViewModelFactory
import gis.data.survey.navigation.AppNavigation
import gis.data.survey.ui.theme.MyAppTheme
import gis.data.survey.ui.theme.ThemeViewModel
import gis.data.survey.ui.theme.ThemeViewModelFactory
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var smsReceiver: SmsReceiver
    private var onOtpReceived: (String) -> Unit = {}

    private val networkViewModel: NetworkViewModel by viewModels {
        NetworkViewModelFactory(applicationContext)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        smsReceiver = SmsReceiver { otp ->
            onOtpReceived(otp)
        }
        registerReceiver(smsReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION), RECEIVER_EXPORTED)

        setContent {
            NetworkStatusBottomSheet(viewModel = networkViewModel)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val themeViewModel: ThemeViewModel by viewModels(factoryProducer = { ThemeViewModelFactory(this) })
            MyAppTheme(themeMode = themeViewModel.themeMode.value) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        setOnOtpReceived = { callback ->
                            onOtpReceived = callback
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }
}

class SmsReceiver(private val onOtpReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages?.forEach { sms ->
            val body = sms.messageBody
            val otpRegex = "\\b\\d{6}\\b".toRegex()
            otpRegex.find(body)?.value?.let { otp ->
                onOtpReceived(otp)
            }
        }
    }
}


//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//    @RequiresApi(Build.VERSION_CODES.R)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            WindowCompat.setDecorFitsSystemWindows(window, false)
//            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(this))
//            MyAppTheme(themeMode = themeViewModel.themeMode.value) {
//                Surface(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .imePadding(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    val navController = rememberNavController()
//                    AppNavigation(navController)
//                }
//
//            }
//        }
//    }
//}

