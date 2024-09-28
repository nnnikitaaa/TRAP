// Top-level build file where you can add configuration options common to all sub-projects/modules.
import java.util.Properties

// Define the extension property for the Mapkit API key
val mapkitApiKey: String by extra {
    loadMapkitApiKey()
}

// Function to read the API key from the local.properties file
fun loadMapkitApiKey(): String {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties.getProperty("MAPKIT_API_KEY", "")
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false
    id("androidx.room") version "2.6.1" apply false
}
