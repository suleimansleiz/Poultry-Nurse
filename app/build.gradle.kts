plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pddc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pddc"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.navigation:navigation-fragment:2.8.7")
    implementation("androidx.navigation:navigation-ui:2.8.7")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.android.datatransport:transport-api:4.0.0")
    implementation("androidx.activity:activity:1.10.0")
    implementation("com.google.firebase:firebase-firestore:25.1.2")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation ("com.google.firebase:firebase-firestore:25.1.2")
    implementation ("com.google.firebase:firebase-firestore:25.1.2")
    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation (platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("androidx.emoji2:emoji2:1.5.0")
    implementation ("androidx.emoji2:emoji2-views:1.5.0")
    implementation ("androidx.emoji2:emoji2-views-helper:1.5.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

}