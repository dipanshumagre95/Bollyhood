plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.app.bollyhood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.bollyhood"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
//    packagingOptions {
//        exclude("META-INF/DEPENDENCIES")
//        exclude("META-INF/LICENSE")
//        exclude("META-INF/LICENSE.txt")
//        exclude("META-INF/license.txt")
//        exclude("META-INF/NOTICE")
//        exclude("META-INF/NOTICE.txt")
//        exclude("META-INF/notice.txt")
//        exclude("META-INF/ASL2.0")
//        exclude("META-INF/*.kotlin_module")
//        exclude("META-INF/androidx.cardview_cardview.version")
//    }


    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }

}

dependencies {
    implementation("androidx.activity:activity:1.9.1")
    val lifecycle_version = "2.6.2"
    val glideVersion = "4.11.0"




    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //glide
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    //di
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.5.0")


    //imagepicker
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")


    //Dimen
    implementation("com.intuit.ssp:ssp-android:1.0.6")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //circleimageview
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //youtubeplayerview
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation("com.github.colourmoon:readmore-textview:v1.0.2")
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-messaging:24.0.0")

    implementation ("com.hbb20:ccp:2.5.0")

    implementation ("com.github.3llomi:RecordView:3.1.3")

    implementation ("com.github.JobGetabu:KaziChatVoicePlayer:1.0.0")
    implementation ("com.google.android.material:material:1.12.0")

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.razorpay:checkout:1.6.39")

    implementation ("com.google.android.exoplayer:exoplayer:2.18.2")
    implementation ("com.airbnb.android:lottie:6.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")


}