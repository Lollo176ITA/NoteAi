# NoteAi

App Android per prendere note in stile post-it, organizzarle in progetti, e farsi suggerire automaticamente collegamenti tra note dall'AI.

## Stack

- Kotlin 2.2.10 + Jetpack Compose
- **Material 3 Expressive `1.5.0-alpha15`** (alpha)
- Room 2.8 per storage locale
- WorkManager per la ricerca AI in background
- Retrofit + OkHttp per chiamare NVIDIA NIM (Nemotron 3 Super)
- Hilt per la dependency injection
- DataStore + Tink per cifrare la chiave API
- minSdk 26 — targetSdk 36

## Setup

1. Apri il progetto in **Android Studio Narwhal** (o successivo) — serve AGP 9.1, JDK 21.
2. Sync Gradle (la prima volta scarica un po' di MB).
3. Procurati una chiave NVIDIA NIM gratuita: <https://build.nvidia.com/settings/api-keys> (40 req/min sul free tier, 1000 crediti iniziali).
4. Esegui l'app su un emulatore o device fisico (API 26+).
5. Apri **Impostazioni** dall'icona ingranaggio in alto a destra, incolla la chiave `nvapi-...` e premi **Verifica chiave**. Se vedi "Chiave valida" sei a posto.

## Come funziona

- Crei un **progetto** (es. "Idee romanzo", "Spese casa", "Ricette").
- Dentro il progetto crei **note** post-it: titolo + corpo libero.
- Quando salvi una nota, dopo 10 secondi di inattivita un job in background invia tutte le note del progetto a Nemotron 3 Super, che restituisce in JSON i collegamenti piu rilevanti.
- I collegamenti appaiono in fondo all'editor della nota come chip cliccabili: tap = vai alla nota collegata.
- Tutto e' salvato **solo localmente** sul tuo device. La chiave API e' cifrata con Tink + Android Keystore. Nessun account, nessun cloud.

## Struttura

```
app/src/main/java/com/lorenzocensi/noteai/
├── data/         # Room, Retrofit/NIM, ApiKeyStore
├── domain/       # modelli e use case
├── work/         # ConnectionDiscoveryWorker
├── ui/           # Compose: theme, schermate, PostItCard
└── di/           # Hilt modules
```

## Limiti noti

- Il batch al LLM scala bene fino a ~3000 note nel progetto. Oltre, il prompt diventa pesante: si potra passare a embeddings in futuro.
- Material 3 Expressive e' in alpha: alcune API potrebbero cambiare al prossimo bump di versione.
- `datastore-tink` 1.3.0-alpha07 e' anch'esso alpha ma e' la libreria ufficiale AndroidX (sostituisce `EncryptedSharedPreferences` deprecata nel 2026).
