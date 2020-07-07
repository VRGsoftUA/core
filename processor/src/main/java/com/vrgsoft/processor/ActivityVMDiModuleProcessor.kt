package com.vrgsoft.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.vrgsoft.annotations.ActivityVMDiModule
import com.vrgsoft.processor.common.BaseVMDiModuleProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ActivityVMDiModuleProcessor : BaseVMDiModuleProcessor(
    annotationClass = ActivityVMDiModule::class.java,
    target = "Activity",
    targetParameterName = "activity"
) {

    private inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> Boolean) =
        try {
            getAnnotation(T::class.java).f()
            throw Exception("Expected to get a MirroredTypeException")
        } catch (e: MirroredTypeException) {
            e.typeMirror
        }

    override fun FileSpec.Builder.addProviderImports(element: TypeElement): FileSpec.Builder {
        try {
            element.getAnnotationClassValue<ActivityVMDiModule> {
                provideRouter.also {
                    return if (it) {
                        this@addProviderImports.addImport(
                            "com.vrgsoft.core.presentation.router",
                            "ActivityRouter"
                        )
                    } else {
                        this@addProviderImports
                    }
                }
            }
        } catch (e: Exception) {
            return this
        }
        return this
    }

    override fun FunSpec.Builder.addProvideBloc(
        element: TypeElement,
        baseName: String
    ): FunSpec.Builder {
        try {
            element.getAnnotationClassValue<ActivityVMDiModule> {
                provideRouter.also {
                    return if (it) {
                        this@addProvideBloc.addCode(
                            "bind<ActivityRouter>() with provider { instance<%LRouter>() }\n",
                            baseName
                        )
                    } else {
                        this@addProvideBloc
                    }
                }
            }
        } catch (e: Exception) {
            return this
        }
        return this
    }
}