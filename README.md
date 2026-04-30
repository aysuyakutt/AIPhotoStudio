# AIPhotoStudio

AIPhotoStudio is an Android photo editing application built with Kotlin and Jetpack Compose. It combines Firebase-based authentication, local image persistence, and AI-assisted image transformation workflows so users can stylize, enhance, save, and manage their photos in a mobile-first experience.

## Overview

The project is organized around three main parts:

- `app/`: the Android client built with Jetpack Compose
- `functions/`: Firebase Functions used for prompt enrichment
- `hf-img2img-worker/`: a Cloudflare Worker that forwards img2img requests to Hugging Face Inference

This setup makes the app more than a simple UI demo. It includes authentication flows, local storage, AI image processing, profile management, and supporting backend services.

## Features

- Firebase authentication with Google sign-in, email/password, and guest access
- Splash, login, sign up, forgot password, home, and profile flows
- Predefined AI effects such as Anime, Arcane, Cartoon, Canny Lines, Depth 3D, and OpenPose-inspired transformations
- Prompt-based editing flow for custom image instructions
- AI-generated outputs returned as images and previewed inside the app
- Local persistence with Room for saved image records
- Per-user image organization using device storage
- Save generated images to the device gallery
- Profile screen with user information and saved photo history
- Dependency injection with Hilt and a layered project structure

## Architecture

The Android app follows a practical layered structure:

- `presentation/`: Compose screens, navigation, and view models
- `data/repository/`: authentication, AI, and image data access
- `data/db/`: Room database, DAO, and entities
- `di/`: Hilt modules for app-wide dependency management

The app currently uses Firebase AI with Gemini image generation capabilities in the client, while the server-side pieces support prompt enhancement and external inference workflows.

## Tech Stack

- Kotlin
- Jetpack Compose
- Android Studio
- Material 3
- Hilt
- Room
- Firebase Authentication
- Firebase Firestore
- Firebase AI
- Google Sign-In and Android Credentials API
- Retrofit, OkHttp, Moshi, and Gson
- Firebase Functions with TypeScript
- Cloudflare Workers
- Hugging Face Inference API

## Project Structure

```text
AIPhotoStudio/
|- app/
|- functions/
|- hf-img2img-worker/
|- firebase.json
|- build.gradle.kts
|- settings.gradle.kts
```

## Getting Started

### Android App

1. Clone the repository.
2. Open the project in Android Studio.
3. Add your Firebase configuration file to `app/google-services.json`.
4. Create or update `local.properties` for local-only secrets and machine-specific configuration.
5. Sync Gradle and run the `app` module on an emulator or physical device.

### Firebase Functions

From the `functions/` directory:

```bash
npm install
npm run build
```

To provide the Gemini key for Functions, configure it through Firebase config or environment variables instead of hardcoding it:

```bash
firebase functions:config:set gemini.key=YOUR_KEY
```

### Cloudflare Worker

From the `hf-img2img-worker/` directory:

```bash
npm install
npm run dev
```

The worker expects an `HF_TOKEN` secret in its runtime environment.

## Security Notes

- Do not commit API keys, local machine configs, or secret tokens.
- Keep `local.properties` local only.
- Treat `google-services.json` carefully when publishing the project publicly.
- Prefer environment variables, Firebase config, or platform secret managers for production credentials.

## What I Practiced In This Project

- Building a full Android app with Jetpack Compose instead of XML
- Designing authentication and navigation flows
- Integrating AI-powered image editing into a mobile UX
- Managing local persistence with Room
- Structuring a multi-part project that includes mobile, serverless, and worker components

## Roadmap

- Add side-by-side before/after comparison for edited photos
- Add stronger loading, retry, and error states for AI requests
- Add text-to-image generation
- Add more AI styles and model providers
- Improve theme support with dark and light modes
- Add localization and multi-language support
- Add tests for core repository and view model logic

---

## Türkçe

AIPhotoStudio, Kotlin ve Jetpack Compose ile geliştirilmiş bir Android fotoğraf düzenleme uygulamasıdır. Firebase tabanlı kimlik doğrulama, yerel görsel saklama ve yapay zeka destekli görsel dönüştürme akışlarını bir araya getirerek kullanıcıların fotoğraflarını düzenlemesini, stilize etmesini, kaydetmesini ve yönetmesini sağlar.

## Genel Bakış

Proje üç ana parçadan oluşur:

- `app/`: Jetpack Compose ile geliştirilen Android istemcisi
- `functions/`: prompt zenginleştirme için kullanılan Firebase Functions katmanı
- `hf-img2img-worker/`: Hugging Face Inference isteklerini yöneten Cloudflare Worker

Bu yapı projeyi yalnızca bir arayüz demosu olmaktan çıkarır. Uygulama; kimlik doğrulama, yerel saklama, yapay zeka destekli görsel işleme, profil yönetimi ve yardımcı backend servislerini birlikte içerir.

## Özellikler

- Firebase ile Google, e-posta/şifre ve misafir girişi desteği
- Splash, login, kayıt olma, şifremi unuttum, ana ekran ve profil akışları
- Anime, Arcane, Cartoon, Canny Lines, Depth 3D ve OpenPose benzeri hazır efektler
- Özel komutlarla prompt tabanlı görsel düzenleme
- Yapay zeka tarafından üretilen çıktıları uygulama içinde önizleme
- Room ile kaydedilen görseller için yerel veri saklama
- Kullanıcı bazlı cihaz içi görsel organizasyonu
- Düzenlenen görselleri cihaz galerisine kaydetme
- Kullanıcı bilgileri ve geçmiş görseller için profil ekranı
- Hilt ile dependency injection ve katmanlı yapı

## Mimari

Android uygulaması pratik bir katmanlı yapı izler:

- `presentation/`: Compose ekranları, navigasyon ve view model katmanı
- `data/repository/`: kimlik doğrulama, yapay zeka ve görsel veri erişimi
- `data/db/`: Room veritabanı, DAO ve entity yapıları
- `di/`: uygulama geneli bağımlılık yönetimi için Hilt modülleri

Uygulama istemci tarafında Firebase AI ve Gemini tabanlı görsel üretim yeteneklerini kullanırken, sunucu tarafındaki parçalar prompt iyileştirme ve harici inference akışlarını destekler.

## Kullanılan Teknolojiler

- Kotlin
- Jetpack Compose
- Android Studio
- Material 3
- Hilt
- Room
- Firebase Authentication
- Firebase Firestore
- Firebase AI
- Google Sign-In ve Android Credentials API
- Retrofit, OkHttp, Moshi ve Gson
- TypeScript ile Firebase Functions
- Cloudflare Workers
- Hugging Face Inference API

## Proje Yapısı

```text
AIPhotoStudio/
|- app/
|- functions/
|- hf-img2img-worker/
|- firebase.json
|- build.gradle.kts
|- settings.gradle.kts
```

## Kurulum

### Android Uygulaması

1. Repoyu klonla.
2. Projeyi Android Studio ile aç.
3. `app/google-services.json` dosyasını kendi Firebase projen ile ekle.
4. Yerel gizli bilgiler ve makineye özel ayarlar için `local.properties` dosyasını oluştur ya da güncelle.
5. Gradle senkronizasyonunu tamamlayıp `app` modülünü emülatör veya gerçek cihazda çalıştır.

### Firebase Functions

`functions/` klasöründe:

```bash
npm install
npm run build
```

Functions için Gemini anahtarını kod içine gömmek yerine Firebase config veya environment variable ile ver:

```bash
firebase functions:config:set gemini.key=YOUR_KEY
```

### Cloudflare Worker

`hf-img2img-worker/` klasöründe:

```bash
npm install
npm run dev
```

Worker tarafında çalışma ortamında `HF_TOKEN` secret'ı beklenir.

## Güvenlik Notları

- API anahtarlarını, local makine ayarlarını ve tokenları repoya commit etme.
- `local.properties` dosyasını sadece yerel kullanım için tut.
- Projeyi herkese açık paylaşırken `google-services.json` dosyasını dikkatli yönet.
- Production için secret manager, environment variable veya Firebase config kullan.

## Bu Projede Geliştirdiğim Konular

- XML yerine Jetpack Compose ile uçtan uca Android arayüz geliştirme
- Kimlik doğrulama ve navigasyon akışları tasarlama
- Yapay zeka destekli görsel düzenlemeyi mobil deneyime entegre etme
- Room ile yerel veri saklama yapısı kurma
- Mobil, serverless ve worker bileşenlerinden oluşan çok parçalı bir proje düzeni yönetme

## Yol Haritası

- Düzenlenen fotoğraflar için önce/sonra karşılaştırma ekranı eklemek
- Yapay zeka istekleri için daha güçlü loading, retry ve hata durumları eklemek
- Metinden görsel üretme özelliği eklemek
- Daha fazla yapay zeka modeli ve sağlayıcısı eklemek
- Karanlık ve aydınlık tema desteğini geliştirmek
- Çoklu dil ve yerelleştirme desteği eklemek
- Repository ve view model katmanları için testler yazmak
