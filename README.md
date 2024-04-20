[![](https://jitpack.io/v/com.gitee.tk_snake/ApkToFir.svg)](https://jitpack.io/#com.gitee.tk_snake/ApkToFir)
# ApkToFir

ApkToFir是一个自动打包apk，自动上传到fir.im，上传完后在钉钉或飞书群内通知并艾特指定成员下载测试的gradle插件


## 最新版开发环境
1. gradle版本：gradle-7.5.1-bin
2. AGP版本：com.android.tools.build:gradle:7.4.2
3. AS版本：Android Studio Giraffe | 2022.3.1 Patch 2

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
        classpath "com.gitee.tk_snake:ApkToFir:v7.4.3"
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

    firApiToken = 你的fir平台token
    dingApiToken = 你的钉钉机器人token
    fsApiToken = 你的飞书机器人token

    #钉钉下载通知配置
    msgTitle = 安卓发包了
    singleButtonTitle = 点击测试
    singleButtonUrl = 你的fir平台项目下载地址

    #钉钉@某人配置
    atMsg = 发包了[送花花][送花花]
    #钉钉群要艾特人的手机，多人使用逗号隔开
    atPhone = 139999999,13788888888

    # 修改日志
    changeLog = 1 处理问题1  2 处理问题1  两个空格相当于换行

3 changeLog为更新日志，每次打包前备注修改内容
4 打开右上角gradle，找app->task->qxupload->sendMsgToDingAtDebug双击
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
            fsContent = "**${appName}安卓v${project.android.defaultConfig.versionName}发布**\n日志：\n${changeLog}\n[下载地址](${singleButtonUrl}) <at id=all></at>"
        }
    }
}
```

### 4 配置钉钉机器人：
1. 在钉钉群设置 -> 智能群助手 -> 添加自定义webhook机器人
2. 安全设置，自定义关键，填写应用名
3. Webhook链接中可以获取到dingApiToken

### 5 执行打包task
- 打开android studio右侧gradle面板找到app -> Tasks -> qxUpload -> sendMsgToDingAtDebug 双击

