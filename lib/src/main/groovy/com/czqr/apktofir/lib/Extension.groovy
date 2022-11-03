package com.czqr.apktofir.lib

import org.gradle.api.Project

/**
 * 扩展对象提供配置
 */
class Extension {

    //fir 上传配置参数
    String firApiToken
    String appName
    String changeLog
    String iconFilePath
    String filePath

    //ding 下载配置参数
    String dingApiToken
    String msgTitle
    String msgContent
    String singleButtonTitle
    String singleButtonUrl

    //ding @通知某人配置
    String atMsg
    //手机号码，多人使用英文逗号分隔
    String atPhone

    Extension(Project project) {
    }

    static Extension getConfig(Project project) {
        println "Extension getConfig"
        Extension config = project.getExtensions().findByType(Extension.class);
        if (config == null) {
            config = new Extension();
        }
        return config;
    }
}
