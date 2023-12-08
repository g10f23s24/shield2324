package com.trust.home.security.etx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow

fun <T> collectNotNull(
    lifecycleOwner: LifecycleOwner?,
    flow: Flow<T?>,
    action: (T) -> Unit
) {
    lifecycleOwner?.collectNotNullInternal(flow, action)
}

private fun <T> LifecycleOwner.collectNotNullInternal(
    flow: Flow<T?>,
    action: (T) -> Unit
) {
    lifecycleScope.launchWhenStarted {
        flow.collect {
            it ?: return@collect
            action.invoke(it)
        }
    }
}