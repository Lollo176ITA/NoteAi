package com.lorenzocensi.noteai.data.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyStore @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val dataStore: DataStore<Preferences>
) {

    suspend fun getKey(): String? = dataStore.data.map { it[KEY] }.first()?.takeIf { it.isNotBlank() }

    suspend fun setKey(value: String) {
        dataStore.edit { it[KEY] = value.trim() }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(KEY) }
    }

    companion object {
        val KEY = stringPreferencesKey("nim_api_key")
    }
}

object TinkAead {
    fun getOrCreate(ctx: Context): Aead {
        AeadConfig.register()
        val handle = AndroidKeysetManager.Builder()
            .withSharedPref(ctx, "noteai_keyset", "noteai_keyset_pref")
            .withKeyTemplate(PredefinedAeadParameters.AES256_GCM)
            .withMasterKeyUri("android-keystore://noteai_master_key")
            .build()
            .keysetHandle
        return handle.getPrimitive(Aead::class.java)
    }
}
