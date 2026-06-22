package com.sypark.finnhub.core.websocket

data class SubscriptionDiff(val toAdd: Set<String>, val toRemove: Set<String>)

class SubscriptionRegistry {
    private var activeSymbols: Set<String> = emptySet()

    @Synchronized
    fun diffAndUpdate(desired: Set<String>): SubscriptionDiff {
        val toAdd = desired - activeSymbols
        val toRemove = activeSymbols - desired
        activeSymbols = desired
        return SubscriptionDiff(toAdd, toRemove)
    }

    @Synchronized
    fun currentSymbols(): Set<String> = activeSymbols
}
