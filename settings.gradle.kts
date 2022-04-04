val isFullModuleDebug:String by settings
val ignoreModuleMode:String by settings
val ignoreModule:String by settings

pluginManagement {
    includeBuild("build_logic")
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }

        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    components.all {
        println(id)
    }
    repositories {
        // 以下两行代码相当于有了 google() jcenter() mavenCentral()，使用国内的阿里镜像
        maven { url = uri("$rootDir/maven") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://jitpack.io") }

        google()
        mavenCentral()
    }
}

//includeBuild("build_logic")

rootDir.listFiles()!!
    //根路径下搜寻前缀为lib_和module_的文件夹
    .filter {
        it.isDirectory && "(lib_.+)|(module_.+)".toRegex().matches(it.name)
    }
    .filter {
        !isFullModuleDebug.toBoolean() || it.name == "module_app"
    }
    .onEach {
        include(":${it.name}")
    }
    //搜寻第二层路径
    .flatMap {
        it.listFiles()!!.toList()
    }
    //搜索前缀为api_的文件夹
    .filter {
        it.isDirectory && "api_.+".toRegex().matches(it.name)
    }
    .onEach {
        include(":${it.parentFile.name}:${it.name}")
    }