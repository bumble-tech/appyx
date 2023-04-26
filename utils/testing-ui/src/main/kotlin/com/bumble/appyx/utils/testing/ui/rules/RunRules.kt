package com.bumble.appyx.utils.testing.ui.rules

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

internal class RunRules(
    base: Statement,
    rules: Iterable<TestRule>,
    description: Description,
    private val before: () -> Unit = {},
    private val after: () -> Unit= {}
) : Statement() {

    private val statement: Statement = applyAll(base, rules, description)

    @Throws(Throwable::class)
    override fun evaluate() {
        try {
            before.invoke()
            statement.evaluate()
        } finally {
            after.invoke()
        }
    }

    private fun applyAll(
        result: Statement,
        rules: Iterable<TestRule>,
        description: Description
    ): Statement {
        var result = result
        for (each in rules) {
            result = each.apply(result, description)
        }
        return result
    }
}
