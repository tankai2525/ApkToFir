# ApkToFir
## 作用：执行一个task实现自动打包，上传到fir,最后通知钉钉。

## 使用步骤
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

### 3 在钉钉群设置 -> 智能群助手 -> 添加自定义webhook机器人
#### 简单配置机器
1 安全设置，自定义关键，填写应用名
2 Webhook连接中可以获取到dingApiToken

### 4 在app下的build.gradle中配置插件需要的参数
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

### 5 最后同步下项目，执行task两种方式，任选其一
#### 打开android studio 右侧gradle面板找到app -> Tasks -> qxUpload -> sendMsgToDingDebug 双击
#### Terminal中输入gradle sendMsgToDingDebug

