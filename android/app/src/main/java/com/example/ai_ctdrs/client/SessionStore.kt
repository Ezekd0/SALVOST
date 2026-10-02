package com.example.ai_ctdrs.client

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.sessionData by preferencesDataStore("ctdrs_session")
class SessionStore(private val context: Context) {
    private val tokenKey = stringPreferencesKey("encrypted_token")
    private val urlKey = stringPreferencesKey("backend_url")
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return (store.getKey("ctdrs_session", null) as? SecretKey) ?: KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder("ctdrs_session", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
    suspend fun read(): Pair<String, String?> {
        val prefs = context.sessionData.data.first()
        val token = prefs[tokenKey]?.let { value -> runCatching {
            val bytes = Base64.decode(value, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(0, 12)))
            String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
        }.getOrNull() }
        return (prefs[urlKey] ?: "https://ai-ctdrs-backend.onrender.com/") to token
    }
    suspend fun saveToken(token: String?) {
        val encrypted = token?.let {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key()) }
            Base64.encodeToString(cipher.iv + cipher.doFinal(it.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
        }
        context.sessionData.edit { if (encrypted == null) it.remove(tokenKey) else it[tokenKey] = encrypted }
    }
    suspend fun saveUrl(url: String) { context.sessionData.edit { it[urlKey] = url; it.remove(tokenKey) } }
}
