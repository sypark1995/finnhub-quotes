package com.sypark.finnhub.core.common

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-process-lifetime coroutine scope for singletons that must outlive any single
 * ViewModel (e.g. the WebSocket connection loop, Task 18). A SupervisorJob means one
 * child's failure never cancels the others or the scope itself. A CoroutineExceptionHandler
 * is installed so an uncaught child failure is swallowed here instead of escaping to the
 * dispatcher's underlying thread (which would otherwise crash the process on Android, or
 * fail an unrelated test on the JVM). Never use GlobalScope — inject this instead, so tests
 * can substitute a TestScope-backed AppDispatchers.
 */
@Singleton
class AppCoroutineScope @Inject constructor(
    dispatchers: AppDispatchers,
) : CoroutineScope by CoroutineScope(
    SupervisorJob() + dispatchers.default + CoroutineExceptionHandler { _, _ -> },
)
