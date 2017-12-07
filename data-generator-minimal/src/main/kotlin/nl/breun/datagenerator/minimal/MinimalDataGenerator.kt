package nl.breun.datagenerator.minimal

import nl.breun.datagenerator.DataGeneratorConfig
import nl.breun.datagenerator.generate
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Generates a minimal instance for this class.
 *
 * - Data class: only fields which are both required and non-nullable are set. Required nullable fields are set to 'null'. For fields with default values the no alternative value is generated.
 * - Collection/sequence: an empty collection/sequence is returned.
 * - Primitive: a minimal/zero value is returned.
 *
 * Also popular classes from java.math.* and java.time.* are supported.
 *
 * Throws IllegalArgumentException when unable to generate an instance.
 */
fun <T : Any> KClass<T>.minimal(): T = generate(MinimalDataGeneratorConfig)

private object MinimalDataGeneratorConfig : DataGeneratorConfig {

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
                                            else -> (type.classifier as KClass<*>).minimal()
                                        }
                                        else -> throw IllegalArgumentException("Cannot create value for data class parameter $it")
                                    }
                                }
                        )
                primaryConstructor.callBy(args)
            }

    /**
     * Picks the first value of an enum class.
     */
    override fun <T : Any> enumSupplier(enumClass: KClass<T>) = { enumClass.java.enumConstants[0] }

    override val typeGenerators: Map<KClass<out Any>, () -> Any>
        get() = mapOf(
                // Built-ins
                Boolean::class to { false },
                Byte::class          to { 0.toByte() },
                Char::class          to { 0.toChar() },
                Double::class        to { 0.toDouble() },
                Float::class         to { 0.toFloat() },
                Int::class           to { 0 },
                Long::class          to { 0.toLong() },
                Short::class         to { 0.toShort() },
                String::class        to { "_" }, // Empty string would be even more minimal, but is often handled the same as a missing value

                // kotlin.collections.*
                List::class          to { emptyList<Any>() },
                Map::class           to { emptyMap<Any, Any>() },
                Set::class           to { emptySet<Any>() },

                // kotlin.sequences.*
                Sequence::class      to { emptySequence<Any>() },

                // java.math.*
                BigDecimal::class    to { 0.toBigDecimal() },
                BigInteger::class    to { 0.toBigInteger() },

                // java.time.*
                Duration::class      to { Duration.ZERO },
                Instant::class       to { Instant.MIN },
                LocalDate::class     to { LocalDate.MIN },
                LocalDateTime::class to { LocalDateTime.MIN },
                LocalTime::class     to { LocalTime.MIN },
                MonthDay::class      to { MonthDay.of(Month.JANUARY, 1) },
                OffsetTime::class    to { OffsetTime.MIN },
                Period::class        to { Period.ZERO },
                Year::class          to { Year.MIN_VALUE },
                YearMonth::class     to { YearMonth.of(Year.MIN_VALUE, Month.JANUARY) },
                ZonedDateTime::class to { ZonedDateTime.ofInstant(Instant.MIN, ZoneId.systemDefault()) }
        )
}