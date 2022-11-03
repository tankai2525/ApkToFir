package com.czqr.apktofir.lib

import com.android.build.gradle.api.BaseVariant
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * 钉钉自动发送消息并@某人
 * 群开放-机器人文档：https://open.dingtalk.com/document/group/custom-robot-access
 */
class SendMsgToDingAtTask extends DefaultTask {

    @Input
    public BaseVariant variant;
    @Input
    public Project targetProject;

    void setup() {
        description "send msg to ding at"
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
        Extension extension = Extension.getConfig(targetProject)
        def content = extension.appName + extension.atMsg
        def phones = extension.atPhone.split(",")
        for (p in phones) {
            content += "@" + p
        }
        def map = [
                "msgtype": "text",
                "text"   : ["content": content],
                "at"     : [
                        "atMobiles": phones,
                        "isAtAll"  : false
                ]
        ]
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
