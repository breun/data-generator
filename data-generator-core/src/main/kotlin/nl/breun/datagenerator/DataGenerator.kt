package nl.breun.datagenerator

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance

/**
 * Generates an instance of this class using the supplied {@link DataGeneratorConfig}.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.generate(config: DataGeneratorConfig): T = when {
    isData -> config.dataClassSupplier(this).invoke()
    //isSealed -> config.sealedClassSupplier(this).invoke() // TODO: How to get subclasses of sealed class?
    java.isEnum -> config.enumSupplier(this).invoke()
    //java.isArray -> config.arraySupplier(this).invoke() // TODO: How to generically create array instances?
    config.typeGenerators.containsKey(this) -> config.typeGenerators[this]!!.invoke() as T
    hasPublicNoArgConstructor() -> createInstance()
    else -> throw IllegalArgumentException("Sorry, DataGenerator cannot create instances of $this (yet?)")
}

private fun <T : Any> KClass<T>.hasPublicNoArgConstructor() = constructors.any { it.visibility == KVisibility.PUBLIC && it.parameters.isEmpty() }