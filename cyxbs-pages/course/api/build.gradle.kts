plugins {
  id("manager.lib")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.cyxbsComponents.utils)
      implementation(projects.cyxbsComponents.config)
      implementation(projects.cyxbsPages.affair.api)
    }
    androidMain.dependencies {
      implementation(libs.androidx.appcompat)
      implementation(libs.rxjava)
    }
  }
}
