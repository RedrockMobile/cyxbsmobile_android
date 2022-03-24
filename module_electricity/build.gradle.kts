import versions.defaultNet
import versions.gson
import versions.retrofit

/*
* 这里只添加确认模块独用库，添加请之前全局搜索，是否已经依赖
* 公用库请不要添加到这里
* */
plugins {
    id("com.redrock.cyxbs")
}
dependencies {
    implementation(project(":module_electricity:api_electricity"))
    implementation(project(":lib_account:api_account"))
    defaultNet()
    implementation("cn.aigestudio.wheelpicker:WheelPicker:1.1.3")
}
