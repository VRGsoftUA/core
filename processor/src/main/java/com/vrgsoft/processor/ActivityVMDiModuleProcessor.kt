package com.vrgsoft.processor

import com.google.auto.service.AutoService
import com.vrgsoft.annotations.ActivityVMDiModule
import com.vrgsoft.processor.common.BaseVMDiModuleProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ActivityVMDiModuleProcessor : BaseVMDiModuleProcessor(
    annotationClass = ActivityVMDiModule::class.java,
    target = "Activity",
    targetParameterName = "activity"
)