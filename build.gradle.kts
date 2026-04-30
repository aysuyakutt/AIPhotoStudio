plugins {
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.devtools.ksp") version  "2.0.20-1.0.25"  apply false
    alias(libs.plugins.google.gms.google.services) apply false
}