//统一管理项目中的版本信息
object Versions {
    // Build Config
    const val minSDK = 23
    const val compileSDK = 30
    const val targetSDK = 30
    const val buildToolsVersion = "30.0.3"

    // App Version
    const val appVersionCode = 1
    const val appVersionName = "1.0.0"

    // Kotlin Version
    const val kotlin = "1.3.72"
    const val jvmTarget = "1.8"

    // Plugins Version
    const val androidGradlePlugins = "4.1.2"

    // Support Lib
    const val androidxCoreKtx = "1.2.0"
    const val androidxAppCompat = "1.1.0"
    const val material = "1.1.0"
    const val constraintLayout = "1.1.3"
    const val navigation = "2.2.2"

    // Test
    const val junit = "4.+"
    const val espresso = "3.2.0"
    const val androidxJunit = "1.1.1"

    // OneStepMessage
    const val oneStepMessage = "1.0.0"

}

//统一管理项目中的依赖库
object Deps {

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.androidxCoreKtx}"
    const val androidxAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val navigationFragment =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val junit = "junit:junit:${Versions.junit}"
    const val androidxJunit = "androidx.test.ext:junit:${Versions.androidxJunit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val oneStepMessage = "com.github.8kEatRadish:OneStepMessage:${Versions.oneStepMessage}"
}