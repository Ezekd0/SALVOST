package com.example.ai_ctdrs.client

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * User-controlled foreground monitor for this app's own runtime network counters.
 * It does not inspect packets, scan other apps, or claim device-wide antivirus coverage.
 */
class RuntimeMonitorService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var store: SessionStore
    private var monitorJob: kotlinx.coroutines.Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        store = SessionStore(this)
        if (intent?.action == ACTION_PAUSE) {
            scope.launch { store.setProtection(false) }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, notification("Security monitoring is starting"))
        if (monitorJob == null) monitorJob = scope.launch { monitor() }
        return START_STICKY
    }

    private suspend fun monitor() {
        val (url, token) = store.read()
        if (token == null) {
            store.setProtection(false)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }
        var previous = counters()
        var retryDelay = INTERVAL_MS
        store.updateMonitor("Active")
        updateNotification("Monitoring runtime security signals")

        while (true) {
            delay(retryDelay)
            val currentState = store.protectionState().first()
            if (!currentState.enabled) break
            if (!networkAvailable()) {
                store.updateMonitor("Connection temporarily unavailable")
                updateNotification("Monitoring active — connection temporarily unavailable")
                retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_MS)
                continue
            }
            val now = counters()
            val elapsedSeconds = ((now.timestamp - previous.timestamp) / 1000.0).coerceAtLeast(1.0)
            val telemetry = Telemetry(
                destination_port = 0,
                flow_duration = elapsedSeconds,
                packet_rate = ((now.rxPackets - previous.rxPackets).coerceAtLeast(0) + (now.txPackets - previous.txPackets).coerceAtLeast(0)) / elapsedSeconds,
                bytes_rate = ((now.rxBytes - previous.rxBytes).coerceAtLeast(0) + (now.txBytes - previous.txBytes).coerceAtLeast(0)) / elapsedSeconds,
                source_ip = "0.0.0.0",
                destination_ip = "0.0.0.0",
                protocol = "APP_RUNTIME",
                is_demo = false,
            )
            try {
                val event = api(url, token).analyze(telemetry)
                previous = now
                retryDelay = INTERVAL_MS
                store.updateMonitor("Active", System.currentTimeMillis())
                updateNotification("Monitoring runtime security signals")
                if (event.analysis?.classification != null && event.analysis.classification != "Benign") {
                    showThreatNotification(event.analysis.classification)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Keep the last successful counters so the next successful upload covers the gap.
                store.updateMonitor("Connection temporarily unavailable")
                updateNotification("Monitoring active — connection temporarily unavailable")
                retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_MS)
            }
        }
        store.updateMonitor("Paused")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun api(url: String, token: String): CtdrsApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).callTimeout(45, TimeUnit.SECONDS)
            .addInterceptor { chain -> chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer $token").build()) }
            .build()
        return Retrofit.Builder().baseUrl(url.trimEnd('/') + "/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(CtdrsApi::class.java)
    }

    private fun counters() = Counters(
        TrafficStats.getUidRxBytes(applicationInfo.uid).coerceAtLeast(0),
        TrafficStats.getUidTxBytes(applicationInfo.uid).coerceAtLeast(0),
        TrafficStats.getUidRxPackets(applicationInfo.uid).coerceAtLeast(0),
        TrafficStats.getUidTxPackets(applicationInfo.uid).coerceAtLeast(0),
        System.currentTimeMillis(),
    )
    private fun networkAvailable(): Boolean {
        val manager = getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        return manager.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }

    private fun notification(message: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(com.example.ai_ctdrs.R.mipmap.ic_launcher)
        .setContentTitle("AI-CTDRS runtime monitoring")
        .setContentText(message)
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .addAction(0, "Pause", android.app.PendingIntent.getService(this, 0, Intent(this, RuntimeMonitorService::class.java).setAction(ACTION_PAUSE), android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE))
        .build()
    private fun updateNotification(message: String) = getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification(message))
    private fun showThreatNotification(classification: String) = getSystemService(NotificationManager::class.java).notify(
        THREAT_NOTIFICATION_ID,
        NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(com.example.ai_ctdrs.R.mipmap.ic_launcher)
            .setContentTitle("AI-CTDRS security alert").setContentText("Runtime signal assessed as $classification")
            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build(),
    )
    override fun onCreate() {
        super.onCreate()
        getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL_ID, "Runtime security monitoring", NotificationManager.IMPORTANCE_LOW))
    }
    override fun onDestroy() { monitorJob?.cancel(); scope.cancel(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null

    private data class Counters(val rxBytes: Long, val txBytes: Long, val rxPackets: Long, val txPackets: Long, val timestamp: Long)
    companion object {
        const val ACTION_PAUSE = "com.example.ai_ctdrs.PAUSE_PROTECTION"
        private const val CHANNEL_ID = "runtime_monitoring"
        private const val NOTIFICATION_ID = 3001
        private const val THREAT_NOTIFICATION_ID = 3002
        private const val INTERVAL_MS = 15 * 60 * 1000L
        private const val MAX_RETRY_MS = 60 * 60 * 1000L
    }
}
