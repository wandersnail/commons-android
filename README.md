## 代码托管
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/common-full/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/common-full)
[![Download](https://api.bintray.com/packages/wandersnail/androidx/common-full/images/download.svg) ](https://bintray.com/wandersnail/androidx/common-full/_latestVersion)


## 使用

1. module的build.gradle中的添加依赖，自行修改为最新版本，需要哪个就依赖哪个，同步后通常就可以用了：
```
dependencies {
	...
	implementation 'cn.wandersnail:common-full:latestVersion'
}
```

2. 如果从jcenter下载失败。在project的build.gradle里的repositories添加内容，最好两个都加上，添加完再次同步即可。
```
allprojects {
	repositories {
		...
		mavenCentral()
		maven { url 'https://dl.bintray.com/wandersnail/androidx/' }
	}
}
```

## 按需要依赖单个库

- 如果不想全部依赖，可以依赖单个的，仓库如下：

1. [https://github.com/wandersnail/commons-basic](https://github.com/wandersnail/commons-basic)
1. [https://github.com/wandersnail/commons-observer](https://github.com/wandersnail/commons-observer)
2. [https://github.com/wandersnail/commons-poster](https://github.com/wandersnail/commons-poster)
3. [https://github.com/wandersnail/commons-utils](https://github.com/wandersnail/commons-utils)
4. [https://github.com/wandersnail/commons-helper](https://github.com/wandersnail/commons-helper)

## 功能

- SharedPreferences、Log、UI、图片、数学、文件操作、加密、网络、日期、数据库等工具类
- wifi、Toast、zip、存储帮助类
- 一些基类
