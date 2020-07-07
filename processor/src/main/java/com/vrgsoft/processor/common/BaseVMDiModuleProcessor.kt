package com.vrgsoft.processor.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.vrgsoft.processor.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.vrgsoft.processor.errormessage
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

abstract class BaseVMDiModuleProcessor(
    private val annotationClass: Class<out Annotation>,
    private val target: String,
    private val targetParameterName: String
) : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(annotationClass).forEach { element ->
            if (element.kind != ElementKind.CLASS) {
                processingEnv.messager.errormessage { "Can only be applied to classes,  element: $element " }
                return false
            }

            val typeElement = element as TypeElement

            generateNewMethod(
                typeElement,
                processingEnv.elementUtils.getPackageOf(element).toString()
            )
        }

        return false
    }

    private fun generateNewMethod(element: TypeElement, packageOfMethod: String) {
        val generatedSourcesRoot: String =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val viewModelName = element.simpleName
        val baseName = viewModelName.removeSuffix("ViewModel")
        val targetName = "${baseName}$target"
        val fileName = "${viewModelName}Module"
        val contractName = "${baseName}Contract"

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
            .addImport("androidx.lifecycle", "ViewModelProvider")
            .addType(
                moduleTypeBuilder
                    .addFunction(
                        FunSpec.builder("get")
                            .addParameter(
                                targetParameterName,
                                ClassName(packageOfMethod, targetName)
                            )
                            .addCode(
                                "return Kodein.Module(\"%L\")",
                                baseName
                            )
                            .addCode("{\n")
                            .addCode(
                                "bind<ViewModelProvider.Factory>(tag = \"%L\") with provider { %LFactory(%L) }\n",
                                baseName,
                                viewModelName,
                                params
                            )
                            .addCode(
                                "bind<%L.ViewModel>() with provider { fragment.vm<%L>(instance(tag = \"%L\")) }\n",
                                contractName,
                                viewModelName,
                                baseName
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
        return mutableSetOf(annotationClass.canonicalName)
    }
}