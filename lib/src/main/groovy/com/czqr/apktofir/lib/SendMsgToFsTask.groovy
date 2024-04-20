package com.czqr.apktofir.lib

import com.android.build.gradle.api.BaseVariant
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * 飞书自动发送消息
 * 群开放-机器人文档：https://open.feishu.cn/document/client-docs/bot-v3/add-custom-bot
 */
class SendMsgToFsTask extends DefaultTask {

    @Input
    public BaseVariant variant;
    @Input
    public Project targetProject;

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

    void setup() {
        description "send msg to fs"
        group "kaiUpload"
    }

    @TaskAction
    def sendToDing() {
        Extension extension = Extension.getConfig(targetProject);
        def text = extension.fsContent
                .replace(extension.appName, extension.appName + "(" + variant.name + ")")
                .replaceAll("  ", "\n")
        def map =
                [
                    "msg_type": "interactive",
                    "card"  : [
                            "elements" : [
                                    [
                                            "tag": "div",
                                            "text" : [
                                                    "content": text,
                                                    "tag": "lark_md"
                                            ]
                                    ],
                                    [
                                            "tag": "action",
                                            "actions" : [
                                                    [
                                                    "tag": "button",
                                                    "text": [
                                                            "content": extension.singleButtonTitle,
                                                            "tag": "lark_md"
                                                    ],
                                                    "url": extension.singleButtonUrl,
                                                    "type": "default"
                                                    ]
                                            ]
                                    ]
                            ],
                    ],

                ]
        println map
        def requestData = JsonOutput.toJson(map)
        println requestData

        def requestUrl = 'https://open.feishu.cn/open-apis/bot/v2/hook/' + extension.fsApiToken;
        def url = new URL(requestUrl)
        def urlConnection = url.openConnection()

        urlConnection.setDoOutput(true)
        urlConnection.setRequestMethod("POST")
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
