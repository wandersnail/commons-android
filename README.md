## 代码托管
[![](https://jitpack.io/v/wandersnail/commons-android.svg)](https://jitpack.io/#wandersnail/commons-android)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.wandersnail/commons-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wandersnail/commons-android)
[![Download](https://api.bintray.com/packages/wandersnail/android/commons-android/images/download.svg) ](https://bintray.com/wandersnail/android/commons-android/_latestVersion)


## 使用

1. module的build.gradle中的添加依赖，自行修改为最新版本，需要哪个就依赖哪个，同步后通常就可以用了：
```
dependencies {
	...
	implementation 'com.github.wandersnail:commons-android:latestVersion'
}
```

2. 如果从jcenter下载失败。在project的build.gradle里的repositories添加内容，最好两个都加上，有时jitpack会抽风，同步不下来。添加完再次同步即可。
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
		maven { url 'https://dl.bintray.com/wandersnail/android/' }
	}
}
```

## 按需要依赖单个库

- 如果不想全部依赖，可以依赖单个的，仓库如下：

1. https://github.com/wandersnail/commons-basic
2. https://github.com/wandersnail/commons-method-poster
3. https://github.com/wandersnail/commons-utils
4. https://github.com/wandersnail/commons-helper

## 功能

- SharedPreferences、Log、UI、图片、数学、文件操作、加密、网络、日期、数据库等工具类
- wifi、Toast、zip、存储帮助类
- 一些基类