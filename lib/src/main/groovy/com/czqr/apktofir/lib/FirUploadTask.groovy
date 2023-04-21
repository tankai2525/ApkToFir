package com.czqr.apktofir.lib

import com.android.build.gradle.api.BaseVariant
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.util.TextUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * apk上传到fir的task
 */
class FirUploadTask extends DefaultTask {

    @Input
    public BaseVariant variant;
    @Input
    public Project targetProject;

    void setup() {
        description "upload apk to fir"
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
    def uploadToFir() {
        variant.outputs.all {
            def apkFile = it.outputFile
            if (apkFile == null || !apkFile.exists()) {
                throw new GradleException("${apkFile} is not existed!")
            }else {
                println(apkFile.getAbsoluteFile())
            }

            Extension extension = Extension.getConfig(targetProject)
            extension.filePath = apkFile.getAbsoluteFile().toString()

            println("##########################################################################################")
            println("#")
            println("#     applicationId :" + variant.applicationId)
            println("#     uploadFileName:" + apkFile.getAbsoluteFile())
            println("#     versionName   :" + targetProject.android.defaultConfig.versionName)
            println("#     versionCode   :" + String.valueOf(targetProject.android.defaultConfig.versionCode))
            println("#     appName       :" + (TextUtils.isEmpty(extension.appName) ? variant.name.capitalize().replace("Release", "") : (extension.appName)))
            println("#     changeLog     :" + (extension.changeLog))
            println("#")
            println("##########################################################################################")

            //获取上传凭证
            def keyTokenRequestData = JsonOutput.toJson([type: 'android', bundle_id: variant.applicationId, api_token: extension.firApiToken])
            println keyTokenRequestData
            def url = new URL("http://api.bq04.com/apps")
            def urlConnection = url.openConnection()

            urlConnection.setDoOutput(true)
            urlConnection.setRequestMethod("POST")
            urlConnection.setRequestProperty("Content-Type", "application/json")

            def httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()))
            httpRequestBodyWriter.write(keyTokenRequestData)
            httpRequestBodyWriter.close()

            def httpResponseScanner = new Scanner(urlConnection.getInputStream())
            while (httpResponseScanner.hasNextLine()) {
                def firResponseJson = new JsonSlurper().parseText(httpResponseScanner.nextLine());
                println("获取上传凭证成功:")
                println(firResponseJson)

                //上传图片
                def httpIcon = new HTTPBuilder(firResponseJson.cert.icon.upload_url)
                httpIcon.request(Method.POST) { request ->
                    headers.'Accept' = 'application/json'
                    def iconEntityBuilder = MultipartEntityBuilder.create();
                    def iconFile = new File(extension.iconFilePath);
                    iconEntityBuilder.addBinaryBody('file', iconFile, ContentType.DEFAULT_BINARY, iconFile.getAbsoluteFile().toString())
                    iconEntityBuilder.addPart('key', new StringBody(firResponseJson.cert.icon.key))
                    iconEntityBuilder.addPart('token', new StringBody(firResponseJson.cert.icon.token))
                    request.entity = iconEntityBuilder.build()
                    response.success = {
                        println "fir 图片上传成功"
                    }
                    response.failure = {
                        println "fir 图片上传失败"
                    }
                }

                def text = variant.name + "\n" + extension.changeLog.replaceAll("  ", "\n");
                //上传apk
                def http = new HTTPBuilder(firResponseJson.cert.binary.upload_url)
                http.request(Method.POST) { request ->
                    headers.'Accept' = 'application/json'
                    def entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.addBinaryBody('file', apkFile, ContentType.DEFAULT_BINARY, apkFile.getAbsoluteFile().toString())
                    entityBuilder.addPart('key', new StringBody(firResponseJson.cert.binary.key))
                    entityBuilder.addPart('token', new StringBody(firResponseJson.cert.binary.token))
                    if (TextUtils.isEmpty(extension.appName)) {
                        entityBuilder.addPart('x:name', new StringBody(variant.name.capitalize().replace("Release", ""), ContentType.APPLICATION_JSON))
                    } else {
                        entityBuilder.addPart('x:name', new StringBody(extension.appName, ContentType.APPLICATION_JSON))
                    }
                    entityBuilder.addPart('x:version', new StringBody(targetProject.android.defaultConfig.versionName))
                    entityBuilder.addPart('x:build', new StringBody(String.valueOf(targetProject.android.defaultConfig.versionCode)))
                    entityBuilder.addPart('x:changelog', new StringBody(text, ContentType.APPLICATION_JSON))
                    request.entity = entityBuilder.build()
                    response.success = {
                        println "fir apk上传成功"
                    }
                    response.failure = {
                        println "fir apk上传失败"
                    }
                }
            }
            httpResponseScanner.close()
        }

    }
}
