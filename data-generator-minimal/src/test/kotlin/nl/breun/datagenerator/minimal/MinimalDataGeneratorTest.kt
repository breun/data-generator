package nl.breun.datagenerator.minimal

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MinimalDataGeneratorTest {

    // Data classes

    @Test
    fun `Should be able to generate a data class`() {
        assertThat(DataClass::class.minimal()).isInstanceOf(DataClass::class.java)
    }

    @Test
    fun `Should set required nullable field of data class to null`() {
        assertThat(DataClassWithRequiredNullableField::class.minimal().field).isNull()
    }

    @Test
    fun `Should use default value for data class field with default value`() {
        assertThat(DataClassWithFieldWithDefaultValue::class.minimal().field).isEqualTo("default")
    }

    @Test
    fun `Should use default value for nullable data class field with default value`() {
        assertThat(DataClassWithNullableFieldWithDefaultValue::class.minimal().field).isEqualTo("default")
    }

    @Test
    fun `Should be able to generate data classes containing other data classes`() {
        assertThat(ContainerDataClass::class.minimal().field).isInstanceOf(DataClass::class.java)
    }

    // Enum classes

    @Test
    fun `Should pick first enum value`() {
        assertThat(EnumClass::class.minimal()).isEqualTo(EnumClass.FIRST)
    }

    // Primitives

    @Test
    fun `Boolean should be false`() {
        assertThat(Boolean::class.minimal()).isFalse()
    }

    @Test
    fun `Byte should be 0`() {
        assertThat(Byte::class.minimal()).isEqualTo(0.toByte())
    }

    @Test
    fun `Int should be 0`() {
        assertThat(Int::class.minimal()).isZero()
    }

    @Test
    fun `String should be _`() {
        assertThat(String::class.minimal()).isEqualTo("_")
    }

    @Test
    fun `List should be empty`() {
        assertThat(List::class.minimal()).isEmpty()
    }

    @Test
    fun `Map should be empty`() {
        assertThat(Map::class.minimal()).isEmpty()
    }

    @Test
    fun `Set should be empty`() {
        assertThat(Set::class.minimal()).isEmpty()
    }

    @Test
    fun `Sequence should be empty`() {
        assertThat(Sequence::class.minimal().count()).isZero()
    }
}

data class DataClass(
        val field: String
)

data class DataClassWithRequiredNullableField(
        val field: String?
)

data class DataClassWithFieldWithDefaultValue(
        val field: String = "default"
)

data class DataClassWithNullableFieldWithDefaultValue(
        val field: String? = "default"
)

data class ContainerDataClass(
        val field: DataClass
)

enum class EnumClass {
    FIRST, SECOND
}