package com.czqr.apktofir.lib

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 实现一键打包上传fir后，钉钉通知所有人
 */
class AssembleHelperPlugin implements Plugin<Project> {

    public static final String EXT_NAME = "qxUpload";

    @Override
    void apply(Project project) {
        //创建扩展对象
        project.extensions.create(EXT_NAME, Extension, project)
        println "创建扩展对象完成"
        //自定义任务
        project.afterEvaluate {
            //遍历所有变体，给所有变体创建task
            (project.extensions.findByName("android") as AppExtension).getApplicationVariants().each { variant ->
                println variant
                println variant instanceof BaseVariant

                def variantName = variant.name.capitalize();
                println variantName

                //创建上传task
                FirUploadTask firUploadTask = project.tasks.create("firUpload${variantName}",FirUploadTask)
                firUploadTask.variant = variant
                firUploadTask.targetProject = project
                firUploadTask.setup()

                println "上传任务创建完"

                SendMsgToDingTask dingTask = project.tasks.create("sendMsgToDing${variantName}", SendMsgToDingTask)
                dingTask.variant = variant
                dingTask.targetProject = project
                dingTask.setup()

                println "钉钉任务创建完"

                println variant.getAssembleProvider().get()
                //dependsOn依赖assemble任务，意思是当我们执行上传任务，gradle会先执行打包任务
                firUploadTask.dependsOn variant.getAssembleProvider().get();
                //执行钉钉任务前会先执行上传fir任务
                dingTask.dependsOn firUploadTask

            }

        }
    }
}