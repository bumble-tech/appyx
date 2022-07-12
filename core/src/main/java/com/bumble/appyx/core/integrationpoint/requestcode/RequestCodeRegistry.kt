package com.bumble.appyx.core.integrationpoint.requestcode

import android.os.Bundle
import kotlin.math.pow

/**
 * Provides request code generation and lookup.
 *
 * Clients can use this to create a request code unique to the RequestCodeRegistry
 * instance (i.e. the whole app if the same instance is reused) while providing only:
 *  - locally unique request code
 *  - globally unique string identifier
 *
 * The idea is that you shouldn't have to come up with "random" numbers for request codes in your
 * components. Instead, use sensible numbers starting from 1, only unique locally to your component,
 * and let this class do the rest to ensure global uniqueness.
 */
class RequestCodeRegistry constructor(
    initialState: Bundle?,
    private val nbLowerBitsForIds: Int = 4
) {
    internal val requestCodes: HashMap<Int, String> =
        (initialState?.getSerializable(KEY_REQUEST_CODE_REGISTRY) as? HashMap<Int, String>)
            ?: hashMapOf()

    private val lowerBitsShift: Int = nbLowerBitsForIds - 0
    private val maskLowerBits = (1 shl lowerBitsShift) - 1
    private val maskHigherBits = 0x0000FFFF - maskLowerBits

    init {
        if (nbLowerBitsForIds < 1) throw IllegalArgumentException("nbLowerBitsForIds can't be less than 1")
        if (nbLowerBitsForIds > 4) throw IllegalArgumentException("nbLowerBitsForIds can't be larger than 4")
    }

    fun generateGroupId(groupName: String): Int {
        var code = generateInitialCode(groupName)

        while (codeCollisionWithAnotherGroup(code, groupName)) {
            code += (1 shl lowerBitsShift) and 0x0000FFFF
        }

        requestCodes[code] = groupName

        return code
    }

    internal fun generateInitialCode(groupName: String) =
        (groupName.hashCode() shl lowerBitsShift) and 0x0000FFFF

    private fun codeCollisionWithAnotherGroup(code: Int, groupName: String) =
        requestCodes.containsKey(code) && requestCodes[code] != groupName

    fun generateRequestCode(groupName: String, code: Int): Int {
        ensureCodeIsCorrect(code)
        return generateGroupId(groupName) + (code and maskLowerBits)
    }

    private fun ensureCodeIsCorrect(code: Int) {
        if (code < 1 || code != code and maskLowerBits) {
            throw RequestCodeDoesntFitInMask(
                "Requestcode '$code' does not fit requirements. Allowed min: 1, max: ${
                    2.0.pow(
                        nbLowerBitsForIds
                    ).toInt() - 1
                }"
            )
        }
    }

    fun resolveGroupId(code: Int): Int =
        code and maskHigherBits

    fun resolveRequestCode(code: Int): Int =
        code and maskLowerBits

    fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(KEY_REQUEST_CODE_REGISTRY, HashMap(requestCodes))
    }

    companion object {
        internal const val KEY_REQUEST_CODE_REGISTRY = "requestCodeRegistry"
    }
}
