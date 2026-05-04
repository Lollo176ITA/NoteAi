package com.lorenzocensi.noteai.data.remote

import java.io.IOException

class MissingApiKeyException : IOException("Chiave NVIDIA NIM non configurata. Aprire Impostazioni e inserirla.")
