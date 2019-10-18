package com.vrgsoft.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.vrgsoft.annotations.CreateFactory
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class CreateFactoryProcessor : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(CreateFactory::class.java).forEach { element ->
            if (element.kind != ElementKind.CLASS) {
                processingEnv.messager.errormessage { "Can only be applied to classes,  element: $element " }
                return false
            }

            val typeElement = element as TypeElement

            generateNewMethod(typeElement, processingEnv.elementUtils.getPackageOf(element).toString())
        }

        return false
    }

    private fun generateNewMethod(element: TypeElement, packageOfMethod: String) {
        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.errormessage { "Can't find the target directory for generated Kotlin files." }
            return
        }

        val fileName = "${element.simpleName}Factory"

        var params = ""

        val viewModel = element.asClassName()
        val base = ClassName("com.vrgsoft.core.presentation.fragment", "BaseFactory")
        val baseFactory = base.parameterizedBy(viewModel)

        val factoryTypeBuilder = TypeSpec.classBuilder(fileName)
                .superclass(baseFactory)

        val factoryConstructorBuilder = FunSpec.constructorBuilder()

        element.enclosedElements.forEach {
            if (it.kind == ElementKind.CONSTRUCTOR) {
                val constructr = it as ExecutableElement
                constructr.parameters.forEach {
                    val name = it.simpleName.toString()
                    val type = it.asType().asTypeName()

                    val javaStringName = java.lang.String::class.java.name

                    val resultType = if (type.toString() == javaStringName) {
                        ClassName("kotlin", "String")
                    } else type

                    factoryConstructorBuilder.addParameter(name, resultType)
                    factoryTypeBuilder.addProperty(
                            PropertySpec.builder(name, resultType)
                                    .apply { modifiers.add(KModifier.PRIVATE) }
                                    .initializer(name)
                                    .build()
                    )
                    params = "$params$name, "
                }
            }
        }

        if (params.isNotEmpty()) {
            params = params.substring(0, params.length - 2)
        }

        val file = File(generatedSourcesRoot)
        file.mkdir()

        FileSpec.builder(packageOfMethod, fileName)
                .addType(
                        factoryTypeBuilder
                                .primaryConstructor(factoryConstructorBuilder.build())
                                .addFunction(
                                        FunSpec.builder("createViewModel")
                                                .addModifiers(KModifier.OVERRIDE)
                                                .returns(viewModel)
                                                .addStatement(
                                                        "return %L($params)",
                                                        viewModel.simpleName
                                                )
                                                .build()
                                )
                                .build()
                )
                .build()
                .writeTo(file)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(CreateFactory::class.java.canonicalName)
    }
}