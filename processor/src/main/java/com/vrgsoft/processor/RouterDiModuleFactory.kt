package com.vrgsoft.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.vrgsoft.annotations.RouterDiModule
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class RouterDiModuleFactory() : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(RouterDiModule::class.java).forEach { element ->
            if (element.kind != ElementKind.CLASS) {
                processingEnv.messager.errormessage { "Can only be applied to classes,  element: $element " }
                return false
            }

            val typeElement = element as TypeElement

            generateNewMethod(
                element = typeElement,
                packageOfMethod = processingEnv.elementUtils.getPackageOf(element).toString()
            )
        }

        return false
    }

    private fun generateNewMethod(
        element: TypeElement,
        packageOfMethod: String
    ) {
        val generatedSourcesRoot: String =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val routerName = element.simpleName
        val baseName = routerName.removeSuffix("Router")
        val fileName = "${routerName}Module"

        var params = ""

        val moduleTypeBuilder = TypeSpec.objectBuilder(fileName)

        element.enclosedElements.forEach {
            if (it.kind == ElementKind.CONSTRUCTOR) {
                val constructor = it as ExecutableElement
                constructor.parameters.forEach { _ ->
                    params += "instance(), "
                }
            }
        }

        if (params.isNotEmpty()) {
            params = params.substring(0, params.length - 2)
        }

        val file = File(generatedSourcesRoot)
        file.mkdir()

        FileSpec.builder(packageOfMethod, fileName)
            .addImport("org.kodein.di", "Kodein")
            .addImport("org.kodein.di.generic", "bind")
            .addImport("org.kodein.di.generic", "singleton")
            .addImport("org.kodein.di.generic", "provider")
            .addImport("org.kodein.di.generic", "instance")
            .addImport("com.vrgsoft.core.presentation.router", "ActivityRouter")
            .addType(
                moduleTypeBuilder
                    .addFunction(
                        FunSpec.builder("get")
                            .addCode(
                                "return Kodein.Module(\"%L\")",
                                "$baseName.RouterModule"
                            )
                            .addCode("{\n")
                            .addCode(
                                "bind() from singleton { %L(%L) }\n",
                                routerName,
                                params
                            )
                            .addCode("}\n")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("getAsActivityRouter")
                            .addCode(
                                "return Kodein.Module(\"%L\")",
                                "$baseName.ActivityRouterModule"
                            )
                            .addCode("{\n")
                            .addCode(
                                "bind<ActivityRouter>() with provider { instance<%L>() }\n",
                                routerName
                            )
                            .addCode("}\n")
                            .build()
                    )
                    .build()
            )
            .build()
            .writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(RouterDiModule::class.java.canonicalName)
    }
}