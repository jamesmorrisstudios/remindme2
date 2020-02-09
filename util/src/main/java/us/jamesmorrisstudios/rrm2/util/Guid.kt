package us.jamesmorrisstudios.rrm2.util

import androidx.room.TypeConverter
import java.util.*

/**
 * Constant separating the base guid from the modifier.
 */
private const val ModConst = "-MOD-"

/**
 * Random guid with support for various variations that provide additional information.
 *
 * A guid is either the base v4 UUID guid or it is the base guid plus an additional modifier used for a specific purpose.
 * A guid with a modifier can always be returned to the base guid.
 * Only a single modifier can be present on a guid.
 *
 * Format
 *   base: 2dc631d5-b6bb-457f-b5a2-de0b7400456d
 *   with modifier: 2dc631d5-b6bb-457f-b5a2-de0b7400456d-MOD-modifier
 */
data class Guid(private val value: String = "") {

    companion object {

        /**
         * Generates a new base guid.
         */
        fun generate(): Guid {
            return Guid(UUID.randomUUID().toString())
        }

        /**
         * Creates a Guid from an existing string representation.
         *
         * Note: This must have come from the Guid.generate method initially.
         */
        fun fromString(string: String): Guid {
            return Guid(string)
        }

    }

    /**
     * Returns if this is a base guid or if it has a modifier set.
     */
    fun isBase(): Boolean {
        return !this.value.contains(ModConst)
    }

    /**
     * Returns the base version of this guid.
     */
    fun getBase(): Guid {
        if (isBase()) {
            return this
        }
        val base = this.value.split(ModConst).firstOrNull() ?: this.value
        return Guid(base)
    }

    /**
     * Returns if the given modifier is present on the guid.
     */
    fun isModifier(modifier: String): Boolean {
        if (isBase()) {
            return false
        }
        return this.value.split(ModConst).lastOrNull() == modifier
    }

    /**
     * Creates a new Guid given the current guid but with the given modifier.
     *
     * This modifier should be alphanumeric and not contain any dashes.
     */
    fun withModifier(modifier: String): Guid {
        check(!modifier.contains("-"))
        val base = getBase().value
        return Guid(base + ModConst + modifier)
    }

    /**
     * Returns the value of the Guid.
     */
    override fun toString(): String {
        return value
    }

}

/**
 * Creates a guid from an existing string representation of that guid.
 */
fun String.toGuid(): Guid {
    return Guid.fromString(this)
}

/**
 * Type converter for storing guids in the database
 */
class GuidDbTypeConverter {

    @TypeConverter
    fun toGuidType(string: String): Guid {
        return string.toGuid()
    }

    @TypeConverter
    fun toStringType(guid: Guid): String {
        return guid.toString()
    }

}
