package com.lorenzocensi.noteai.data.remote

import com.lorenzocensi.noteai.BuildConfig
import com.lorenzocensi.noteai.data.security.ApiKeyStore
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NimClientFactory @Inject constructor(
    private val apiKeyStore: ApiKeyStore
) {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

    fun create(): NimApi {
        val auth = AuthInterceptor(apiKeyStore)
        val rateLimit = RateLimitInterceptor(maxRequestsPerMinute = 30)
        val retry = RetryInterceptor(maxRetries = 3)
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(rateLimit)
            .addInterceptor(retry)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(NimApi.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(NimApi::class.java)
    }
}

internal class AuthInterceptor(private val store: ApiKeyStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val key = try {
            runBlocking { store.getKey() }
        } catch (io: IOException) {
            throw io
        } catch (t: Throwable) {
            throw IOException("Lettura chiave NIM fallita", t)
        } ?: throw MissingApiKeyException()
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(req)
    }
}

internal class RateLimitInterceptor(maxRequestsPerMinute: Int) : Interceptor {
    private val intervalMs = 60_000L / maxRequestsPerMinute
    private val gate = Semaphore(1)
    @Volatile private var lastRequestAt = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
        gate.acquire()
        try {
            val now = System.currentTimeMillis()
            val wait = lastRequestAt + intervalMs - now
            if (wait > 0) Thread.sleep(wait)
            lastRequestAt = System.currentTimeMillis()
        } finally {
            gate.release()
        }
        return chain.proceed(chain.request())
    }
}

internal class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var lastResponse: Response? = null
        while (attempt <= maxRetries) {
            val response = chain.proceed(chain.request())
            if (response.code == 429 || response.code in 500..599) {
                lastResponse?.close()
                lastResponse = response
                val retryAfter = response.header("Retry-After")?.toLongOrNull()
                val backoffSec = retryAfter ?: minOf(30L, (1L shl attempt) * 2)
                response.close()
                if (attempt == maxRetries) break
                Thread.sleep(backoffSec * 1000)
                attempt++
                continue
            }
            return response
        }
        return lastResponse ?: chain.proceed(chain.request())
    }
}
