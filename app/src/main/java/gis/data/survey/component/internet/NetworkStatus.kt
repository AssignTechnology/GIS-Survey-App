package gis.data.survey.component.internet

sealed interface NetworkStatus {
    object Connected : NetworkStatus
    object Disconnected : NetworkStatus
    object LowSpeed : NetworkStatus // For low-speed connections
}
