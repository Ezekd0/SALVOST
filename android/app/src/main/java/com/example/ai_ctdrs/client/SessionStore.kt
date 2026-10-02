package com.example.ai_ctdrs.client

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.sessionData by preferencesDataStore("ctdrs_session")
class SessionStore(private val context: Context) {
    private val tokenKey = stringPreferencesKey("encrypted_token")
    private val urlKey = stringPreferencesKey("backend_url")
    private val protectionEnabledKey = booleanPreferencesKey("protection_enabled")
    private val monitorStatusKey = stringPreferencesKey("monitor_status")
    private val monitorActivityKey = longPreferencesKey("monitor_last_activity")

    data class ProtectionState(
        val enabled: Boolean = false,
        val status: String = "Paused",
        val lastActivityEpochMs: Long? = null,
    )

    companion object {
        const val PRODUCTION_URL = "https://salvost.onrender.com/"
        private val LOCAL_DEVELOPMENT_HOSTS = setOf(
            "10.0.2.2",
            "localhost",
            "127.0.0.1",
            "0.0.0.0",
            "::1",
            "[::1]",
        )

        fun sanitizeServerUrl(value: String?): String {
            val candidate = value?.trim()?.removeSuffix("/") ?: PRODUCTION_URL
            if (candidate.isBlank()) return PRODUCTION_URL

            return try {
                val uri = java.net.URI(candidate)
                val host = uri.host?.lowercase() ?: ""
                val scheme = uri.scheme?.lowercase() ?: ""
                val normalized = candidate.trimEnd('/')

                if (host in LOCAL_DEVELOPMENT_HOSTS || candidate.contains("10.0.2.2") || candidate.contains("localhost") || candidate.contains("127.0.0.1") || candidate.contains("0.0.0.0")) {
                    PRODUCTION_URL
                } else if (scheme == "http") {
                    PRODUCTION_URL
                } else if (scheme.isBlank()) {
                    PRODUCTION_URL
                } else if (normalized.equals("https://salvost.onrender.com", ignoreCase = true) || normalized.equals("https://ai-ctdrs-backend.onrender.com", ignoreCase = true)) {
                    PRODUCTION_URL
                } else {
                    candidate
                }
            } catch (_: Exception) {
                PRODUCTION_URL
            }
        }
    }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return (store.getKey("ctdrs_session", null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder("ctdrs_session", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    suspend fun read(): Pair<String, String?> {
        val prefs = context.sessionData.data.first()
        val rawUrl = prefs[urlKey]
        val token = prefs[tokenKey]?.let { value -> runCatching {
            val bytes = Base64.decode(value, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
            String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
        }.getOrNull() }
        val safeUrl = sanitizeServerUrl(rawUrl)
        if (rawUrl != safeUrl) {
            context.sessionData.edit { prefs ->
                prefs[urlKey] = safeUrl
                prefs.remove(tokenKey)
            }
            return safeUrl to null
        }
        return safeUrl to token
    }
    suspend fun saveToken(token: String?) {
        val encrypted = token?.let {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key()) }
            Base64.encodeToString(cipher.iv + cipher.doFinal(it.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
        }
        context.sessionData.edit { if (encrypted == null) it.remove(tokenKey) else it[tokenKey] = encrypted }
    }
    suspend fun saveUrl(url: String) {
        val safeUrl = sanitizeServerUrl(url)
        context.sessionData.edit {
            it[urlKey] = safeUrl
            it.remove(tokenKey)
        }
    }
    fun protectionState(): Flow<ProtectionState> = context.sessionData.data.map { prefs ->
        ProtectionState(
            enabled = prefs[protectionEnabledKey] ?: false,
            status = prefs[monitorStatusKey] ?: "Paused",
            lastActivityEpochMs = prefs[monitorActivityKey],
        )
    }
    suspend fun setProtection(enabled: Boolean, status: String = if (enabled) "Starting" else "Paused") {
        context.sessionData.edit {
            it[protectionEnabledKey] = enabled
            it[monitorStatusKey] = status
            if (!enabled) it.remove(monitorActivityKey)
        }
    }
    suspend fun updateMonitor(status: String, activityEpochMs: Long? = null) {
        context.sessionData.edit {
            it[monitorStatusKey] = status
            if (activityEpochMs != null) it[monitorActivityKey] = activityEpochMs
        }
    }
}
