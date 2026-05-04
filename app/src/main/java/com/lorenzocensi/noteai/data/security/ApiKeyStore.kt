package com.lorenzocensi.noteai.data.security

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyStore @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val dataStore: DataStore<Preferences>,
    private val aead: Aead
) {

    suspend fun getKey(): String? {
        val raw = dataStore.data.map { it[KEY] }.first() ?: return null
        if (raw.isBlank()) return null
        val cipherBytes = Base64.decode(raw, Base64.NO_WRAP)
        val plain = aead.decrypt(cipherBytes, ASSOCIATED_DATA)
        return String(plain, Charsets.UTF_8).takeIf { it.isNotBlank() }
    }

    suspend fun setKey(value: String) {
        val plain = value.trim().toByteArray(Charsets.UTF_8)
        val cipher = aead.encrypt(plain, ASSOCIATED_DATA)
        val encoded = Base64.encodeToString(cipher, Base64.NO_WRAP)
        dataStore.edit { it[KEY] = encoded }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(KEY) }
    }

    companion object {
        val KEY = stringPreferencesKey("nim_api_key")
        private val ASSOCIATED_DATA = "noteai_api_key".toByteArray(Charsets.UTF_8)
    }
}

object TinkAead {
    fun getOrCreate(ctx: Context): Aead {
        AeadConfig.register()
        val handle = AndroidKeysetManager.Builder()
            .withSharedPref(ctx, "noteai_keyset", "noteai_keyset_pref")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://noteai_master_key")
            .build()
            .keysetHandle
        return handle.getPrimitive(Aead::class.java)
    }
}
