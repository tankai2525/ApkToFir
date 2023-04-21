package com.czqr.apktofir.lib

import com.android.build.gradle.api.BaseVariant
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * 钉钉自动发送消息
 * 群开放-机器人文档：https://open.dingtalk.com/document/group/custom-robot-access
 */
class SendMsgToDingTask extends DefaultTask {

    @Input
    public BaseVariant variant;
    @Input
    public Project targetProject;

    void setup() {
        description "send msg to ding"
        group "qxUpload"
    }

    BaseVariant getVariant() {
        return variant
    }

    void setVariant(BaseVariant variant) {
        this.variant = variant
    }

    Project getTargetProject() {
        return targetProject
    }

    void setTargetProject(Project targetProject) {
        this.targetProject = targetProject
    }

    @TaskAction
    def sendToDing() {
        Extension extension = Extension.getConfig(targetProject);
        def text = extension.msgContent
                .replace(extension.appName, extension.appName + "(" + variant.name + ")")
                .replaceAll("  ", " \n\n ")
        def map = [actionCard: [title         : extension.msgTitle,
                                text          : text,
                                hideAvatar    : 0,
                                btnOrientation: 0,
                                singleTitle   : extension.singleButtonTitle,
                                singleURL     : extension.singleButtonUrl
                    ],
                   "msgtype": "actionCard"]

        def requestData = JsonOutput.toJson(map)
        println requestData
        def requestUrl = 'https://oapi.dingtalk.com/robot/send?access_token=' + extension.dingApiToken;
        def url = new URL(requestUrl)
        def urlConnection = url.openConnection()

        urlConnection.setDoOutput(true)
        urlConnection.setRequestMethod("POST")
        urlConnection.setRequestProperty("Authorization", "Basic")
        urlConnection.setRequestProperty("Content-Type", "application/json")

        def httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()))
        httpRequestBodyWriter.write(requestData)
        httpRequestBodyWriter.close()

        def httpResponseScanner = new Scanner(urlConnection.getInputStream())
        while (httpResponseScanner.hasNextLine()) {
            println(httpResponseScanner.nextLine())
        }
        httpResponseScanner.close()
    }
}
