## 推荐一款工具箱【蜗牛工具箱】

> 涵盖广，功能丰富。生活实用、效率办公、图片处理等等，还有隐藏的VIP功能，总之很多惊喜的功能。各大应用市场搜索【蜗牛工具箱】安装即可。

<div align="center">
    <img src="https://tucdn.wpon.cn/2023/12/21/3cea99987b074.png" width=150>
    <img src="https://tucdn.wpon.cn/2023/12/21/d46d124878a87.png" width=150>
    <img src="https://tucdn.wpon.cn/2023/12/21/191d4b5dca4d3.png" width=150>
    <img src="https://tucdn.wpon.cn/2023/12/21/cad80aeb12184.png" width=150>
</div>

**部分功能介绍**

- 【滚动字幕】超实用应援打call神器，输入文字内容使文字在屏幕中滚动显示；
- 【振动器】可自定义振动频率、时长，达到各种有意思的效果；
- 【测量仪器】手机当直尺、水平仪、指南针、分贝仪；
- 【文件加解密】可加密任意文件，可用于私密文件分享；
- 【金额转大写】将阿拉伯数字类型的金额转成中文大写；
- 【通信调试】BLE/SPP蓝牙、USB、TCP/UDP/MQTT通信调试；
- 【二维码】调用相机扫描或扫描图片识别二维码，支持解析WiFi二维码获取密码，输入文字生成相应的二维码；
- 【图片模糊处理】将图片进行高斯模糊处理，毛玻璃效果；
- 【黑白图片上色】黑白图片变彩色；
- 【成语词典】查询成语拼音、释义、出处、例句；
- 【图片拼接】支持长图、4宫格、9宫格拼接；
- 【自动点击】自动连点器，解放双手；
- 【图片加水印】图片上添加自定义水印；
- 【网页定时刷新】设定刷新后自动定时刷新网页；
- 【应用管理】查看本机安装的应用详细信息，并可提取安装包分享；
- 【BLE调试】低功耗蓝牙GATT通信调试，支持主从模式，可多设备同时连接，实时日志；
- 【SPP蓝牙调试】经典蓝牙Socket通信调试，支持自定义UUID，多设备同时连接，实时日志；
- 【USB调试】USB串口调试，兼容芯片多，实时日志；
……
已集成上百个小工具，持续更新中...

点击下方按钮或扫码下载【蜗牛工具箱】

[![](https://img.shields.io/badge/下载-%E8%9C%97%E7%89%9B%E5%B7%A5%E5%85%B7%E7%AE%B1-red.svg)](https://www.pgyer.com/8AN5OhVd)

<img src="https://www.pgyer.com/app/qrcode/8AN5OhVd" width=150>

----------------------------------------------

## 本库功能

- SharedPreferences、Log、UI、图片、数学、文件操作、加密、网络、日期、数据库等工具类
- wifi、Toast、zip、存储帮助类
- 一些基类

## 代码托管
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/commons-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.wandersnail/commons-android)
[![Release](https://jitpack.io/v/cn.wandersnail/commons-android.svg)](https://jitpack.io/#cn.wandersnail/commons-android)


## 使用
1. 因为使用了jdk8的一些特性，需要在module的build.gradle里添加如下配置：
```
//纯java的项目
android {
	compileOptions {
		sourceCompatibility JavaVersion.VERSION_1_8
		targetCompatibility JavaVersion.VERSION_1_8
	}
}

//有kotlin的项目还需要在project的build.gradle里添加
allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        kotlinOptions {
            jvmTarget = '1.8'
            apiVersion = '1.3'
            languageVersion = '1.3'
        }
    }
}

```
2. module的build.gradle中的添加依赖，自行修改为最新版本，需要哪个就依赖哪个，同步后通常就可以用了：
```
dependencies {
	...
	implementation 'cn.wandersnail:commons-android:latestVersion'
}
```

2. 在project的build.gradle里的repositories添加内容，最好两个都加上，添加完再次同步即可。
```
allprojects {
	repositories {
		...
		mavenCentral()
        maven { url 'https://jitpack.io' }
	}
}
```