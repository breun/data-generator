package nl.breun.datagenerator.random

import nl.breun.datagenerator.DataGeneratorConfig
import nl.breun.datagenerator.generate
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

fun <T : Any> KClass<T>.random(): T = generate(RandomDataGeneratorConfig)

private object RandomDataGeneratorConfig : DataGeneratorConfig {

    override fun <T : Any> dataClassSupplier(dataClass: KClass<T>) =
            {
                val primaryConstructor: KFunction<T> = dataClass.primaryConstructor!! // Data classes always have a primary constructor
                val args: Map<KParameter, Any?> = primaryConstructor.parameters
                        .filter { !it.isOptional }
                        .associateBy(
                                keySelector = { it },
                                valueTransform = {
                                    val type = it.type
                                    when {
                                        type.isMarkedNullable -> null // Keep it minimal
                                        type.classifier is KClass<*> -> when {
                                            type.classifier == it -> throw IllegalArgumentException("Cannot create value for recursive definition of $dataClass") // Simple endless recursion (stack overflow) protection
                                            else -> (type.classifier as KClass<*>).random()
                                        }
                                        else -> throw IllegalArgumentException("Cannot create value for data class parameter $it")
                                    }
                                }
                        )
                primaryConstructor.callBy(args)
            }

    /**
     * Picks a random value of an enum class.
     */
    override fun <T : Any> enumSupplier(enumClass: KClass<T>) =
            {
                enumClass.java.enumConstants.random()
                        ?: throw RuntimeException("An enum class without values?!")
            }

    override val typeGenerators: Map<KClass<out Any>, () -> Any>
        get() = mapOf(
                // Built-ins

                // kotlin.collections.*

                // kotlin.sequences.*

                // java.math.*

                // java.time.*
        )
}