[![](https://jitpack.io/v/com.gitee.tk_snake/ApkToFir.svg)](https://jitpack.io/#com.gitee.tk_snake/ApkToFir)
## 简介：
### ApkToFir是一个自动打包，然后上传到fir，钉钉自动通知并艾特测试人员的gradle插件

## 开发环境：
### gradle版本：gradle-7.2
### AGP版本：com.android.tools.build:gradle:7.1.3
### AS版本：Android Studio Chipmunk | 2021.2.1 Patch 1

## 使用步骤：
### 1 在项目根路径build.gradle中加入
```groovy
//buildscript是设置gradle的依赖和存储库
buildscript {
    repositories {
        //com.gitee.tk_snake:ApkToFir依赖库从jitpack下载
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        //com.cz.qx.gradle.fir标识的插件在这个依赖库中
        classpath "com.gitee.tk_snake:ApkToFir:v7.1"
    }
}
```

### 2 在app下的build.gradle中引入插件
```groovy
plugins {
    id 'com.cz.qx.gradle.fir'
}
```

### 3 在app下的build.gradle中添加扩展对象配置：
```groovy
/*
使用一键打包插件说明：
1 项目根路径创建ApkToFir.properties文件，注意编码方式一定是utf-8
2 把下面内容加到ApkToFir.properties中
    #项目名,钉钉机器人自定义关键字
    appName = 项目名

    #fir上传平台token
    firApiToken = 你的fir平台token
    #钉钉机器人token
    dingApiToken = 你的钉钉机器人token

    #钉钉下载通知配置
    msgTitle = 安卓发包了
    singleButtonTitle = 点击测试
    singleButtonUrl = 你的fir平台项目下载地址

    #钉钉@某人配置
    atMsg = 发包了[送花花][送花花]
    #钉钉群要艾特人的手机，多人使用逗号隔开
    atPhone = 139999999,13788888888

    # 修改日志 比如：测试包 \n\n 1 增加谷歌登录 \n\n 2 增加谷歌支付
    changeLog = 测试包 \n\n 1 增加谷歌登录 \n\n 2 增加谷歌支付

3 changeLog为更新日志，每次打包前备注修改内容
4 打开右上角gradle，找app->task->qxupload->sendMsgToDingDebug双击
 */
qxUpload {
    File firFile = rootProject.file('ApkToFir.properties')
    if (firFile && firFile.exists()) {
        firFile.withReader('utf-8') {
            def properties = new Properties()
            properties.load(it)
            iconFilePath = rootProject.projectDir.getAbsolutePath() + "/app/src/main/res/mipmap-xhdpi/ic_launcher.png"
            appName = properties.getProperty('appName')
            dingApiToken = properties.getProperty('dingApiToken')
            firApiToken = properties.getProperty('firApiToken')
            msgTitle = properties.getProperty('msgTitle')
            singleButtonTitle = properties.getProperty('singleButtonTitle')
            singleButtonUrl = properties.getProperty('singleButtonUrl')
            atMsg = properties.getProperty('atMsg')
            atPhone = properties.getProperty('atPhone')
            changeLog = properties.getProperty('changeLog')
            msgContent = "### ${appName}安卓v${project.android.defaultConfig.versionName}发布 \n\n 日志：\n\n ${changeLog} \n\n [下载地址](${singleButtonUrl})"
        }
    }
}
```

### 4 在钉钉群设置 -> 智能群助手 -> 添加自定义webhook机器人：
#### 简单配置机器人
##### 1 安全设置，自定义关键，填写应用名
##### 2 Webhook链接中可以获取到dingApiToken

### 5 最后同步下项目，执行task。两种方式，任选其一
#### 打开android studio右侧gradle面板找到app -> Tasks -> qxUpload -> sendMsgToDingAtDebug 双击
#### Terminal中输入gradle sendMsgToDingDebug

