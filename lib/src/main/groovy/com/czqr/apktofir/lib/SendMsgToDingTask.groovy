package com.czqr.apktofir.lib

import com.android.build.gradle.api.BaseVariant
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * 发送消息给钉钉
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

    @TaskAction
    def sendToDing() {
        Extension extension = Extension.getConfig(targetProject);

//        def map = [
//                "msgtype": "markdown",
//                "markdown": [
//                        "title":"GoNovel",
//                        "text": "#### GoNovel @15580403927 \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
//                ],
//                "at": [
//                        "atMobiles": [
//                                "15580403927"
//                        ],
//                        "isAtAll": false
//                ]
//        ]


        def map = [actionCard: [title         : extension.msgTitle,
                                text          : extension.msgContent,
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
