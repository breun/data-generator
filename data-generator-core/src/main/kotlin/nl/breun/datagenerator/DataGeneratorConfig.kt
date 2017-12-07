package nl.breun.datagenerator

import kotlin.reflect.KClass

interface DataGeneratorConfig {

    fun <T : Any> dataClassSupplier(dataClass: KClass<T>): () -> T
    //fun <T : Any> sealedClassSupplier(sealedClass: KClass<T>): () -> T // TODO
    fun <T : Any> enumSupplier(enumClass: KClass<T>): () -> T
    //fun <T : Any> arraySupplier(arrayClass: KClass<T>): () -> T // TODO

    val typeGenerators: Map<KClass<out Any>, () -> Any>
}