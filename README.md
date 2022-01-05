[![](https://jitpack.io/v/com.gitee.tk_snake/ApkToFir.svg)](https://jitpack.io/#com.gitee.tk_snake/ApkToFir)
## 简介：
### ApkToFir是一个自动打包，然后上传到fir，最后通知钉钉的gradle插件

## 开发环境：
### gradle版本：gradle-6.7.1
### AGP版本：com.android.tools.build:gradle:4.2.1(这里4.2.0+都可以)
### AS版本：Android Studio Arctic Fox | 2020.3.1 Patch 3

## 注意：
### 由于AGP7.0API变化较大，暂不支持

## 使用步骤：
### 1 在app下的build.gradle中引入插件
```groovy
apply plugin: 'com.cz.qx.gradle.fir'
//或者
plugins {
    id 'com.cz.qx.gradle.fir'
}
```

### 2 在项目根路径build.gradle中加入
```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:4.2.1'
        classpath "com.gitee.tk_snake:ApkToFir:v5.0"
    }
}
```

### 3 在钉钉群设置 -> 智能群助手 -> 添加自定义webhook机器人：
#### 简单配置机器人
##### 1 安全设置，自定义关键，填写应用名
##### 2 Webhook链接中可以获取到dingApiToken

### 4 在app下的build.gradle中配置插件需要的参数：
```groovy
qxUpload {
    //fir需要的参数
    iconFilePath = rootProject.projectDir.getAbsolutePath() + "/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png"
    appName = '应用名'
    changeLog = 'fir提交日志'
    firApiToken = 'firApiToken'

    //钉钉需要的参数
    dingApiToken = 'dingApiToken'
    singleButtonTitle = '按钮标题'
    singleButtonUrl = 'fir短链接'
    msgTitle = '钉钉会话窗标题'
    msgContent = appName + "钉钉消息内容" //钉钉文档说关键字必现出现在消息内容中才会正常通知，所以这里拼接应用名

}
```
#### 也可把部分配置参数放到ApkToFir.properties(注意要utf-8)文件，避免频繁修改build文件：
##### ApkToFir.properties 内容：
```text
appName = appName
#每次发包修改changeLog日志
changeLog = 测试包 \n\n 1
firApiToken = 你的firApiToken
dingApiToken = 你的dingApiToken
msgTitle = 安卓发包了
singleButtonTitle = 点击测试
singleButtonUrl = fir短链接
```
##### 修改app下的build.gradle中配置插件配置
```groovy
qxUpload {
    File firFile = rootProject.file('ApkToFir.properties')
    if (firFile && firFile.exists()) {
        firFile.withInputStream {
            def properties = new Properties()
            properties.load(it)
            iconFilePath = rootProject.projectDir.getAbsolutePath() + "/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png"
            appName = properties.getProperty('appName')
            changeLog = new String(properties.getProperty('changeLog').getBytes("ISO8859-1"), "utf-8")
            firApiToken = properties.getProperty('firApiToken')
            dingApiToken = properties.getProperty('dingApiToken')
            singleButtonTitle = new String(properties.getProperty('singleButtonTitle').getBytes("ISO8859-1"), "utf-8")
            singleButtonUrl = properties.getProperty('singleButtonUrl')
            msgTitle = new String(properties.getProperty('msgTitle').getBytes("ISO8859-1"), "utf-8")
            //msgContent支持Markdown格式
            msgContent = "### ${appName}安卓v${project.android.defaultConfig.versionName}发布 \n\n 日志：\n\n ${changeLog} \n\n [下载地址](${singleButtonUrl})"

        }
    }
}
```

### 5 最后同步下项目，执行task。两种方式，任选其一
#### 打开android studio右侧gradle面板找到app -> Tasks -> qxUpload -> sendMsgToDingDebug 双击
#### Terminal中输入gradle sendMsgToDingDebug

