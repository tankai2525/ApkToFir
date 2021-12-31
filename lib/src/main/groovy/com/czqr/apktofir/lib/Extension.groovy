package com.czqr.apktofir.lib

import org.gradle.api.Project

/**
 * 扩展对象提供配置
 */
class Extension {

    //fir 配置参数
    String firApiToken
    String appName
    String changeLog
    String iconFilePath
    String filePath

    //ding 配置参数
    String dingApiToken
    String msgTitle
    String msgContent
    String singleButtonTitle
    String singleButtonUrl

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
