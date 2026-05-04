# NoteAi

App Android per organizzare note come knowledge workspace locale: note, progetti, ricerca e collegamenti manuali.

## Stack

- Kotlin 2.2.10 + Jetpack Compose
- Material 3 Expressive `1.5.0-alpha15`
- Room 2.8 per storage locale
- Hilt per dependency injection
- minSdk 26 - targetSdk 36

## Setup

1. Apri il progetto in Android Studio Narwhal o successivo.
2. Usa JDK 21 e Android SDK 36.
3. Sync Gradle.
4. Esegui il modulo `:appNext` su emulatore o device fisico.

Da terminale:

```sh
./gradlew :appNext:testDebugUnitTest :appNext:assembleDebug
```

## Come funziona

- La sezione **Note** mostra tutte le note recenti, con ricerca su titolo e corpo e filtro per progetto.
- La sezione **Progetti** permette di creare contenitori e avviare note gia associate al progetto.
- La sezione **Link** permette di collegare manualmente due note correlate.
- L'editor salva automaticamente dopo un breve debounce e mostra lo stato `Salvato`, `Salvataggio...` o `Errore`.

## Struttura

```
app-next/src/main/java/com/lorenzocensi/noteainext/
├── data/         # Room, DAO, repository
├── domain/       # modelli e use case puri
├── ui/           # Compose, ViewModel, navigazione e tema
└── di/           # Hilt modules
```

## Note

- Il vecchio modulo `:app` e' stato rimosso.
- L'MVP attuale non include AI, export/import, sync cloud, tag o graph canvas.
- Il database nuovo e' `noteai_next.db` e non migra i dati della vecchia app.
