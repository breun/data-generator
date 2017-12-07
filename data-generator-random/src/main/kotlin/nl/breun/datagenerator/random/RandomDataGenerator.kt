package nl.breun.datagenerator.random

import io.github.benas.randombeans.api.EnhancedRandom.random
import nl.breun.datagenerator.DataGeneratorConfig
import nl.breun.datagenerator.generate
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
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
                Boolean::class    to { random(Boolean::class.java) },
                Byte::class              to { random(Byte::class.java) },
                Char::class              to { random(Char::class.java) },
                Double::class            to { random(Double::class.java) },
                Float::class             to { random(Float::class.java) },
                Int::class               to { random(Int::class.java) },
                Long::class              to { random(Long::class.java) },
                Short::class             to { random(Short::class.java) },
                String::class            to { random(String::class.java) },

                // TODO: How can we know about generic type parameter and generate non-empty collections/sequences/arrays?
                // kotlin.collections.*
                List::class              to { emptyList<Any>() },
                Map::class               to { emptyMap<Any, Any>() },
                Set::class               to { emptySet<Any>() },

                // kotlin.sequences.*
                Sequence::class          to { emptySequence<Any>() },

                // java.math.*
                BigDecimal::class        to { random(BigDecimal::class.java) },
                BigInteger::class        to { random(BigInteger::class.java) },

                // java.net.*
                URI::class               to { random(URI::class.java) },
                URL::class               to { random(URL::class.java) },

                // java.sql.*
                Time::class              to { random(Time::class.java) },
                Timestamp::class         to { random(Timestamp::class.java) },

                // java.time.*
                Duration::class          to { random(Duration::class.java) },
                Instant::class           to { random(Instant::class.java) },
                LocalDate::class         to { random(LocalDate::class.java) },
                LocalDateTime::class     to { random(LocalDateTime::class.java) },
                LocalTime::class         to { random(LocalTime::class.java) },
                MonthDay::class          to { random(MonthDay::class.java) },
                OffsetDateTime::class    to { random(OffsetDateTime::class.java) },
                OffsetTime::class        to { random(OffsetTime::class.java) },
                Period::class            to { random(Period::class.java) },
                Year::class              to { random(Year::class.java) },
                YearMonth::class         to { random(YearMonth::class.java) },
                ZonedDateTime::class     to { random(ZonedDateTime::class.java) },
                ZoneOffset::class        to { random(ZoneOffset::class.java) },

                // java.util.*
                Calendar::class          to { random(Calendar::class.java) },
                Date::class              to { random(Date::class.java) },
                GregorianCalendar::class to { random(GregorianCalendar::class.java) },
                Queue::class             to { random(Queue::class.java) },
                UUID::class              to { random(UUID::class.java) },

                // java.util.concurrent.atomic.*
                AtomicInteger::class     to { random(AtomicInteger::class.java) },
                AtomicLong::class        to { random(AtomicLong::class.java) }
        )
}