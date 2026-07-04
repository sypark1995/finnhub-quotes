package com.sypark.finnhub.core.common

import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppCoroutineScopeTest {

    @Test
    fun `scope stays active after a child coroutine throws`() = runTest {
        val scope = AppCoroutineScope(AppDispatchers())
        var secondRan = false

        scope.launch { throw IllegalStateException("boom") }
        scope.launch { secondRan = true }.join()

        assertTrue(scope.isActive)
        assertTrue(secondRan)
    }
}
